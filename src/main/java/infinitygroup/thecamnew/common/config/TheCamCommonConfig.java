package infinitygroup.thecamnew.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TheCamCommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue SERVER_AIM_STATE_EXPIRE_TICKS = BUILDER
            .comment("How long server aim state stays valid without refresh.")
            .defineInRange("serverAimStateExpireTicks", 30, 1, 1200);

    public static final ModConfigSpec.DoubleValue SERVER_MAX_AIM_RANGE = BUILDER
            .comment("Maximum server-side aim range.")
            .defineInRange("serverMaxAimRange", 256.0D, 1.0D, 256.0D);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private TheCamCommonConfig() {}
}
