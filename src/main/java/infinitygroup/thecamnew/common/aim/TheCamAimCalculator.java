package infinitygroup.thecamnew.common.aim;

import infinitygroup.thecamnew.client.camera.TheCamCameraController.CameraPose;
import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import infinitygroup.thecamnew.network.TheCamAimSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class TheCamAimCalculator {
    private static final double MIN_VECTOR_LENGTH_SQR = 1.0E-8D;

    private TheCamAimCalculator() {}

    public static TheCamAimSyncPayload compute(LocalPlayer player, CameraPose pose) {
        if (player == null || player.level() == null || pose == null) {
            return TheCamAimSyncPayload.clear();
        }

        Vec3 origin = pose.origin();
        Vec3 direction = pose.direction().normalize();
        return computeFromDirection(player, origin, direction);
    }

    public static TheCamAimSyncPayload compute(LocalPlayer player, CameraPose pose, double cursorNormX, double cursorNormY) {
        if (player == null || player.level() == null || pose == null) {
            return TheCamAimSyncPayload.clear();
        }

        Vec3 rayDirection = computeCursorDirection(pose, cursorNormX, cursorNormY);
        if (!isFinite(rayDirection)) {
            return compute(player, pose);
        }

        return computeFromDirection(player, pose.origin(), rayDirection);
    }

    private static TheCamAimSyncPayload computeFromDirection(LocalPlayer player, Vec3 origin, Vec3 direction) {
        double range = TheCamClientConfig.AIM_RANGE.get();
        Vec3 fallbackTarget = origin.add(direction.scale(range));

        if (!isFinite(origin) || !isFinite(direction) || !isFinite(fallbackTarget)) {
            return TheCamAimSyncPayload.clear();
        }

        BlockHitResult blockHit = player.level().clip(new ClipContext(
                origin,
                fallbackTarget,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player));

        AABB searchBox = player.getBoundingBox().expandTowards(direction.scale(range)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                origin,
                fallbackTarget,
                searchBox,
                TheCamAimCalculator::isValidTarget);

        boolean hasAimTarget = false;
        Vec3 aimTarget = fallbackTarget;

        double blockDistance = blockHit.getType() == HitResult.Type.MISS ? Double.POSITIVE_INFINITY : origin.distanceToSqr(blockHit.getLocation());
        double entityDistance = entityHit == null ? Double.POSITIVE_INFINITY : origin.distanceToSqr(entityHit.getLocation());

        if (entityHit != null && entityDistance <= blockDistance) {
            aimTarget = entityHit.getLocation();
            hasAimTarget = true;
        } else if (blockHit.getType() != HitResult.Type.MISS) {
            aimTarget = blockHit.getLocation();
            hasAimTarget = true;
        }

        return new TheCamAimSyncPayload(true, hasAimTarget, origin, direction, aimTarget);
    }

    private static Vec3 computeCursorDirection(CameraPose pose, double cursorNormX, double cursorNormY) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.options == null || minecraft.getWindow() == null) {
            return pose.direction().normalize();
        }

        int screenWidth = minecraft.getWindow().getScreenWidth();
        int screenHeight = minecraft.getWindow().getScreenHeight();
        if (screenWidth <= 0 || screenHeight <= 0) {
            return pose.direction().normalize();
        }

        Vec3 forward = pose.direction().normalize();
        Vec3 worldUp = new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = worldUp.cross(forward);
        if (right.lengthSqr() <= MIN_VECTOR_LENGTH_SQR || !isFinite(right)) {
            double yawRadians = Math.toRadians(pose.yaw());
            right = new Vec3(Math.cos(yawRadians), 0.0D, Math.sin(yawRadians));
        }
        right = right.normalize();

        Vec3 up = forward.cross(right);
        if (up.lengthSqr() <= MIN_VECTOR_LENGTH_SQR || !isFinite(up)) {
            up = worldUp;
        } else {
            up = up.normalize();
        }

        double verticalFovDegrees = minecraft.options.fov().get();
        double verticalTan = Math.tan(Math.toRadians(verticalFovDegrees) / 2.0D);
        double aspect = (double) screenWidth / (double) screenHeight;
        double horizontalTan = verticalTan * aspect;

        return forward
                .add(right.scale(cursorNormX * horizontalTan))
                .add(up.scale(-cursorNormY * verticalTan))
                .normalize();
    }

    private static boolean isValidTarget(Entity entity) {
        return entity != null && !entity.isSpectator() && entity.isPickable();
    }

    private static boolean isFinite(Vec3 vec) {
        return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }
}
