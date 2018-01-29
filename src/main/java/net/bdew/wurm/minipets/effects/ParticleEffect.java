package net.bdew.wurm.minipets.effects;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.support.JSONObject;

public class ParticleEffect implements ICreatureEffect {
    final private String particle;
    final private float rot;

    public ParticleEffect(String particle, float rot) {
        this.particle = particle;
        this.rot = rot;
    }

    @Override
    public void doSend(Communicator comm, Creature creature) {
        comm.sendAddEffect(creature.getWurmId(), (short) 27, creature.getPosX(), creature.getPosY(), creature.getPositionZ(),
                (byte) creature.getLayer(), particle, 10, rot);
    }

    public static ParticleEffect read(JSONObject effObj) {
        return new ParticleEffect(
                effObj.getString("particle"),
                (float) effObj.optDouble("rot", 0)
        );
    }
}


