package infinitygroup.thecamnew.client.input;

import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import java.util.Optional;

public final class TheCamClientState {
    private static boolean debugEnabled;
    private static long lastDebugUpdateGameTime;

    private TheCamClientState() {}

    public static void initializeFromConfig() {
        debugEnabled = TheCamClientConfig.DEBUG_AIM.get();
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static void setDebugEnabled(boolean value) {
        debugEnabled = value;
    }

    public static long getLastDebugUpdateGameTime() {
        return lastDebugUpdateGameTime;
    }

    public static void setLastDebugUpdateGameTime(long value) {
        lastDebugUpdateGameTime = value;
    }

    public static Optional<Boolean> isDebugEnabledOptional() {
        return Optional.of(debugEnabled);
    }
}
