package com.holybuckets.challengetemple.client.screen;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.menu.ChallengeChestCountingMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Screen for Challenge Counting Chest — displays the chest GUI with a special layout.
 */
public class CountingChestScreen extends AbstractContainerScreen<ChallengeChestCountingMenu> {

    // Path to your custom GUI texture (adjust path to your mod's namespace)
    private static final ResourceLocation TEXTURE =
        new ResourceLocation(Constants.MOD_ID, "textures/gui/challenge_chest_counting_gui.png");

    public CountingChestScreen(ChallengeChestCountingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 114 + (menu.containerRows - 1) * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
