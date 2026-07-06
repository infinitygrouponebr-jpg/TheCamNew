package infinitygroup.thecamnew.client.camera;

import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import infinitygroup.thecamnew.mixin.CameraAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class TheCamCameraController {
    private static final double VISUAL_FOCUS_VERTICAL_OFFSET = 0.12D;
    private static final double COLLISION_ANCHOR_VERTICAL_OFFSET = 0.20D;
    private static final double TIGHT_SPACE_ANCHOR_VERTICAL_OFFSET = 0.15D;
    private static final double MOVEMENT_RESET_DISTANCE_SQR = 64.0D;
    private static final double MIN_VECTOR_LENGTH_SQR = 1.0E-8D;

    private static Vec3 lastCameraPosition;
    private static Vec3 lastEyePosition;
    private static CameraPose lastRenderedPose;

    private TheCamCameraController() {}

    public static CameraPose computePose(LocalPlayer player, float partialTick) {
        return computePose(player, partialTick, true);
    }

    public static CameraPose computePose(LocalPlayer player, float partialTick, boolean updateSmoothingState) {
        Vec3 eyePosition = player.getEyePosition(partialTick);
        if (updateSmoothingState && shouldResetForMovement(eyePosition)) {
            reset();
        }

        Vec3 horizontalForward = getHorizontalForward(player, partialTick);
        Vec3 right = rightVector(horizontalForward);
        Vec3 cameraBack = horizontalForward.scale(-1.0D);
        // cameraBack must place the camera behind the player.
        // If the player face is visible, this vector is inverted.
        if (cameraBack.dot(horizontalForward) > 0.0D) {
            cameraBack = cameraBack.scale(-1.0D);
        }

        Vec3 visualFocus = eyePosition
                .add(horizontalForward.scale(TheCamClientConfig.CAMERA_FORWARD_FOCUS_OFFSET.get()))
                .add(0.0D, VISUAL_FOCUS_VERTICAL_OFFSET, 0.0D);

        Vec3 desiredPosition = visualFocus
                .add(cameraBack.scale(TheCamClientConfig.CAMERA_DISTANCE.get()))
                .add(right.scale(TheCamClientConfig.CAMERA_SIDE_OFFSET.get()))
                .add(0.0D, TheCamClientConfig.CAMERA_HEIGHT.get(), 0.0D);

        if (desiredPosition.subtract(visualFocus).dot(horizontalForward) > 0.0D) {
            desiredPosition = visualFocus
                    .add(horizontalForward.scale(-TheCamClientConfig.CAMERA_DISTANCE.get()))
                    .add(right.scale(TheCamClientConfig.CAMERA_SIDE_OFFSET.get()))
                    .add(0.0D, TheCamClientConfig.CAMERA_HEIGHT.get(), 0.0D);
        }

        Vec3 collisionAnchor = eyePosition.add(0.0D, COLLISION_ANCHOR_VERTICAL_OFFSET, 0.0D);
        CollisionResult collisionResult = new CollisionResult(desiredPosition, false, false);
        if (TheCamClientConfig.CAMERA_COLLISION.get()) {
            collisionResult = resolveCollision(player, collisionAnchor, desiredPosition);
        }

        Vec3 finalTargetPosition = collisionResult.position();
        if (isTooCloseOrCeilingBlocked(collisionAnchor, finalTargetPosition, collisionResult.ceilingBlocked())) {
            finalTargetPosition = computeTightSpaceFallback(player, eyePosition, horizontalForward, right, cameraBack);
        }

        Vec3 smoothed = finalTargetPosition;
        if (updateSmoothingState && lastCameraPosition != null) {
            double smoothingFactor = collisionResult.blocked()
                    ? TheCamClientConfig.CAMERA_COLLISION_SMOOTHNESS.get()
                    : TheCamClientConfig.CAMERA_SMOOTHNESS.get();
            smoothed = lastCameraPosition.lerp(finalTargetPosition, Mth.clamp(smoothingFactor, 0.0D, 1.0D));
        }

        if (TheCamClientConfig.CAMERA_COLLISION.get()) {
            smoothed = clipCameraPosition(player, collisionAnchor, smoothed);
        }

        Vec3 direction = visualFocus.subtract(smoothed);
        if (direction.lengthSqr() <= MIN_VECTOR_LENGTH_SQR) {
            direction = horizontalForward;
        } else {
            direction = direction.normalize();
        }

        float[] cameraAngles = rotationFromDirection(direction);
        CameraPose pose = new CameraPose(smoothed, direction, visualFocus, cameraAngles[0], cameraAngles[1]);

        if (updateSmoothingState) {
            lastCameraPosition = smoothed;
            lastEyePosition = eyePosition;
            lastRenderedPose = pose;
        }

        return pose;
    }

    public static CameraPose getLastRenderedPose() {
        return lastRenderedPose;
    }

    public static void applyToCamera(Camera camera, CameraPose pose) {
        ((CameraAccessor) camera).thecamnew$setPosition(pose.origin().x, pose.origin().y, pose.origin().z);
    }

    public static void reset() {
        lastCameraPosition = null;
        lastEyePosition = null;
        lastRenderedPose = null;
    }

    private static CollisionResult resolveCollision(LocalPlayer player, Vec3 from, Vec3 desired) {
        BlockHitResult blockHit = clipBlock(player, from, desired);
        if (blockHit.getType() != HitResult.Type.BLOCK) {
            return new CollisionResult(desired, false, false);
        }

        Vec3 travel = desired.subtract(from);
        if (travel.lengthSqr() <= MIN_VECTOR_LENGTH_SQR) {
            return new CollisionResult(desired, true, blockHit.getDirection() == Direction.DOWN);
        }

        Vec3 direction = travel.normalize();
        Vec3 corrected = blockHit.getLocation().subtract(direction.scale(TheCamClientConfig.CAMERA_COLLISION_PADDING.get()));
        return new CollisionResult(corrected, true, blockHit.getDirection() == Direction.DOWN);
    }

    private static Vec3 clipCameraPosition(LocalPlayer player, Vec3 from, Vec3 desired) {
        BlockHitResult hit = clipBlock(player, from, desired);
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3 travel = desired.subtract(from);
            if (travel.lengthSqr() > MIN_VECTOR_LENGTH_SQR) {
                Vec3 direction = travel.normalize();
                return hit.getLocation().subtract(direction.scale(TheCamClientConfig.CAMERA_COLLISION_PADDING.get()));
            }
        }
        return desired;
    }

    private static Vec3 computeTightSpaceFallback(LocalPlayer player, Vec3 eyePosition, Vec3 horizontalForward, Vec3 right, Vec3 cameraBack) {
        double minDistance = TheCamClientConfig.CAMERA_MIN_PLAYABLE_DISTANCE.get();
        double side = TheCamClientConfig.CAMERA_SIDE_OFFSET.get() * TheCamClientConfig.CAMERA_TIGHT_SPACE_SIDE_SCALE.get();
        double height = TheCamClientConfig.CAMERA_TIGHT_SPACE_HEIGHT.get();

        Vec3 anchor = eyePosition.add(0.0D, TIGHT_SPACE_ANCHOR_VERTICAL_OFFSET, 0.0D);
        Vec3 fallback = anchor
                .add(cameraBack.scale(minDistance))
                .add(right.scale(side))
                .add(0.0D, height, 0.0D);

        return clipCameraPosition(player, anchor, fallback);
    }

    private static boolean isTooCloseOrCeilingBlocked(Vec3 collisionAnchor, Vec3 targetPosition, boolean ceilingBlocked) {
        if (ceilingBlocked) {
            return true;
        }
        return collisionAnchor.distanceTo(targetPosition) < TheCamClientConfig.CAMERA_MIN_PLAYABLE_DISTANCE.get();
    }

    private static BlockHitResult clipBlock(LocalPlayer player, Vec3 from, Vec3 desired) {
        return player.level().clip(new ClipContext(
                from,
                desired,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player));
    }

    private static Vec3 rightVector(Vec3 horizontalForward) {
        Vec3 forward = horizontalForward.normalize();
        return new Vec3(forward.z, 0.0D, -forward.x).normalize();
    }

    private static float[] rotationFromDirection(Vec3 direction) {
        double x = direction.x;
        double y = direction.y;
        double z = direction.z;
        double horizontal = Math.sqrt(x * x + z * z);
        float yaw = (float) (Math.toDegrees(Math.atan2(z, x)) - 90.0D);
        float pitch = (float) (-Math.toDegrees(Math.atan2(y, horizontal)));
        return new float[] {yaw, pitch};
    }

    private static boolean shouldResetForMovement(Vec3 eyePosition) {
        if (lastEyePosition == null) {
            return false;
        }
        return lastEyePosition.distanceToSqr(eyePosition) > MOVEMENT_RESET_DISTANCE_SQR;
    }

    private static Vec3 getHorizontalForward(LocalPlayer player, float partialTick) {
        Vec3 view = player.getViewVector(partialTick);
        Vec3 horizontal = new Vec3(view.x, 0.0D, view.z);
        if (horizontal.lengthSqr() <= MIN_VECTOR_LENGTH_SQR) {
            float yaw = player.getYRot();
            double rad = Math.toRadians(-yaw);
            horizontal = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
        }
        return horizontal.normalize();
    }

    public record CameraPose(Vec3 origin, Vec3 direction, Vec3 focusPoint, float yaw, float pitch) {}

    private record CollisionResult(Vec3 position, boolean blocked, boolean ceilingBlocked) {}
}
