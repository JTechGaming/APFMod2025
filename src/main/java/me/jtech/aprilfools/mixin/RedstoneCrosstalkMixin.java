package me.jtech.aprilfools.mixin;

import me.jtech.aprilfools.CrosstalkManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(RedstoneWireBlock.class)
public class RedstoneCrosstalkMixin {

    @Inject(method = "update", at = @At("RETURN"))
    private void injectCrosstalk(World world, BlockPos updatedPos, BlockState state, WireOrientation orientation, boolean blockAdded, CallbackInfo ci) {
        if (!(world instanceof ServerWorld)) return; // Ensure we're on the server side
        CrosstalkManager.world = world;
        if (!(state.getBlock() instanceof RedstoneWireBlock)) return;

        int signalStrength = state.get(RedstoneWireBlock.POWER);
        if (signalStrength == 0) { // Skip if not powered
            CrosstalkManager.causes.remove(updatedPos);
            CrosstalkManager.removeWire(updatedPos);
            for (BlockPos pos : CrosstalkManager.oldWires) {
                CrosstalkManager.causes.remove(pos);
                CrosstalkManager.removeWire(pos);
            }
            return;
        }

        if (CrosstalkManager.containsBlockPos(updatedPos)) return;
        CrosstalkManager.causes.add(updatedPos);
    }
}
