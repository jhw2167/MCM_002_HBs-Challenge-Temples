package com.holybuckets.challengetemple.block;

import net.blay09.mods.balm.api.event.UseBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ChallengeBuildingBlock extends Block {

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public ChallengeBuildingBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)
        .strength(0f)
        .sound(SoundType.STONE)
        );
        this.registerDefaultState(this.defaultBlockState().setValue(COLOR, DyeColor.WHITE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    /**
     * Move to the next color in the dyes array or wrap around
     */
    public static void changeColor(Level level, BlockPos pos, BlockState state) {
        DyeColor[] dyes = DyeColor.values();
        DyeColor currentColor = state.getValue(ChallengeBuildingBlock.COLOR);
        int nextIndex = (currentColor.ordinal() + 1) % dyes.length;
        DyeColor nextColor = dyes[nextIndex];

        level.setBlock(pos, state.setValue(ChallengeBuildingBlock.COLOR, nextColor), Block.UPDATE_ALL);
    }

}
