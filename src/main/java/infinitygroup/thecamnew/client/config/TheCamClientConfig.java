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
    public static final ModConfigSpec.BooleanValue SHOW_CROSSHAIR = BUILDER
            .comment("Show the The Cam New centered crosshair overlay.")
            .define("showCrosshair", true);

    public static final ModConfigSpec.BooleanValue CROSSHAIR_ONLY_WITH_TOOL_OR_WEAPON = BUILDER
            .comment("Only show the centered crosshair when holding a relevant tool or weapon in the main hand.")
            .define("crosshairOnlyWithToolOrWeapon", true);

    public static final ModConfigSpec.BooleanValue COMBAT_FRAMING_WHEN_HOLDING_TOOL_OR_WEAPON = BUILDER
            .comment("Apply combat camera framing when holding a relevant tool or weapon in the main hand.")
            .define("combatFramingWhenHoldingToolOrWeapon", true);

    public static final ModConfigSpec.DoubleValue COMBAT_CAMERA_DISTANCE = BUILDER
            .comment("Third-person camera distance used for combat framing.")
            .defineInRange("combatCameraDistance", 3.3D, 0.0D, 32.0D);

    public static final ModConfigSpec.DoubleValue COMBAT_CAMERA_HEIGHT = BUILDER
            .comment("Vertical camera offset used for combat framing.")
            .defineInRange("combatCameraHeight", 1.05D, -8.0D, 16.0D);

    public static final ModConfigSpec.DoubleValue COMBAT_CAMERA_SIDE_OFFSET = BUILDER
            .comment("Horizontal camera offset used for combat framing.")
            .defineInRange("combatCameraSideOffset", 1.55D, -8.0D, 8.0D);

    public static final ModConfigSpec.DoubleValue COMBAT_CAMERA_FORWARD_FOCUS_OFFSET = BUILDER
            .comment("Forward focus offset used for combat framing.")
            .defineInRange("combatCameraForwardFocusOffset", 2.4D, -8.0D, 16.0D);

    public static final ModConfigSpec.BooleanValue FREE_AIM_ENABLED = BUILDER
            .comment("Enable free aim cursor movement for relevant tools and weapons.")
            .define("freeAimEnabled", true);

    public static final ModConfigSpec.BooleanValue FREE_AIM_CAPTURE_MOUSE = BUILDER
            .comment("Capture gameplay mouse movement into the virtual free aim cursor instead of vanilla turning.")
            .define("freeAimCaptureMouse", true);

    public static final ModConfigSpec.DoubleValue FREE_AIM_MOUSE_SENSITIVITY = BUILDER
            .comment("Virtual free aim cursor sensitivity multiplier.")
            .defineInRange("freeAimMouseSensitivity", 1.0D, 0.01D, 10.0D);

    public static final ModConfigSpec.DoubleValue FREE_AIM_MAX_SCREEN_X = BUILDER
            .comment("Maximum horizontal free aim cursor range in normalized screen space.")
            .defineInRange("freeAimMaxScreenX", 0.85D, 0.0D, 1.0D);

    public static final ModConfigSpec.DoubleValue FREE_AIM_MAX_SCREEN_Y = BUILDER
            .comment("Maximum vertical free aim cursor range in normalized screen space.")
            .defineInRange("freeAimMaxScreenY", 0.70D, 0.0D, 1.0D);

    public static final ModConfigSpec.BooleanValue FREE_AIM_RECENTER_WHEN_INACTIVE = BUILDER
            .comment("Recenter the virtual free aim cursor when free aim becomes inactive.")
            .define("freeAimRecenterWhenInactive", true);

    public static final ModConfigSpec.BooleanValue DEBUG_AIM = BUILDER
            .comment("Show client debug aim info.")
            .define("debugAim", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private TheCamClientConfig() {}
}
