package net.bdew.wurm.minipets;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import net.bdew.wurm.minipets.actions.ConvertPetAction;
import net.bdew.wurm.minipets.actions.HatchEggAction;
import net.bdew.wurm.minipets.actions.HidePetAction;
import net.bdew.wurm.minipets.actions.SummonPetAction;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniPets implements WurmServerMod, Configurable, PreInitable, Initable, ServerStartedListener, ItemTemplatesCreatedListener, PlayerMessageListener, PlayerLoginListener, ServerPollListener {
    private static final Logger logger = Logger.getLogger("MiniPets");

    public static void logException(String msg, Throwable e) {
        if (logger != null)
            logger.log(Level.SEVERE, msg, e);
    }

    public static void logWarning(String msg) {
        if (logger != null)
            logger.log(Level.WARNING, msg);
    }

    public static void logInfo(String msg) {
        if (logger != null)
            logger.log(Level.INFO, msg);
    }

    static File typesFile;

    @Override
    public void configure(Properties properties) {
        typesFile = new File(properties.getProperty("typesFile", "mods/minipets_types.json"));
    }

    @Override
    public void preInit() {
        try {
            ModActions.init();
            ClassPool classPool = HookManager.getInstance().getClassPool();

            classPool.getCtClass("com.wurmonline.server.zones.VirtualZone")
                    .getMethod("addCreature", "(JZJFFF)Z")
                    .insertAfter("net.bdew.wurm.minipets.Hooks.addCreatureHook(this, $1);");

            classPool.getCtClass("com.wurmonline.server.creatures.Creature")
                    .getMethod("getModelName", "()Ljava/lang/String;")
                    .insertBefore("if (net.bdew.wurm.minipets.Hooks.isMiniPet(this)) return net.bdew.wurm.minipets.Hooks.getModel(this);");

            CtClass ctVirtualZone = classPool.getCtClass("com.wurmonline.server.zones.VirtualZone");

            ctVirtualZone.getMethod("addItem", "(Lcom/wurmonline/server/items/Item;Lcom/wurmonline/server/zones/VolaTile;JZ)Z")
                    .instrument(new ExprEditor() {
                        @Override
                        public void edit(MethodCall m) throws CannotCompileException {
                            if (m.getMethodName().equals("sendItem")) {
                                m.replace("if (net.bdew.wurm.minipets.Hooks.preSendItemHook(this.watcher.getCommunicator(), item)) { $_ = $proceed($$); net.bdew.wurm.minipets.Hooks.sendItemHook(this.watcher.getCommunicator(), item); }");
                            }
                        }
                    });

            ctVirtualZone.getMethod("sendRemoveItem", "(Lcom/wurmonline/server/items/Item;)V")
                    .instrument(new ExprEditor() {
                        @Override
                        public void edit(MethodCall m) throws CannotCompileException {
                            if (m.getMethodName().equals("sendRemoveItem")) {
                                m.replace("if (net.bdew.wurm.minipets.Hooks.removeItemHook(this.watcher.getCommunicator(), item)) $_ = $proceed($$);");
                            }
                        }
                    });

            ctVirtualZone.getMethod("isVisible", "(Lcom/wurmonline/server/items/Item;Lcom/wurmonline/server/zones/VolaTile;)Z")
                    .insertBefore("if (this.watcher.isPlayer() && net.bdew.wurm.minipets.Hooks.checkVisibilityOverride(this.watcher, $1, $2)) return true;");

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void onServerStarted() {
        ModActions.registerAction(new SummonPetAction());
        ModActions.registerAction(new HidePetAction());
        ModActions.registerAction(new HatchEggAction());
        ModActions.registerAction(new ConvertPetAction());
    }

    @Override
    public void onItemTemplatesCreated() {
        try {
            PetTypes.load(typesFile);
            PetCreature.register();
            PetItems.register();
            Compat3D.installDisplayHook();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Deprecated
    public boolean onPlayerMessage(Communicator communicator, String message) {
        return false;
    }

    @Override
    public MessagePolicy onPlayerMessage(Communicator communicator, String message, String title) {
        return CommandHandler.handleCommands(communicator, message);
    }

    @Override
    public void onPlayerLogin(Player player) {
        EffectTracker.addPlayer(player);
    }

    @Override
    public void onPlayerLogout(Player player) {
        EffectTracker.removePlayer(player);
    }

    @Override
    public void onServerPoll() {
        EffectTracker.tick();
    }
}
