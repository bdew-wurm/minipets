package net.bdew.wurm.minipets.effects;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.support.JSONObject;

public class ParticleEffect implements ICreatureEffect {
    final private String particle;
    final private float rot;

    public ParticleEffect(String particle, float rot) {
        this.particle = particle;
        this.rot = rot;
    }

    @Override
    public void doSend(Communicator comm, Long wurmId, boolean decorative) {
        comm.sendAddEffect(wurmId, (short) 27, 0, 0, 0, (byte) 0, particle, decorative ? Float.MAX_VALUE : 10f, rot);
    }

    @Override
    public boolean needToRefreshOnItems() {
        return false;
    }

    public static ParticleEffect read(JSONObject effObj) {
        return new ParticleEffect(
                effObj.getString("particle"),
                (float) effObj.optDouble("rot", 0)
        );
    }
}


