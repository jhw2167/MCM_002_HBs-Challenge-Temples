package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.client.IBewlrRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Item.class})
public class ItemMixin implements IBewlrRenderer {
    @Shadow(
        remap = false
    )
    private Object renderProperties;

    public ItemMixin() {
    }

    public void setBlockEntityWithoutLevelRenderer(final BlockEntityWithoutLevelRenderer bewlr) {
        if (this.renderProperties != null) {
            throw new IllegalStateException("Cannot set both BlockEntityWithoutLevelRenderer because it already exists");
        } else {
            this.renderProperties = new IClientItemExtensions() {
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return bewlr;
                }
            };
        }
    }

}

