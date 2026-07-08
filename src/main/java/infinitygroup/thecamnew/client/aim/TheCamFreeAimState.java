package infinitygroup.thecamnew.client.aim;

import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import infinitygroup.thecamnew.client.hud.TheCamCrosshairContext;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public final class TheCamFreeAimState {
    private static final double MIN_MOUSE_DELTA = 1.0E-6D;

    private static boolean active;
    private static boolean pendingCursorInput;
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

    public static void addMouseDelta(Minecraft minecraft, double deltaX, double deltaY) {
        if (!active) {
            return;
        }

        if (Math.abs(deltaX) > MIN_MOUSE_DELTA || Math.abs(deltaY) > MIN_MOUSE_DELTA) {
            pendingCursorInput = true;
        }

        double sensitivity = TheCamClientConfig.FREE_AIM_MOUSE_SENSITIVITY.get();
        double vanillaTurnScale = getVanillaTurnScale(minecraft);
        double width = Math.max(1.0D, lastScreenWidth);
        double height = Math.max(1.0D, lastScreenHeight);

        double xDelta = ((deltaX * vanillaTurnScale) / (width * 0.5D)) * sensitivity;
        double yDelta = ((deltaY * vanillaTurnScale) / (height * 0.5D)) * sensitivity;

        if (TheCamClientConfig.FREE_AIM_INVERT_X.get()) {
            xDelta = -xDelta;
        }

        if (TheCamClientConfig.FREE_AIM_INVERT_Y.get()) {
            yDelta = -yDelta;
        }

        cursorNormX += xDelta;
        cursorNormY += yDelta;
        clampCursor();
    }

    public static boolean consumePendingCursorInput() {
        boolean pending = pendingCursorInput;
        pendingCursorInput = false;
        return pending;
    }

    private static double getVanillaTurnScale(Minecraft minecraft) {
        if (minecraft == null || minecraft.options == null) {
            return 1.0D;
        }

        double mouseSensitivity = minecraft.options.sensitivity().get();
        double sensitivityBase = mouseSensitivity * 0.6D + 0.2D;
        double sensitivityCurve = sensitivityBase * sensitivityBase * sensitivityBase;
        if (minecraft.options.getCameraType().isFirstPerson()
                && minecraft.player != null
                && minecraft.player.isScoping()) {
            return sensitivityCurve;
        }

        return sensitivityCurve * 8.0D;
    }

    public static double cursorNormX() {
        return cursorNormX;
    }

    public static double cursorNormY() {
        return cursorNormY;
    }

    public static void setCursorNormalized(double normX, double normY) {
        cursorNormX = normX;
        cursorNormY = normY;
        clampCursor();
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
        pendingCursorInput = false;
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
        pendingCursorInput = false;
        if (TheCamClientConfig.FREE_AIM_RECENTER_WHEN_INACTIVE.get()) {
            resetToCenter();
        }
    }

    private static void clampCursor() {
        cursorNormX = Mth.clamp(cursorNormX, -TheCamClientConfig.FREE_AIM_MAX_SCREEN_X.get(), TheCamClientConfig.FREE_AIM_MAX_SCREEN_X.get());
        cursorNormY = Mth.clamp(cursorNormY, -TheCamClientConfig.FREE_AIM_MAX_SCREEN_Y.get(), TheCamClientConfig.FREE_AIM_MAX_SCREEN_Y.get());
    }
}
