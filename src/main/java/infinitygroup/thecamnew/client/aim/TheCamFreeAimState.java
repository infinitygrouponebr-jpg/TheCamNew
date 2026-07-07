package infinitygroup.thecamnew.client.aim;

import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import infinitygroup.thecamnew.client.hud.TheCamCrosshairContext;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public final class TheCamFreeAimState {
    private static boolean active;
    private static double cursorNormX;
    private static double cursorNormY;
    private static int lastScreenWidth;
    private static int lastScreenHeight;

    private TheCamFreeAimState() {}

    public static void tick(Minecraft minecraft) {
        if (minecraft == null) {
            clear();
            return;
        }

        if (minecraft.getWindow() != null) {
            lastScreenWidth = Math.max(1, minecraft.getWindow().getScreenWidth());
            lastScreenHeight = Math.max(1, minecraft.getWindow().getScreenHeight());
        }

        boolean shouldBeActive = shouldBeActive(minecraft);
        if (!shouldBeActive) {
            deactivate();
            return;
        }

        active = true;
        clampCursor();
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean shouldCaptureMouse(Minecraft minecraft) {
        return active
                && minecraft != null
                && minecraft.screen == null
                && TheCamClientConfig.FREE_AIM_ENABLED.get()
                && TheCamClientConfig.FREE_AIM_CAPTURE_MOUSE.get();
    }

    public static void addMouseDelta(double deltaX, double deltaY) {
        if (!active) {
            return;
        }

        double sensitivity = TheCamClientConfig.FREE_AIM_MOUSE_SENSITIVITY.get();
        double width = Math.max(1.0D, lastScreenWidth);
        double height = Math.max(1.0D, lastScreenHeight);

        cursorNormX += (deltaX / (width * 0.5D)) * sensitivity;
        cursorNormY += (deltaY / (height * 0.5D)) * sensitivity;
        clampCursor();
    }

    public static double cursorNormX() {
        return cursorNormX;
    }

    public static double cursorNormY() {
        return cursorNormY;
    }

    public static int cursorScreenX(int guiWidth) {
        return Mth.floor((guiWidth * 0.5D) + (cursorNormX * guiWidth * 0.5D));
    }

    public static int cursorScreenY(int guiHeight) {
        return Mth.floor((guiHeight * 0.5D) + (cursorNormY * guiHeight * 0.5D));
    }

    public static void resetToCenter() {
        cursorNormX = 0.0D;
        cursorNormY = 0.0D;
    }

    public static void clear() {
        active = false;
        resetToCenter();
        lastScreenWidth = 0;
        lastScreenHeight = 0;
    }

    private static boolean shouldBeActive(Minecraft minecraft) {
        return TheCamClientConfig.FREE_AIM_ENABLED.get()
                && minecraft.player != null
                && minecraft.level != null
                && minecraft.screen == null
                && !minecraft.player.isSpectator()
                && TheCamCrosshairContext.shouldShowCrosshair(minecraft.player);
    }

    private static void deactivate() {
        active = false;
        if (TheCamClientConfig.FREE_AIM_RECENTER_WHEN_INACTIVE.get()) {
            resetToCenter();
        }
    }

    private static void clampCursor() {
        cursorNormX = Mth.clamp(cursorNormX, -TheCamClientConfig.FREE_AIM_MAX_SCREEN_X.get(), TheCamClientConfig.FREE_AIM_MAX_SCREEN_X.get());
        cursorNormY = Mth.clamp(cursorNormY, -TheCamClientConfig.FREE_AIM_MAX_SCREEN_Y.get(), TheCamClientConfig.FREE_AIM_MAX_SCREEN_Y.get());
    }
}
