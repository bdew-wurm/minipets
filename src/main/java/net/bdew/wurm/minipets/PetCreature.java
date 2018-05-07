package net.bdew.wurm.minipets;

import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.shared.constants.CreatureTypes;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;


public class PetCreature {
    public static int templateId;
    public static CreatureTemplate template;

    static void register() throws NoSuchFieldException, IllegalAccessException {
        template = new CreatureTemplateBuilder("bdew.minipet")
                .name("Mini Pet")
                .description("A cute small creature that likes the company of humans")
                .modelName("model")
                .types(new int[]{CreatureTypes.C_TYPE_SWIMMING, CreatureTypes.C_TYPE_INVULNERABLE})
                .defaultSkills()
                .vision((short) 5)
                .dimension((short) 10, (short) 10, (short) 10)
                .speed(1.5f)
                .moveRate(0)
                .aggressive(0)
                .baseCombatRating(99f)
                .build();


        template.setCreatureAI(new MiniPetAI());

        ReflectionUtil.setPrivateField(template, ReflectionUtil.getField(CreatureTemplate.class, "noCorpse"), true);

        templateId = template.getTemplateId();
    }
}
