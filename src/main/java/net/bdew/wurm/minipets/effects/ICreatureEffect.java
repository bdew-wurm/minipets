package net.bdew.wurm.minipets.effects;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.support.JSONObject;

public interface ICreatureEffect {
    void doSend(Communicator comm, Long wurmId, boolean decorative);

    boolean needToRefreshOnItems();

    public static ICreatureEffect read(JSONObject effObj) {
        switch (effObj.getString("type")) {
            case "color":
                return ColorEffect.read(effObj);
            case "colorRandom":
                return RandomColorEffect.read(effObj);
            case "fire":
                return AttachedEffect.Fire.read(effObj);
            case "light":
                return AttachedEffect.Light.read(effObj);
            case "ghost":
                return AttachedEffect.Transparent.read(effObj);
            case "particle":
                return ParticleEffect.read(effObj);
            default:
                throw new RuntimeException("Unknown effect: " + effObj.getString("type"));
        }
    }
}
