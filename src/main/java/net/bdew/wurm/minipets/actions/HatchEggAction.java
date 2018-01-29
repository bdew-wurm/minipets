package net.bdew.wurm.minipets.actions;

import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import net.bdew.wurm.minipets.*;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.Collections;
import java.util.List;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.*;

public class HatchEggAction implements ModAction, ActionPerformer, BehaviourProvider {
    private ActionEntry actionEntry;

    public HatchEggAction() {
        actionEntry = ActionEntry.createEntry((short) ModActions.getNextActionId(), "Hatch", "hatch", new int[]{
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
                && target.getTemplateId() == PetItems.petEggId
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

        if (counter == 1.0f) {
            comm.sendNormalServerMessage(String.format("You start hatching the %s.", target.getName()));
            Server.getInstance().broadCastAction(String.format("%s starts hatching the %s.", performer.getName(), target.getName()), performer, 5);
            action.setTimeLeft(50);
            performer.sendActionControl("hatching", true, action.getTimeLeft());
            performer.playAnimation("use", false);
        } else {
            if (action.justTickedSecond()) {
                switch (action.currentSecond()) {
                    case 2:
                        comm.sendNormalServerMessage("You give the egg a slight tap.");
                        break;
                    case 3:
                        comm.sendNormalServerMessage("Cracks start forming on the surface of the egg.");
                        break;
                    case 4:
                        comm.sendNormalServerMessage("The cracks are running all over the surface.");
                        break;
                    case 5:
                        comm.sendNormalServerMessage("Something moves inside!");
                        break;
                }
                if (counter * 10.0f > action.getTimeLeft()) {
                    PetType petData = PetTypes.getRandomByRarity(target.getRarity());
                    if (petData == null) {
                        comm.sendNormalServerMessage("Something went wrong, and the egg failed to hatch.");
                    } else {
                        try {
                            Item leash = ItemFactory.createItem(PetItems.petLeashId, 99, target.getRarity(), performer.getName());
                            Item parent = target.getParent();
                            parent.dropItem(target.getWurmId(), false);
                            leash.setAuxData((byte) petData.id);
                            leash.setDescription(petData.name);
                            PetTracker.leashesToPets.put(leash.getWurmId(), Utils.createPet(performer, petData.id, petData.name).getWurmId());
                            parent.insertItem(leash, true, false);
                            Items.destroyItem(target.getWurmId());
                        } catch (Exception e) {
                            MiniPets.logException("Error hatching egg", e);
                            comm.sendNormalServerMessage("Something went wrong, and the egg failed to hatch.");
                        }
                    }

                    return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
                }
            }
        }

        return propagate(action, CONTINUE_ACTION);
    }
}
