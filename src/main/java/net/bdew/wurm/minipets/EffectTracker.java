package net.bdew.wurm.minipets;

import com.wurmonline.server.players.Player;
import net.bdew.wurm.minipets.effects.ICreatureEffect;

import java.util.HashMap;
import java.util.Map;

public class EffectTracker {
    private static Map<Player, Map<Long, PetType>> playerMap = new HashMap<>();
    private static long lastTick = System.currentTimeMillis();

    public static void addPlayer(Player player) {
        playerMap.put(player, new HashMap<>());
    }

    public static void removePlayer(Player player) {
        playerMap.remove(player);
    }

    public static void addItem(Player player, long wurmId, PetType type) {
        if (playerMap.containsKey(player)) {
            playerMap.get(player).put(wurmId, type);
        }
    }

    public static void removeItem(Player player, long wurmId) {
        if (playerMap.containsKey(player)) {
            playerMap.get(player).remove(wurmId);
        }
    }

    public static void tick() {
        if (System.currentTimeMillis() - lastTick < 4000) return;
        lastTick = System.currentTimeMillis();
        for (Map.Entry<Player, Map<Long, PetType>> playerEntry : playerMap.entrySet()) {
            if (playerEntry.getKey().hasLink() && !playerEntry.getValue().isEmpty()) {
                for (Map.Entry<Long, PetType> itemEntry : playerEntry.getValue().entrySet()) {
                    for (ICreatureEffect effect : itemEntry.getValue().effects) {
                        if (effect.needToRefreshOnItems())
                            effect.doSend(playerEntry.getKey().getCommunicator(), itemEntry.getKey(), true);
                    }
                }
            }
        }
    }
}
