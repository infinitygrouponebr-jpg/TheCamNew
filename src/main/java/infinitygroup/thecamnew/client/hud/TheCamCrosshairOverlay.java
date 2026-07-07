package infinitygroup.thecamnew.client.hud;

import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class TheCamCrosshairOverlay {
    private static final int CROSSHAIR_COLOR = 0xCCFFFFFF;
    private static final int SHADOW_COLOR = 0x80000000;
    private static final int GAP = 4;
    private static final int LENGTH = 5;
    private static final int THICKNESS = 1;
    private static final int CENTER_SIZE = 2;
    private static final int SHADOW_OFFSET = 1;

    private TheCamCrosshairOverlay() {}

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!shouldRender(minecraft)) {
            return;
        }

        int centerX = guiGraphics.guiWidth() / 2;
        int centerY = guiGraphics.guiHeight() / 2;

        drawCrosshair(guiGraphics, centerX + SHADOW_OFFSET, centerY + SHADOW_OFFSET, SHADOW_COLOR);
        drawCrosshair(guiGraphics, centerX, centerY, CROSSHAIR_COLOR);
    }

    private static boolean shouldRender(Minecraft minecraft) {
        return TheCamClientConfig.SHOW_CROSSHAIR.get()
                && minecraft.player != null
                && minecraft.level != null
                && minecraft.screen == null
                && minecraft.options != null
                && !minecraft.options.hideGui
                && !minecraft.player.isSpectator()
                && TheCamCrosshairContext.shouldShowCrosshair(minecraft.player);
    }

    private static void drawCrosshair(GuiGraphics guiGraphics, int centerX, int centerY, int color) {
        int pointLeft = centerX - (CENTER_SIZE / 2);
        int pointTop = centerY - (CENTER_SIZE / 2);

        guiGraphics.fill(pointLeft, pointTop, pointLeft + CENTER_SIZE, pointTop + CENTER_SIZE, color);

        guiGraphics.fill(centerX - GAP - LENGTH, centerY, centerX - GAP, centerY + THICKNESS, color);
        guiGraphics.fill(centerX + GAP, centerY, centerX + GAP + LENGTH, centerY + THICKNESS, color);
        guiGraphics.fill(centerX, centerY - GAP - LENGTH, centerX + THICKNESS, centerY - GAP, color);
        guiGraphics.fill(centerX, centerY + GAP, centerX + THICKNESS, centerY + GAP + LENGTH, color);
    }
}
