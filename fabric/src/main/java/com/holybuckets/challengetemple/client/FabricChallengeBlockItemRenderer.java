package com.holybuckets.challengetemple.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FabricChallengeBlockItemRenderer extends ChallengeItemBlockRenderer implements BuiltinItemRenderer {

    public FabricChallengeBlockItemRenderer(BlockEntityType<?> beType, BlockState state) {
        super(beType, state);
    }

    @Override
    public void render(ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
       super.renderByItem(stack, matrices, vertexConsumers, light, overlay);
    }
}
