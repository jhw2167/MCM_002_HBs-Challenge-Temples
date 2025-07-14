package com.holybuckets.challengetemple.client;

import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.block.be.ChallengeChestBlockEntity;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.balm.api.client.BalmClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChallengeItemBlockRenderer extends  BlockEntityWithoutLevelRenderer {

    public static ChallengeItemBlockRenderer CHEST_RENDERER;


    private final BlockEntity be;

    public ChallengeItemBlockRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet ems, BlockEntityType<?> beType, BlockState state) {
        super(dispatcher, ems);
        this.be = beType.create(BlockPos.ZERO, state);
    }

    public ChallengeItemBlockRenderer(BlockEntityType<?> beType, BlockState state) {
        this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(), beType, state);
    }

    public void renderByItem(ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockEntityRenderDispatcher dis = Minecraft.getInstance().getBlockEntityRenderDispatcher();

        //Orient the chest angles so it appears like vanilla chests
        if (be instanceof ChestBlockEntity)
        {
            matrices.pushPose();

            matrices.translate(0.5, 0.5, 0.5);
            matrices.mulPose(Axis.YP.rotationDegrees(135.0F));
            matrices.mulPose(Axis.XP.rotationDegrees(30.0F));

            float scale = 0.625F;
            matrices.scale(scale, scale, scale);
            matrices.translate(-0.5, -0.5, -0.5);
            //no overlay
            dis.render(be, light, matrices, vertexConsumers);

            matrices.popPose();

        }

        dis.renderItem(be , matrices, vertexConsumers, light, overlay);
    }

}
