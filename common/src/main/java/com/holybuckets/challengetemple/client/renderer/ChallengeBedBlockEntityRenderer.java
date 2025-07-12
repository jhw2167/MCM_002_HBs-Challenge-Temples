package com.holybuckets.challengetemple.client.renderer;

import com.holybuckets.challengetemple.block.entity.ChallengeBedBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class ChallengeBedBlockEntityRenderer implements BlockEntityRenderer<ChallengeBedBlockEntity> {
    private final ModelPart headRoot;
    private final ModelPart footRoot;

    public ChallengeBedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.headRoot = context.bakeLayer(ModelLayers.BED_HEAD);
        this.footRoot = context.bakeLayer(ModelLayers.BED_FOOT);
    }

    @Override
    public void render(ChallengeBedBlockEntity blockEntity, float partialTick, PoseStack poseStack, 
                      MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Material material = new Material(InventoryMenu.BLOCK_ATLAS, 
            new ResourceLocation("minecraft:entity/bed/red"));

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5625D, 0.0D);
        
        VertexConsumer vertexconsumer = material.buffer(bufferSource, RenderType::entitySolid);
        this.headRoot.render(poseStack, vertexconsumer, packedLight, packedOverlay);
        this.footRoot.render(poseStack, vertexconsumer, packedLight, packedOverlay);
        
        poseStack.popPose();
    }
}
