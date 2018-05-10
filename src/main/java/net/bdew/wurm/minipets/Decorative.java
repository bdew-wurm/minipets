package net.bdew.wurm.minipets;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.Item;
import net.bdew.wurm.minipets.effects.ICreatureEffect;

import java.util.Random;

public class Decorative {
    private static Random rng = new Random();

    static void sendDecorative(Communicator comm, Item item, float x, float y, float z, float rot) {
        PetType type = PetTypes.get(item.getAuxData());

        String model = "model.";

        if (type != null) {
            if (type.models.size() == 1)
                model = type.models.get(0);
            rng.setSeed(item.getWurmId());
            model = type.models.get(rng.nextInt(type.models.size()));
        }

        comm.sendNewCreature(
                item.getWurmId(),
                String.format("%s (%s)", item.getName(), item.getDescription()),
                model,
                x,
                y,
                z,
                item.getBridgeId(),
                rot,
                (byte) (item.isOnSurface() ? 0 : -1),
                false,
                false,
                false,
                (byte) (item.getData2() & 0xFF),
                0L,
                (byte) 0,
                false,
                false,
                (byte) 0
        );

        comm.setCreatureDamage(item.getWurmId(), 100f);

        if (type != null) {
            Hooks.sendAdditionalStuff(comm, item.getWurmId(), type, true);
            if (type.effects.stream().anyMatch(ICreatureEffect::needToRefreshOnItems)) {
                EffectTracker.addItem(comm.getPlayer(), item.getWurmId(), type);
            }
        }
    }

    static void removeDecorative(Communicator comm, Item item) {
        comm.sendDeleteCreature(item.getWurmId());
        EffectTracker.removeItem(comm.getPlayer(), item.getWurmId());
    }
}
