package net.bdew.wurm.minipets.effects;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.support.JSONObject;

import java.util.Random;

public class RandomColorEffect implements ICreatureEffect {
    private final byte a, t;
    private static Random rng = new Random();

    public RandomColorEffect(byte a, byte t) {
        this.a = a;
        this.t = t;
    }

    public static RandomColorEffect read(JSONObject effObj) {
        return new RandomColorEffect(
                (byte) effObj.optInt("alpha", 255),
                (byte) effObj.optInt("mode", 0)
        );
    }

    @Override
    public void doSend(Communicator comm, Long wurmId, boolean decorative) {
        rng.setSeed(wurmId);
        comm.sendRepaint(wurmId, (byte) rng.nextInt(256), (byte) rng.nextInt(256), (byte) rng.nextInt(256), a, t);
    }

    @Override
    public boolean needToRefreshOnItems() {
        return false;
    }
}
