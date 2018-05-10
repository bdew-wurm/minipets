package net.bdew.wurm.minipets.effects;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.support.JSONObject;

public class ColorEffect implements ICreatureEffect {
    private final byte r, g, b, a, t;

    public ColorEffect(byte r, byte g, byte b, byte a, byte t) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.t = t;
    }

    public static ColorEffect read(JSONObject effObj) {
        return new ColorEffect(
                (byte) effObj.getInt("r"),
                (byte) effObj.getInt("g"),
                (byte) effObj.getInt("b"),
                (byte) effObj.optInt("alpha", 255),
                (byte) effObj.optInt("mode", 0)
        );
    }

    @Override
    public void doSend(Communicator comm, Long wurmId, boolean decorative) {
        comm.sendRepaint(wurmId, r, g, b, a, t);
    }

    @Override
    public boolean needToRefreshOnItems() {
        return false;
    }
}
