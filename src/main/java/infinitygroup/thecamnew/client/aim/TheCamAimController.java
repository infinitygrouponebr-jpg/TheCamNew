package infinitygroup.thecamnew.client.aim;

import infinitygroup.thecamnew.client.hud.TheCamCrosshairContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class TheCamAimController {
    private static final double MIN_DIRECTION_LENGTH_SQR = 1.0E-6D;

    private TheCamAimController() {}

    public static void alignPlayerToAim(LocalPlayer player) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!shouldAlign(minecraft, player)) {
            return;
        }

        Vec3 target = TheCamClientAimState.getTarget();
        if (!isFinite(target)) {
            return;
        }

        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 direction = target.subtract(eye);
        if (direction.lengthSqr() <= MIN_DIRECTION_LENGTH_SQR || !isFinite(direction)) {
            return;
        }

        double dx = direction.x;
        double dy = direction.y;
        double dz = direction.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) (-(Mth.atan2(dy, horizontal) * Mth.RAD_TO_DEG));
        pitch = Mth.clamp(pitch, -89.9F, 89.9F);

        player.setYRot(yaw);
        player.setXRot(pitch);
        player.setYHeadRot(yaw);
        player.yBodyRot = yaw;
    }

    private static boolean shouldAlign(Minecraft minecraft, LocalPlayer player) {
        return player != null
                && minecraft.level != null
                && minecraft.screen == null
                && !player.isSpectator()
                && TheCamCrosshairContext.shouldShowCrosshair(player)
                && TheCamClientAimState.isActive()
                && isFinite(TheCamClientAimState.getOrigin())
                && isFinite(TheCamClientAimState.getDirection())
                && isFinite(TheCamClientAimState.getTarget());
    }

    private static boolean isFinite(Vec3 vec) {
        return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }
}
