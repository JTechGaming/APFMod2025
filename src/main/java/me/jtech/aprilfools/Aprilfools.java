package me.jtech.aprilfools;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Aprilfools implements ModInitializer {
    public static final String MOD_ID = "aprilfools";

    private static final Random RANDOM = new Random();

    public static final RegistryKey<ConfiguredFeature<?, ?>> PINE_BUSH = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of("aprilfools", "pine_bush"));

    @Override
    public void onInitialize() {
        //AprilFoolsItems.initialize();
        AprilFoolsBlocks.initialize();

        //ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            CrosstalkManager.tick();
        });

        // April fools mod command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("aprilfools")
                    .then(CommandManager.literal("ct_chance")
                            .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                    .executes(context -> {
                                        float value = FloatArgumentType.getFloat(context, "value");
                                        CrosstalkManager.PULSE_CHANCE = value / 100.0f;
                                        context.getSource().sendFeedback(() -> Text.literal("Set crosstalk chance to " + value + "%"), false);
                                        return 1;
                                    })))
                    .then(CommandManager.literal("ct_duration")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                    .executes(context -> {
                                        int value = IntegerArgumentType.getInteger(context, "value");
                                        CrosstalkManager.PULSE_DURATION = value;
                                        context.getSource().sendFeedback(() -> Text.literal("Set crosstalk duration to " + value), false);
                                        return 1;
                                    })))
                    .then(CommandManager.literal("ct_strength")
                            .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                    .executes(context -> {
                                        float value = FloatArgumentType.getFloat(context, "value");
                                        CrosstalkManager.PULSE_STRENGTH = value / 100.0f;
                                        context.getSource().sendFeedback(() -> Text.literal("Set crosstalk strength to " + value + "%"), false);
                                        return 1;
                                    })))
            );
        });
    }

//    private void onWorldTick(ServerWorld world) {
//        List<BlockPos> redstoneDustToPulse = new ArrayList<>();
//
//        for (var chunk : world.getChunkManager().chunkLoadingManager.()) {
//            for (BlockPos pos : BlockPos.iterate(chunk.getPos().getStartPos(), chunk.getPos().getEndPos())) {
//                if (world.getBlockState(pos).getBlock() instanceof RedstoneWireBlock) {
//                    int power = world.getBlockState(pos).get(RedstoneWireBlock.POWER);
//                    if (power > 3 && RANDOM.nextFloat() < 0.2) { // 20% chance per tick
//                        List<BlockPos> possibleTargets = getValidTargets(world, pos);
//                        if (!possibleTargets.isEmpty()) {
//                            BlockPos target = possibleTargets.get(RANDOM.nextInt(possibleTargets.size()));
//                            int pulseStrength = 1 + RANDOM.nextInt(Math.min(5, power));
//                            world.setBlockState(target, world.getBlockState(target).with(RedstoneWireBlock.POWER, pulseStrength));
//                            redstoneDustToPulse.add(target);
//                        }
//                    }
//                }
//            }
//        }
//        world.getServer().createTask(() -> {
//            for (BlockPos pos : redstoneDustToPulse) {
//                world.setBlockState(pos, world.getBlockState(pos).with(RedstoneWireBlock.POWER, 0));
//            }
//        });//, 2);
//    }

    private List<BlockPos> getValidTargets(World world, BlockPos origin) {
        List<BlockPos> candidates = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = origin.add(dx, dy, dz);
                    if (!pos.equals(origin) && world.getBlockState(pos).getBlock() instanceof RedstoneWireBlock) {
                        int power = world.getBlockState(pos).get(RedstoneWireBlock.POWER);
                        if (power == 0 && !isDirectlyConnected(world, origin, pos)) {
                            candidates.add(pos);
                        }
                    }
                }
            }
        }
        return candidates;
    }

    private boolean isDirectlyConnected(World world, BlockPos a, BlockPos b) {
        for (Direction direction : Direction.values()) {
            if (a.offset(direction).equals(b)) {
                return true;
            }
        }
        return false;
    }
}
