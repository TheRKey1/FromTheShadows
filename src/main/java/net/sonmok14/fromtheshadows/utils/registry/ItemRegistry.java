package net.sonmok14.fromtheshadows.utils.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.Fromtheshadows;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Fromtheshadows.MODID);


    public static final RegistryObject<Item> NEHEMOTH_SPAWN_EGG = ITEMS.register("nehemoth_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistry.NEHEMOTH, 0x626A6F,0xF3EAE8, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
}
