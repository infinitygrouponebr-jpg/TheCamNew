package infinitygroup.thecamnew.client.aim;

import infinitygroup.thecamnew.network.TheCamAimSyncPayload;
import net.minecraft.world.phys.Vec3;

public final class TheCamClientAimState {
    private static boolean active;
    private static boolean hasAimTarget;
    private static Vec3 origin = Vec3.ZERO;
    private static Vec3 direction = Vec3.ZERO;
    private static Vec3 target = Vec3.ZERO;

    private TheCamClientAimState() {}

    public static void update(TheCamAimSyncPayload payload) {
        if (payload == null || !payload.isFinite()) {
            clear();
            return;
        }

        active = payload.active();
        hasAimTarget = payload.hasAimTarget();
        origin = payload.aimOrigin();
        direction = payload.aimDirection();
        target = payload.aimTarget();
    }

    public static void clear() {
        active = false;
        hasAimTarget = false;
        origin = Vec3.ZERO;
        direction = Vec3.ZERO;
        target = Vec3.ZERO;
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean hasAimTarget() {
        return hasAimTarget;
    }

    public static Vec3 getOrigin() {
        return origin;
    }

    public static Vec3 getDirection() {
        return direction;
    }

    public static Vec3 getTarget() {
        return target;
    }
}
