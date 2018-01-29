package net.bdew.wurm.minipets;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.ai.CreatureAIData;

public class MiniPetAIData extends CreatureAIData {
    private Creature owner;
    private PetType type;

    public Creature getOwner() {
        return owner;
    }

    public void setOwner(Creature owner) {
        this.owner = owner;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public static MiniPetAIData get(Creature creature) {
        return (MiniPetAIData) creature.getCreatureAIData();
    }
}
