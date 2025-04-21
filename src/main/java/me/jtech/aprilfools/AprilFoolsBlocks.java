package me.jtech.aprilfools;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class AprilFoolsBlocks {
    private static AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.OAK_PRESSURE_PLATE)
            .sounds(BlockSoundGroup.METAL);
    public static final Block DIAMOND_PRESSURE_PLATE = register(
            "diamond_pressure_plate",
            set -> new DiamondPressureplateBlock(BlockSetType.IRON, settings),
            settings,
            true
    );
    public static final Block PINE_BUSH = register(
            "pine_bush",
            ShortPlantBlock::new,
            AbstractBlock.Settings.create().mapColor(MapColor.DARK_GREEN).replaceable().noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(AbstractBlock.OffsetType.XZ).burnable().pistonBehavior(PistonBehavior.DESTROY),
            true
    );
    public static final Block SUN_THORN = register(
            "sun_thorn",
            ShortPlantBlock::new,
            AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).replaceable().noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(AbstractBlock.OffsetType.XZ).burnable().pistonBehavior(PistonBehavior.DESTROY),
            true
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> {
            itemGroup.addAfter(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.asItem(), AprilFoolsBlocks.DIAMOND_PRESSURE_PLATE.asItem());
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register((itemGroup) -> {
            itemGroup.addAfter(Blocks.SHORT_GRASS.asItem(), AprilFoolsBlocks.PINE_BUSH.asItem());
            itemGroup.addAfter(Blocks.DEAD_BUSH.asItem(), AprilFoolsBlocks.SUN_THORN.asItem());
        });
    }

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        // Create a registry key for the block
        RegistryKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Aprilfools.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Aprilfools.MOD_ID, name));
    }
}
