package net.bdew.wurm.minipets;

import com.wurmonline.math.Vector2f;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.ai.scripts.UtilitiesAOE;
import com.wurmonline.server.support.JSONArray;

import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils {
    public static void blinkToCreature(Creature creature, Creature target) {
        creature.getCurrentTile().deleteCreatureQuick(creature);
        creature.setLayer(target.getLayer(), false);
        creature.setPositionX(target.getPosX());
        creature.setPositionY(target.getPosY());
        creature.setPositionZ(target.getPositionZ());
        creature.setBridgeId(target.getBridgeId());
        creature.pushToFloorLevel(target.getFloorLevel());
        try {
            target.getCurrentTile().getZone().addCreature(creature.getWurmId());
        } catch (NoSuchCreatureException | NoSuchPlayerException e) {
            MiniPets.logException("Error adding creature to zone", e);
        }
    }

    public static Creature createPet(Creature performer, int type, String name) throws Exception {
        Vector2f newPos = UtilitiesAOE.getPointInFrontOf(performer, 1);

        PetType petType = PetTypes.get(type);
        String fullName;

        if (name != null && name.length() > 0) {
            fullName = name + " (" + performer.getName().toLowerCase() + "'s pet)";
        } else {
            fullName = petType.name + " (" + performer.getName().toLowerCase() + "'s pet)";
            name = petType.name;
        }

        try {
            Long oldPet = PetTracker.playerPets.remove(performer.getWurmId());
            if (oldPet != null) {
                Creature oldPetObj = Creatures.getInstance().getCreature(oldPet);
                oldPetObj.destroy();
            }
        } catch (NoSuchCreatureException ignored) {
        }

        Creature summoned = Creature.doNew(PetCreature.templateId, newPos.x, newPos.y, performer.getPositionZ(), performer.getLayer(), name, (byte) 0);

        PetTracker.playerPets.put(performer.getWurmId(), summoned.getWurmId());

        summoned.setVisible(false);

        summoned.setName(fullName);
        summoned.setBridgeId(performer.getBridgeId());
        summoned.pushToFloorLevel(performer.getFloorLevel());

        MiniPetAIData data = MiniPetAIData.get(summoned);
        data.setOwner(performer);
        data.setType(petType);

        summoned.setVisible(true);

        return summoned;
    }

    public static <R> Stream<R> streamJSArray(JSONArray array, BiFunction<JSONArray, Integer, R> getter) {
        return IntStream.range(0, array.length()).mapToObj(i -> getter.apply(array, i));
    }
}
