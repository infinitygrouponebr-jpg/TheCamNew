package infinitygroup.thecamnew.common.aim;

import infinitygroup.thecamnew.common.config.TheCamCommonConfig;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class TheCamAimStateStore {
    private static final Map<UUID, TheCamAimState> STATES = new ConcurrentHashMap<>();
    private static final double MAX_VALID_RANGE = 256.0D;
    private static final double MIN_DIRECTION_LENGTH_SQR = 1.0E-8D;

    private TheCamAimStateStore() {}

    public static void update(UUID playerId, boolean active, boolean hasAimTarget, Vec3 aimOrigin, Vec3 aimDirection, Vec3 aimTarget, long gameTime) {
        if (playerId == null) {
            return;
        }

        if (!active) {
            STATES.remove(playerId);
            return;
        }

        if (!isFinite(aimOrigin) || !isFinite(aimDirection) || !isFinite(aimTarget)) {
            STATES.remove(playerId);
            return;
        }

        if (aimDirection.lengthSqr() <= MIN_DIRECTION_LENGTH_SQR) {
            STATES.remove(playerId);
            return;
        }

        Vec3 direction = aimDirection.normalize();
        Vec3 target = aimTarget;
        double maxRange = TheCamCommonConfig.SERVER_MAX_AIM_RANGE.get();

        if (maxRange <= 0.0D || maxRange > MAX_VALID_RANGE) {
            maxRange = MAX_VALID_RANGE;
        }

        double distance = aimOrigin.distanceTo(target);
        if (!Double.isFinite(distance) || distance > maxRange) {
            target = aimOrigin.add(direction.scale(maxRange));
        }

        STATES.put(playerId, new TheCamAimState(true, hasAimTarget, aimOrigin, direction, target, gameTime));
    }

    public static void clear(UUID playerId) {
        if (playerId != null) {
            STATES.remove(playerId);
        }
    }

    public static Optional<TheCamAimState> get(Player player, long currentGameTime) {
        if (player == null) {
            return Optional.empty();
        }

        TheCamAimState state = STATES.get(player.getUUID());
        if (state == null) {
            return Optional.empty();
        }

        long expireTicks = TheCamCommonConfig.SERVER_AIM_STATE_EXPIRE_TICKS.get();
        if (state.isExpired(currentGameTime, expireTicks)) {
            STATES.remove(player.getUUID());
            return Optional.empty();
        }

        return Optional.of(state);
    }

    public static long getAgeTicks(Player player, long currentGameTime) {
        return get(player, currentGameTime).map(state -> state.ageTicks(currentGameTime)).orElse(-1L);
    }

    public static void prune(long currentGameTime) {
        long expireTicks = TheCamCommonConfig.SERVER_AIM_STATE_EXPIRE_TICKS.get();
        STATES.entrySet().removeIf(entry -> entry.getValue().isExpired(currentGameTime, expireTicks));
    }

    private static boolean isFinite(Vec3 vec) {
        return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }
}
