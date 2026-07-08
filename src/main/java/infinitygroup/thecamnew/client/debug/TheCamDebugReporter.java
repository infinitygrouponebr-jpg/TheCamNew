package infinitygroup.thecamnew.client.debug;

import infinitygroup.thecamnew.client.aim.TheCamFreeAimState;
import infinitygroup.thecamnew.client.aim.TheCamVisualAimPoseState;
import infinitygroup.thecamnew.client.input.TheCamClientState;
import infinitygroup.thecamnew.common.aim.TheCamAimStateStore;
import infinitygroup.thecamnew.network.TheCamAimSyncPayload;
import java.util.Locale;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public final class TheCamDebugReporter {
    private TheCamDebugReporter() {}

    public static void show(LocalPlayer player, TheCamAimSyncPayload payload) {
        if (player == null || payload == null || !TheCamClientState.isDebugEnabled()) {
            return;
        }

        long gameTime = player.level().getGameTime();
        long lastShown = TheCamClientState.getLastDebugUpdateGameTime();
        if (gameTime - lastShown < 10L) {
            return;
        }

        TheCamClientState.setLastDebugUpdateGameTime(gameTime);
        long serverAge = TheCamAimStateStore.getAgeTicks(player, gameTime);
        player.displayClientMessage(Component.literal(buildMessage(payload, serverAge)), true);
    }

    public static String buildMessage(TheCamAimSyncPayload payload, long serverAgeTicks) {
        String age = serverAgeTicks >= 0L ? Long.toString(serverAgeTicks) : "n/a";
        return String.format(Locale.ROOT,
                "The Cam | active=%s hit=%s freeAimActive=%s cursor=(%.2f, %.2f) visualPoseActive=%s visualYaw=%.2f visualPitch=%.2f origin=(%s) direction=(%s) target=(%s) serverAge=%s",
                payload.active(),
                payload.hasAimTarget(),
                TheCamFreeAimState.isActive(),
                TheCamFreeAimState.cursorNormX(),
                TheCamFreeAimState.cursorNormY(),
                TheCamVisualAimPoseState.isActive(),
                TheCamVisualAimPoseState.visualYaw(1.0F),
                TheCamVisualAimPoseState.visualPitch(1.0F),
                formatVec(payload.aimOrigin().x, payload.aimOrigin().y, payload.aimOrigin().z),
                formatVec(payload.aimDirection().x, payload.aimDirection().y, payload.aimDirection().z),
                formatVec(payload.aimTarget().x, payload.aimTarget().y, payload.aimTarget().z),
                age);
    }

    private static String formatVec(double x, double y, double z) {
        return String.format(Locale.ROOT, "%.2f, %.2f, %.2f", x, y, z);
    }
}
