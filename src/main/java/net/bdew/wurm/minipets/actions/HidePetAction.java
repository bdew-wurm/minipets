package net.bdew.wurm.minipets.actions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.items.Item;
import net.bdew.wurm.minipets.PetItems;
import net.bdew.wurm.minipets.PetTracker;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.Collections;
import java.util.List;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.*;

public class HidePetAction implements ModAction, ActionPerformer, BehaviourProvider {
    private ActionEntry actionEntry;

    public HidePetAction() {
        actionEntry = ActionEntry.createEntry((short) ModActions.getNextActionId(), "Hide pet", "hiding", new int[]{
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                37 /* ACTION_TYPE_NEVER_USE_ACTIVE_ITEM */
        });
        ModActions.registerAction(actionEntry);
    }

    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }

    @Override
    public BehaviourProvider getBehaviourProvider() {
        return this;
    }

    @Override
    public ActionPerformer getActionPerformer() {
        return this;
    }

    public boolean canUse(Creature performer, Item target) {
        if (!performer.isPlayer() || target == null
                || target.getTemplateId() != PetItems.petLeashId
                || target.isTraded()
                || target.getTopParentOrNull() != performer.getInventory())
            return false;

        if (!PetTracker.leashesToPets.containsKey(target.getWurmId())) return false;

        try {
            Creatures.getInstance().getCreature(PetTracker.leashesToPets.get(target.getWurmId()));
            return true;
        } catch (NoSuchCreatureException e) {
            return false;
        }
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        return getBehavioursFor(performer, target);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
        if (canUse(performer, target))
            return Collections.singletonList(actionEntry);
        else
            return null;
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        return action(action, performer, target, num, counter);
    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        if (!canUse(performer, target)) {
            performer.getCommunicator().sendAlertServerMessage("You can't do that now.");
            return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
        }

        try {
            Long oldId = PetTracker.leashesToPets.remove(target.getWurmId());
            if (oldId != null) {
                Creature oldInstance = Creatures.getInstance().getCreature(oldId);
                oldInstance.destroy();
                performer.getCommunicator().sendNormalServerMessage(String.format("%s disappears.", target.getDescription()));
            }
        } catch (NoSuchCreatureException ignored) {
        }

        return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
    }
}
