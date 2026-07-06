package infinitygroup.thecamnew.api;

import infinitygroup.thecamnew.common.aim.TheCamAimState;
import infinitygroup.thecamnew.common.aim.TheCamAimStateStore;
import infinitygroup.thecamnew.common.config.TheCamCommonConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class TheCamAimApi {
    private TheCamAimApi() {}

    public static boolean isAimActive(Player player) {
        return getState(player).isPresent();
    }

    public static boolean isTheCamAimActive(Player player) {
        return isAimActive(player);
    }

    public static boolean hasAimTarget(Player player) {
        long currentGameTime = currentGameTime(player);
        return getState(player).map(state -> !state.isExpired(currentGameTime, TheCamCommonConfig.SERVER_AIM_STATE_EXPIRE_TICKS.get()) && state.hasAimTarget()).orElse(false);
    }

    public static Vec3 getAimOrigin(Player player) {
        return getState(player).map(TheCamAimState::aimOrigin).orElseGet(() -> fallbackOrigin(player));
    }

    public static Vec3 getAimDirection(Player player) {
        return getState(player).map(TheCamAimState::aimDirection).orElseGet(() -> fallbackDirection(player));
    }

    public static Vec3 getAimTarget(Player player) {
        return getState(player).map(TheCamAimState::aimTarget).orElseGet(() -> {
            Vec3 origin = fallbackOrigin(player);
            Vec3 direction = fallbackDirection(player);
            return origin.add(direction.scale(TheCamCommonConfig.SERVER_MAX_AIM_RANGE.get()));
        });
    }

    private static java.util.Optional<TheCamAimState> getState(Player player) {
        if (player == null || player.level() == null) {
            return java.util.Optional.empty();
        }

        long gameTime = player.level().getGameTime();
        return TheCamAimStateStore.get(player, gameTime);
    }

    private static long currentGameTime(Player player) {
        return player != null && player.level() != null ? player.level().getGameTime() : 0L;
    }

    private static Vec3 fallbackOrigin(Player player) {
        return player != null ? player.getEyePosition(1.0F) : new Vec3(0.0D, 0.0D, 0.0D);
    }

    private static Vec3 fallbackDirection(Player player) {
        Vec3 direction = player != null ? player.getViewVector(1.0F) : new Vec3(0.0D, 0.0D, 1.0D);
        return direction.lengthSqr() > 0.0D ? direction.normalize() : new Vec3(0.0D, 0.0D, 1.0D);
    }
}
