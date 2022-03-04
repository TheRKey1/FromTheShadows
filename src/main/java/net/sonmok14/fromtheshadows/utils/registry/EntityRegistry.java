package net.sonmok14.fromtheshadows.utils.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.NehemothEntity;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
            Fromtheshadows.MODID);


    public static final RegistryObject<EntityType<NehemothEntity>> NEHEMOTH = ENTITY_TYPES.register("nehemoth",
            () -> EntityType.Builder.of(NehemothEntity::new, MobCategory.MONSTER).sized(1.0f, 3.0f)
                    .clientTrackingRange(9).build(new ResourceLocation(Fromtheshadows.MODID, "nehemoth").toString()));


}
