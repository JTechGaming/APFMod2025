package me.jtech.aprilfools;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class CrosstalkManager {
    public static List<Wire> wires = new CopyOnWriteArrayList<>();
    public static List<BlockPos> oldWires = new CopyOnWriteArrayList<>();
    public static List<BlockPos> causes = new ArrayList<>();
    public static World world;

    private static final Random random = new Random();

    public static int PULSE_DURATION = 30; // Adjust as needed
    public static float PULSE_CHANCE = 0.001f; // Adjust as needed
    public static float PULSE_STRENGTH = 0.25f; // Adjust as needed

    public static void tick() {
        if (world == null || wires == null) return;

        for (BlockPos updatedPos : causes) {
            crossTalk(updatedPos);
        }

        for (Wire wire : wires) {
            wire.ticks++;
//            if (wire.ticks == 1) {
//                if (world.getBlockState(wire.pos).get(Properties.POWER) > 0) {
//                    removeWire(wire.pos);
//                }
//            }
            if (wire.ticks == 2) {
                updateWireBlock(wire.pos, wire.power, wire.state);
            }
            if (wire.ticks == PULSE_DURATION + 1) {
                updateWireBlock(wire.pos, wire.oldPower, wire.state);
                removeWire(wire.pos);
            }
        }
    }

    public static void updateWireBlock(BlockPos pos, int power, BlockState state) {
        world.setBlockState(pos, state.with(Properties.POWER, power));
        world.updateNeighbors(pos, Blocks.REDSTONE_WIRE);
        world.updateNeighborsAlways(pos, Blocks.REDSTONE_WIRE);
    }

    public static boolean containsBlockPos(BlockPos pos) {
        for (Wire wire : wires) {
            if (wire.pos.equals(pos)) return true;
        }
        return false;
    }

    public static Wire getWireFromBlockPos(BlockPos pos) {
        for (Wire wire : wires) {
            if (wire.pos.equals(pos)) return wire;
        }
        return null;
    }

    public static void addWire(BlockPos pos, int power, int oldPower, BlockState state) {
        wires.add(new Wire(pos, power, oldPower, state));
    }

    public static void removeWire(BlockPos pos) {
        if (wires.removeIf(wire -> wire.pos.equals(pos))) {
            if (oldWires.contains(pos)) return;
            oldWires.add(pos);
            causes.remove(pos);
        }
    }

    public static class Wire {
        public BlockPos pos;
        public int power;
        public int oldPower;
        public BlockState state;
        public int ticks = 0;

        public Wire(BlockPos pos, int power, int oldPower, BlockState state) {
            this.pos = pos;
            this.power = power;
            this.oldPower = oldPower;
            this.state = state;
        }
    }

    public static List<BlockPos> getAllBlocksIn3x3x3(BlockPos pos) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    positions.add(pos.add(x, y, z));
                }
            }
        }
        return positions;
    }

    public static List<BlockPos> getAllDiagonalBlocksIn3x3x3(BlockPos pos) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 || y == 0 || z == 0) continue;
                    positions.add(pos.add(x, y, z));
                }
            }
        }
        return positions;
    }

    public static boolean isDirectlyConnected(World world, BlockPos a, BlockPos b) {
        for (Direction direction : Direction.values()) {
            if (a.offset(direction).equals(b)) {
                return true;
            }
        }
        return false;
    }

    public static List<BlockPos> getDirectlyConnected(BlockPos pos) {
        List<BlockPos> positions = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            positions.add(pos.offset(direction));
        }
        return positions;
    }

    public static void crossTalk(BlockPos updatedPos) {
        // Check nearby positions (including diagonals)
        if (containsBlockPos(updatedPos)) return;
        for (BlockPos crossTalkPos : getAllDiagonalBlocksIn3x3x3(updatedPos)) {
            BlockState targetState = world.getBlockState(crossTalkPos);
            if (!(targetState.getBlock() instanceof RedstoneWireBlock)) continue;
            if (crossTalkPos.equals(updatedPos)) continue; // Skip self

            if (isDirectlyConnected(world, updatedPos, crossTalkPos)) continue; // Skip directly connected wires

            if (containsBlockPos(crossTalkPos)) return;

            int crosstalkStrength = random.nextInt(1, 3); // Random strength
            boolean shouldCrosstalk = random.nextFloat() < PULSE_CHANCE; // Random chance to crosstalk
            int oldPower = targetState.get(RedstoneWireBlock.POWER);
            if (shouldCrosstalk && oldPower == 0) {
                addWire(crossTalkPos, crosstalkStrength, oldPower, targetState);
            }
        }
    }
}
