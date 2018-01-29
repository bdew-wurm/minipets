package net.bdew.wurm.minipets.effects;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.support.JSONObject;

public abstract class AttachedEffect implements ICreatureEffect {
    final private byte effectType, data0, data1, data2, data3;

    public AttachedEffect(byte effectType, byte data0, byte data1, byte data2, byte data3) {
        this.effectType = effectType;
        this.data0 = data0;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }

    @Override
    public void doSend(Communicator comm, Creature creature) {
        comm.sendAttachEffect(creature.getWurmId(), effectType, data0, data1, data2, data3);
    }

    static class Fire extends AttachedEffect {
        public Fire(int size) {
            super((byte) 5, (byte) 0, (byte) 0, (byte) 0, (byte) size);
        }

        public static Fire read(JSONObject effObj) {
            return new Fire(
                    effObj.optInt("size", 0)
            );
        }
    }

    static class Light extends AttachedEffect {
        public Light(int r, int g, int b, int power) {
            super((byte) 0, (byte) r, (byte) g, (byte) b, (byte) power);
        }

        public static Light read(JSONObject effObj) {
            return new Light(
                    effObj.optInt("r", 255),
                    effObj.optInt("g", 255),
                    effObj.optInt("b", 255),
                    effObj.optInt("power", 0)
            );
        }
    }

    static class Transparent extends AttachedEffect {
        public Transparent(int alpha, int mode) {
            super((byte) 2, (byte) alpha, (byte) mode, (byte) 0, (byte) 0);
        }

        public static Transparent read(JSONObject effObj) {
            return new Transparent(
                    effObj.optInt("alpha", 255),
                    effObj.optInt("mode", 255)
            );
        }
    }
}

