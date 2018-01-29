package net.bdew.wurm.minipets;

import com.wurmonline.server.Server;
import com.wurmonline.server.support.JSONArray;
import com.wurmonline.server.support.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PetTypes {
    private static Map<Integer, PetType> types = new HashMap<>();
    private static Map<Integer, List<Integer>> byRarity = new HashMap<>();

    public static PetType get(int id) {
        return types.get(id);
    }

    public static boolean exists(int id) {
        return types.containsKey(id);
    }

    public static PetType getRandomByRarity(int rarity) {
        if (!byRarity.containsKey(rarity)) return null;
        List<Integer> list = byRarity.get(rarity);
        if (list == null || list.isEmpty()) return null;
        return types.get(list.get(Server.rand.nextInt(list.size())));
    }

    public static void load(File file) throws IOException {
        try (FileInputStream f = new FileInputStream(file)) {
            JSONTokener tokenizer = new JSONTokener(f);
            JSONArray typeArray = new JSONArray(tokenizer);
            Map<Integer, PetType> loaded = Utils.streamJSArray(typeArray, JSONArray::getJSONObject)
                    .map(PetType::read)
                    .collect(Collectors.toMap(e -> e.id, Function.identity()));
            types.clear();
            types.putAll(loaded);

            byRarity.clear();

            types.values().forEach(petType -> {
                if (!byRarity.containsKey(petType.rarity))
                    byRarity.put(petType.rarity, new ArrayList<>());
                byRarity.get(petType.rarity).add(petType.id);
            });

            MiniPets.logInfo("Loaded pet data...");
            if (byRarity.containsKey(0))
                MiniPets.logInfo(String.format("Common pets: %d", byRarity.get(0).size()));
            if (byRarity.containsKey(1))
                MiniPets.logInfo(String.format("Rare pets: %d", byRarity.get(1).size()));
            if (byRarity.containsKey(2))
                MiniPets.logInfo(String.format("Supreme pets: %d", byRarity.get(2).size()));
            if (byRarity.containsKey(3))
                MiniPets.logInfo(String.format("Fantastic pets: %d", byRarity.get(3).size()));
        }
    }
}
