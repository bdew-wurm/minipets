package net.bdew.wurm.minipets;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.Item;
import net.bdew.wurm.server.threedee.api.IDisplayHook;

import java.lang.reflect.InvocationTargetException;

public class Compat3D {
    static void installDisplayHook() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            Class.forName("net.bdew.wurm.server.threedee.api.DisplayHookRegistry")
                    .getMethod("add", int.class, IDisplayHook.class)
                    .invoke(null, PetItems.petDecorativeId, new IDisplayHook() {
                                @Override
                                public boolean addItem(Communicator comm, Item item, float x, float y, float z, float rot) {
                                    Decorative.sendDecorative(comm, item, x, y, z, rot);
                                    return true;
                                }

                                @Override
                                public boolean removeItem(Communicator comm, Item item) {
                                    Decorative.removeDecorative(comm, item);
                                    return true;
                                }
                            }

                    );
            MiniPets.logInfo("3D Stuff mod loaded - added compatibility hook");
        } catch (ClassNotFoundException e) {
            MiniPets.logInfo("3D Stuff mod doesn't seem to be loaded");
        }
    }
}
