package infinitygroup.thecamnew.common.aim;

import infinitygroup.thecamnew.client.camera.TheCamCameraController.CameraPose;
import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import infinitygroup.thecamnew.network.TheCamAimSyncPayload;
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
    private TheCamAimCalculator() {}

    public static TheCamAimSyncPayload compute(LocalPlayer player, CameraPose pose) {
        if (player == null || player.level() == null || pose == null) {
            return TheCamAimSyncPayload.clear();
        }

        Vec3 origin = pose.origin();
        Vec3 direction = pose.direction().normalize();
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

    private static boolean isValidTarget(Entity entity) {
        return entity != null && !entity.isSpectator() && entity.isPickable();
    }

    private static boolean isFinite(Vec3 vec) {
        return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }
}
