package net.bdew.wurm.minipets;

import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.shared.constants.ItemMaterials;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import java.io.IOException;

public class PetItems {
    public static ItemTemplate petEgg, petLeash, petDecorative;
    public static int petEggId, petLeashId, petDecorativeId;

    static void register() throws IOException {
        petEgg = new ItemTemplateBuilder("bdew.pets.egg")
                .name("mystery egg", "mystery eggs", "A shiny egg from an unknown creature.")
                .imageNumber((short) 522)
                .weightGrams(500)
                .dimensions(1, 1, 1)
                .decayTime(3024000L)
                .value(10000)
                .isTraded(false)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_INDESTRUCTIBLE,
                })
                .material(ItemMaterials.MATERIAL_MAGIC)
                .modelName("model.food.egg.easter.")
                .behaviourType((short) 1)
                .build();

        petEggId = petEgg.getTemplateId();

        petLeash = new ItemTemplateBuilder("bdew.pets.leash")
                .name("pet leash", "pet leashes", "A magical leash linked to a small creature.")
                .imageNumber((short) 621)
                .weightGrams(500)
                .dimensions(1, 1, 1)
                .decayTime(3024000L)
                .value(10000)
                .isTraded(false)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_INDESTRUCTIBLE,
                        ItemTypes.ITEM_TYPE_NAMED,
                        ItemTypes.ITEM_TYPE_HASDATA
                })
                .material(ItemMaterials.MATERIAL_MAGIC)
                .modelName("model.resource.wemprope.")
                .behaviourType((short) 1)
                .build();

        petLeashId = petLeash.getTemplateId();

        petDecorative = new ItemTemplateBuilder("bdew.pets.deco")
                .name("pet", "pets", "A cute small creature that likes the company of humans.")
                .imageNumber((short) 60)
                .weightGrams(500)
                .dimensions(1, 1, 1)
                .decayTime(3024000L)
                .value(10000)
                .isTraded(false)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_INDESTRUCTIBLE,
                        ItemTypes.ITEM_TYPE_HASDATA,
                        ItemTypes.ITEM_TYPE_NAMED,
                        ItemTypes.ITEM_TYPE_DECORATION
                })
                .material(ItemMaterials.MATERIAL_MAGIC)
                .modelName("model.")
                .behaviourType((short) 1)
                .build();

        petDecorativeId = petDecorative.getTemplateId();
    }
}

