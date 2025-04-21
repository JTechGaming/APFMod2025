package me.jtech.aprilfools;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DiamondPressureplateBlock extends PressurePlateBlock {
    public DiamondPressureplateBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {
        return getEntityCount(world, BOX.offset(pos), PlayerEntity.class) > 0 ? 15 : 0;
    }
}
