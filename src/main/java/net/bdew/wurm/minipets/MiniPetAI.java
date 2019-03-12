package net.bdew.wurm.minipets;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.ai.CreatureAI;
import com.wurmonline.server.creatures.ai.CreatureAIData;
import com.wurmonline.server.zones.Zones;

public class MiniPetAI extends CreatureAI {
    private static final double R2D = 57.29577951308232D;
    private static final double D2R = 0.01745329238474369D;

    @Override
    protected boolean pollMovement(Creature creature, long l) {
        MiniPetAIData data = MiniPetAIData.get(creature);
        Creature owner = data.getOwner();

        if (owner == null || owner.currentTile == null || owner.isOffline()) {
            creature.destroy();
            Server.getInstance().broadCastAction(String.format("%s looks lost and confused then disappears!", creature.getName()), creature, 5);
            return true;
        }

        if ((creature.getBridgeId() == -10L && (creature.getFloorLevel() != owner.getFloorLevel())) || (creature.getBridgeId() != owner.getBridgeId())) {
            Utils.blinkToCreature(creature, owner);
        }

        float ownerX = owner.getPosX();
        float ownerY = owner.getPosY();
        float creatureX = creature.getPosX();
        float creatureY = creature.getPosY();
        float creatureZ = creature.getPositionZ();
        float diffX = ownerX - creatureX;
        float diffY = ownerY - creatureY;

        int diff = (int) Math.max(Math.abs(diffX), Math.abs(diffY));
        if ((diffX >= 0.0F || creatureX >= 10.0F) && (diffY >= 0.0F || creatureY >= 10.0F) && (diffX <= 0.0F || creatureX <= Zones.worldMeterSizeX - 10.0F) && (diffY <= 0.0F || creatureY <= Zones.worldMeterSizeY - 10.0F)) {
            if (diff > 35) {
                Utils.blinkToCreature(creature, owner);
            } else if (diffX > 2.0F || diffY > 2.0F || diffX < -2.0F || diffY < -2.0F) {
                int oldTileX = (int) creatureX >> 2;
                int oldTileY = (int) creatureY >> 2;

                double newRot = Math.atan2((double) (ownerY - creatureY), (double) (ownerX - creatureX)) * R2D + 90.0D;
                if (newRot > 360.0D) newRot -= 360.0D;
                if (newRot < 0.0D) newRot += 360.0D;

                float moveX = 0.0F;
                float moveY = 0.0F;

                if (diffX < -2.0F) {
                    moveX = diffX + 2.0F;
                } else if (diffX > 2.0F) {
                    moveX = diffX - 2.0F;
                }

                if (diffY < -2.0F) {
                    moveY = diffY + 2.0F;
                } else if (diffY > 2.0F) {
                    moveY = diffY - 2.0F;
                }

                float modX = (float) Math.sin(newRot * D2R) * Math.abs(moveX + Server.rand.nextFloat());
                float modY = -((float) Math.cos(newRot * D2R)) * Math.abs(moveY + Server.rand.nextFloat());

                float newX = creatureX + modX;
                float newY = creatureY + modY;

                int newTileX = (int) newX >> 2;
                int newTileY = (int) newY >> 2;

                if (!owner.isOnSurface() && !creature.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int) newX >> 2, (int) newY >> 2)))) {
                    newX = ownerX;
                    newY = ownerY;
                    newTileX = (int) newX >> 2;
                    newTileY = (int) newY >> 2;
                }

                float newZ = Zones.calculatePosZ(newX, newY, null, creature.isOnSurface(), false, creatureZ, creature, creature.getBridgeId());

                creature.setRotation((float) newRot);
                creature.setPositionX(newX);
                creature.setPositionY(newY);
                creature.setPositionZ(newZ);

                int deltaX = (int) (newX * 100.0F) - (int) (creatureX * 100.0F);
                int deltaY = (int) (newY * 100.0F) - (int) (creatureY * 100.0F);
                int deltaZ = (int) (newZ * 100.0F) - (int) (creatureZ * 100.0F);

                creature.moved(deltaX, deltaY, deltaZ, newTileX - oldTileX, newTileY - oldTileY);
            } else {
                creature.moved(0, 0, 0, 0, 0);
            }
        }
        return false;
    }

    @Override
    protected boolean pollAttack(Creature creature, long l) {
        return false;
    }

    @Override
    protected boolean pollBreeding(Creature creature, long l) {
        return false;
    }

    @Override
    public CreatureAIData createCreatureAIData() {
        return new MiniPetAIData();
    }

    @Override
    public void creatureCreated(Creature creature) {
        creature.getCreatureAIData().setCreature(creature);
    }
}
