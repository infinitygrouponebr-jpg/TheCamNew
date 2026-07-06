package infinitygroup.thecamnew.client.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TheCamClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Camera
    public static final ModConfigSpec.DoubleValue CAMERA_DISTANCE = BUILDER
            .comment("Third-person camera distance.")
            .defineInRange("cameraDistance", 3.6D, 0.0D, 32.0D);

    public static final ModConfigSpec.DoubleValue CAMERA_HEIGHT = BUILDER
            .comment("Vertical camera offset.")
            .defineInRange("cameraHeight", 1.15D, -8.0D, 16.0D);

    public static final ModConfigSpec.DoubleValue CAMERA_SIDE_OFFSET = BUILDER
            .comment("Horizontal camera offset.")
            .defineInRange("cameraSideOffset", 1.25D, -8.0D, 8.0D);

    public static final ModConfigSpec.DoubleValue CAMERA_FORWARD_FOCUS_OFFSET = BUILDER
            .comment("Forward offset for the camera focus point.")
            .defineInRange("cameraForwardFocusOffset", 2.0D, -8.0D, 16.0D);

    public static final ModConfigSpec.DoubleValue CAMERA_PITCH_OFFSET = BUILDER
            .comment("Camera pitch offset in degrees.")
            .defineInRange("cameraPitchOffset", 0.0D, -90.0D, 90.0D);

    public static final ModConfigSpec.DoubleValue CAMERA_SMOOTHNESS = BUILDER
            .comment("Camera interpolation factor.")
            .defineInRange("cameraSmoothness", 0.12D, 0.0D, 1.0D);

    // Collision
    public static final ModConfigSpec.BooleanValue CAMERA_COLLISION = BUILDER
            .comment("Prevent the camera from clipping through blocks.")
            .define("cameraCollision", true);

    public static final ModConfigSpec.DoubleValue CAMERA_COLLISION_PADDING = BUILDER
            .comment("Padding kept away from walls when camera collision is applied.")
            .defineInRange("cameraCollisionPadding", 0.25D, 0.0D, 2.0D);

    public static final ModConfigSpec.DoubleValue CAMERA_COLLISION_SMOOTHNESS = BUILDER
            .comment("How quickly the camera returns from collision.")
            .defineInRange("cameraCollisionSmoothness", 0.20D, 0.0D, 1.0D);

    public static final ModConfigSpec.DoubleValue CAMERA_MIN_PLAYABLE_DISTANCE = BUILDER
            .comment("Minimum stable camera distance in tight spaces.")
            .defineInRange("cameraMinPlayableDistance", 1.25D, 0.3D, 4.0D);

    public static final ModConfigSpec.DoubleValue CAMERA_TIGHT_SPACE_SIDE_SCALE = BUILDER
            .comment("Side offset multiplier used in tight spaces.")
            .defineInRange("cameraTightSpaceSideScale", 0.35D, 0.0D, 1.0D);

    public static final ModConfigSpec.DoubleValue CAMERA_TIGHT_SPACE_HEIGHT = BUILDER
            .comment("Height offset used in tight spaces.")
            .defineInRange("cameraTightSpaceHeight", 0.35D, -1.0D, 2.0D);

    // Aim
    public static final ModConfigSpec.DoubleValue AIM_RANGE = BUILDER
            .comment("Maximum client-side aim range.")
            .defineInRange("aimRange", 128.0D, 1.0D, 256.0D);

    // Visual
    public static final ModConfigSpec.BooleanValue DEBUG_AIM = BUILDER
            .comment("Show client debug aim info.")
            .define("debugAim", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private TheCamClientConfig() {}
}
