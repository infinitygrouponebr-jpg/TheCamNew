package infinitygroup.thecamnew.client.camera;

import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import infinitygroup.thecamnew.mixin.CameraAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;

public final class TheCamCameraController {
    private static Vec3 lastCameraPosition;
    private static Vec3 lastEyePosition;
    private static double lastCollisionDistance = Double.NaN;

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

        Vec3 focus = eyePosition
                .add(horizontalForward.scale(TheCamClientConfig.CAMERA_FORWARD_FOCUS_OFFSET.get()))
                .add(0.0D, 0.12D, 0.0D);

        double targetCollisionDistance = TheCamClientConfig.CAMERA_DISTANCE.get();
        Vec3 desiredPosition = focus
                .add(cameraBack.scale(targetCollisionDistance))
                .add(right.scale(TheCamClientConfig.CAMERA_SIDE_OFFSET.get()))
                .add(0.0D, TheCamClientConfig.CAMERA_HEIGHT.get(), 0.0D);

        if (desiredPosition.subtract(focus).dot(horizontalForward) > 0.0D) {
            desiredPosition = focus
                    .add(horizontalForward.scale(-TheCamClientConfig.CAMERA_DISTANCE.get()))
                    .add(right.scale(TheCamClientConfig.CAMERA_SIDE_OFFSET.get()))
                    .add(0.0D, TheCamClientConfig.CAMERA_HEIGHT.get(), 0.0D);
        }

        if (TheCamClientConfig.CAMERA_COLLISION.get()) {
            Vec3 collisionPosition = clipCamera(player, focus, desiredPosition);
            targetCollisionDistance = focus.distanceTo(collisionPosition);
        }

        double collisionDistance = targetCollisionDistance;
        if (updateSmoothingState) {
            if (Double.isNaN(lastCollisionDistance)) {
                lastCollisionDistance = targetCollisionDistance;
            } else {
                lastCollisionDistance = Mth.lerp(TheCamClientConfig.CAMERA_COLLISION_SMOOTHNESS.get(), lastCollisionDistance, targetCollisionDistance);
            }
            collisionDistance = lastCollisionDistance;
        }

        Vec3 smoothedPosition = focus
                .add(cameraBack.scale(collisionDistance))
                .add(right.scale(TheCamClientConfig.CAMERA_SIDE_OFFSET.get()))
                .add(0.0D, TheCamClientConfig.CAMERA_HEIGHT.get(), 0.0D);

        if (TheCamClientConfig.CAMERA_COLLISION.get()) {
            smoothedPosition = clipCamera(player, focus, smoothedPosition);
        }

        double smoothness = TheCamClientConfig.CAMERA_SMOOTHNESS.get();
        Vec3 smoothed = smoothedPosition;
        if (updateSmoothingState && lastCameraPosition != null) {
            smoothed = lastCameraPosition.lerp(smoothedPosition, smoothness);
        }
        if (updateSmoothingState) {
            lastCameraPosition = smoothed;
            lastEyePosition = eyePosition;
        }

        Vec3 direction = focus.subtract(smoothed).normalize();
        float[] cameraAngles = rotationFromDirection(direction);
        return new CameraPose(smoothed, direction, focus, cameraAngles[0], cameraAngles[1]);
    }

    public static void applyToCamera(Camera camera, CameraPose pose) {
        ((CameraAccessor) camera).thecamnew$setPosition(pose.origin().x, pose.origin().y, pose.origin().z);
    }

    public static void reset() {
        lastCameraPosition = null;
        lastEyePosition = null;
        lastCollisionDistance = Double.NaN;
    }

    private static Vec3 clipCamera(LocalPlayer player, Vec3 focus, Vec3 desired) {
        HitResult hit = player.level().clip(new ClipContext(focus, desired, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3 travel = desired.subtract(focus);
            if (travel.lengthSqr() > 1.0E-8D) {
                Vec3 direction = travel.normalize();
                return hit.getLocation().subtract(direction.scale(TheCamClientConfig.CAMERA_COLLISION_PADDING.get()));
            }
        }
        return desired;
    }

    private static Vec3 rightVector(Vec3 horizontalForward) {
        Vec3 forward = horizontalForward.normalize();
        return new Vec3(forward.z, 0.0D, -forward.x).normalize();
    }

    private static Vec3 directionFromRotation(float pitchDegrees, float yawDegrees) {
        float pitch = pitchDegrees * ((float) Math.PI / 180.0F);
        float yaw = -yawDegrees * ((float) Math.PI / 180.0F) - (float) Math.PI;
        float cosPitch = Mth.cos(pitch);
        float sinPitch = Mth.sin(pitch);
        float cosYaw = Mth.cos(yaw);
        float sinYaw = Mth.sin(yaw);
        return new Vec3((double) (sinYaw * cosPitch), (double) sinPitch, (double) (cosYaw * cosPitch));
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
        return lastEyePosition.distanceToSqr(eyePosition) > 64.0D;
    }

    private static Vec3 getHorizontalForward(LocalPlayer player, float partialTick) {
        Vec3 view = player.getViewVector(partialTick);
        Vec3 horizontal = new Vec3(view.x, 0.0D, view.z);
        if (horizontal.lengthSqr() <= 1.0E-8D) {
            float yaw = player.getYRot();
            double rad = Math.toRadians(-yaw);
            horizontal = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
        }
        return horizontal.normalize();
    }

    public record CameraPose(Vec3 origin, Vec3 direction, Vec3 focusPoint, float yaw, float pitch) {}
}
