package net.bdew.wurm.minipets;

import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.zones.VolaTile;

import java.util.List;
import java.util.Random;

public class Hooks {
    private static Random rng = new Random();

    public static boolean isMiniPet(Creature creature) {
        return creature.getTemplate() == PetCreature.template;
    }

    public static String getModel(Creature creature) {
        MiniPetAIData data = MiniPetAIData.get(creature);

        if (data != null && data.getType() != null) {
            List<String> models = data.getType().models;
            if (models.size() == 1)
                return models.get(0);
            rng.setSeed(creature.getWurmId());
            return models.get(rng.nextInt(models.size()));
        } else {
            return "model";
        }
    }

    public static void addCreatureHook(VirtualZone vz, long creatureId) {
        Creature watcher = vz.getWatcher();
        if (watcher != null && watcher.isPlayer() && watcher.hasLink()) {
            try {
                Communicator comm = watcher.getCommunicator();
                Creature creature = Server.getInstance().getCreature(creatureId);
                if (creature.getTemplate() == PetCreature.template) {
                    MiniPetAIData data = MiniPetAIData.get(creature);

                    if (data != null && data.getType() != null) {
                        int scale = (int) (data.getType().scale * 64f);
                        if (scale > 255) scale = 255;
                        if (scale < 0) scale = 0;
                        comm.sendResize(creatureId, (byte) scale, (byte) scale, (byte) scale);
                        data.getType().effects.forEach(eff -> eff.doSend(comm, creature));
                    }

                }
            } catch (NoSuchPlayerException | NoSuchCreatureException ignored) {

            }
        }
    }

    public static void sendItemHook(Communicator comm, Item item) {
        if (item.getTemplateId() == PetItems.petEggId && (item.lastOwner == -10L || item.lastOwner == 0L)) {
            rng.setSeed(item.getWurmId());
            int color = rng.nextInt();
            int r = 255, g = 255, b = 255;
            switch (rng.nextInt(3)) {
                case 0:
                    g = rng.nextInt(256);
                    b = rng.nextInt(256);
                    break;
                case 1:
                    r = rng.nextInt(256);
                    b = rng.nextInt(256);
                    break;
                case 2:
                    r = rng.nextInt(256);
                    g = rng.nextInt(256);
                    break;

            }
            comm.sendAttachEffect(item.getWurmId(), (byte) 4, (byte) r, (byte) g, (byte) b, (byte) 127);
            comm.sendRepaint(item.getWurmId(), (byte) r, (byte) g, (byte) b, (byte) -1, (byte) 0);
            comm.sendAddEffect(item.getWurmId(), item.getWurmId(), (short) 27, item.getPosX(), item.getPosY(), item.getPosZ(), (byte) 0, "lightningBall_1_1", Float.MAX_VALUE, 0f);
        }
    }


    public static void removeItemHook(Communicator comm, Item item) {
        if (item.getTemplateId() == PetItems.petEggId && (item.lastOwner == -10L || item.lastOwner == 0L)) {
            comm.sendRemoveEffect(item.getWurmId());
        }
    }

    public static boolean checkVisibilityOverride(Creature watcher, Item item, VolaTile tile) {
        return (item.getTemplateId() == PetItems.petEggId) && (watcher.isWithinDistanceTo(item, 50f));
    }
}
