package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.client.IBewlrRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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

