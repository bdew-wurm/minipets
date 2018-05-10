package net.bdew.wurm.minipets.actions;

import com.wurmonline.server.Items;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import net.bdew.wurm.minipets.MiniPets;
import net.bdew.wurm.minipets.PetItems;
import net.bdew.wurm.minipets.PetTracker;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.Collections;
import java.util.List;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.*;

public class ConvertPetAction implements ModAction, ActionPerformer, BehaviourProvider {
    private ActionEntry actionEntry;

    public ConvertPetAction() {
        actionEntry = ActionEntry.createEntry((short) ModActions.getNextActionId(), "Convert", "converting", new int[]{
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
        return (performer.isPlayer() && target != null
                && (target.getTemplateId() == PetItems.petLeashId || target.getTemplateId() == PetItems.petDecorativeId)
                && !target.isTraded()
                && target.getTopParentOrNull() == performer.getInventory());
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
        Communicator comm = performer.getCommunicator();

        if (!canUse(performer, target)) {
            comm.sendAlertServerMessage("You can't do that now.");
            return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
        }

        try {
            if (target.getTemplateId() == PetItems.petLeashId) {
                Item pet = ItemFactory.createItem(PetItems.petDecorativeId, 99, target.getRarity(), performer.getName());
                Item parent = target.getParent();
                pet.setAuxData(target.getAuxData());
                pet.setData(target.getData());
                pet.setName(target.getDescription());
                pet.setDescription(performer.getName().toLowerCase() + "'s pet");
                parent.insertItem(pet, true, false);
                Long summonedId = PetTracker.leashesToPets.remove(target.getWurmId());
                if (summonedId != null) {
                    try {
                        Creature summonedPet = Creatures.getInstance().getCreature(summonedId);
                        summonedPet.destroy();
                        performer.getCommunicator().sendNormalServerMessage(String.format("%s disappears.", target.getDescription()));
                    } catch (NoSuchCreatureException ignored) {
                    }
                }
                parent.dropItem(target.getWurmId(), false);
                Items.destroyItem(target.getWurmId());
            } else {
                Item leash = ItemFactory.createItem(PetItems.petLeashId, 99, target.getRarity(), performer.getName());
                Item parent = target.getParent();
                leash.setAuxData(target.getAuxData());
                leash.setData(target.getData());
                leash.setDescription(target.getName());
                parent.insertItem(leash, true, false);
                parent.dropItem(target.getWurmId(), false);
                Items.destroyItem(target.getWurmId());
            }
        } catch (Exception e) {
            MiniPets.logException(String.format("Error converting pet %d for player %s (type=%d)", target.getWurmId(), performer.getName(), target.getAuxData()), e);
            comm.sendNormalServerMessage("Something went wrong, try later or open a /support ticket.");
        }

        return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
    }
}
