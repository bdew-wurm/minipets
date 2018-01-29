package net.bdew.wurm.minipets;

import com.wurmonline.server.support.JSONArray;
import com.wurmonline.server.support.JSONObject;
import net.bdew.wurm.minipets.effects.ICreatureEffect;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PetType {

    final public int id;
    final public String name;
    final public List<String> models;
    final public int rarity;
    final public float scale;
    final public List<ICreatureEffect> effects;

    public PetType(int id, String name, List<String> models, int rarity, float scale, List<ICreatureEffect> effects) {
        this.id = id;
        this.name = name;
        this.models = models;
        this.rarity = rarity;
        this.scale = scale;
        this.effects = effects;
    }

    public static PetType read(JSONObject typeObj) {
        try {
            int id = typeObj.getInt("id");
            String name = typeObj.getString("name");
            int rarity = typeObj.optInt("rarity", 0);

            List<ICreatureEffect> effects;

            if (typeObj.has("effects")) {
                JSONArray effArray = typeObj.getJSONArray("effects");
                effects = Utils.streamJSArray(effArray, JSONArray::getJSONObject)
                        .map(ICreatureEffect::read)
                        .collect(Collectors.toList());
            } else {
                effects = Collections.emptyList();
            }

            List<String> models;

            if (typeObj.has("models")) {
                models = Utils.streamJSArray(typeObj.getJSONArray("models"), JSONArray::getString)
                        .collect(Collectors.toList());
            } else {
                models = Collections.singletonList(typeObj.getString("model"));
            }

            float scale = (float) typeObj.optDouble("scale", 1);

            return new PetType(id, name, models, rarity, scale, effects);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing pet type record: " + typeObj.toString(), e);
        }

    }
}
