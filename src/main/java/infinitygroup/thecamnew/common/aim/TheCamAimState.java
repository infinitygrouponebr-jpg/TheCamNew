package infinitygroup.thecamnew.common.aim;

import net.minecraft.world.phys.Vec3;

public final class TheCamAimState {
    private final boolean active;
    private final boolean hasAimTarget;
    private final Vec3 aimOrigin;
    private final Vec3 aimDirection;
    private final Vec3 aimTarget;
    private final long lastUpdateGameTime;

    public TheCamAimState(boolean active, boolean hasAimTarget, Vec3 aimOrigin, Vec3 aimDirection, Vec3 aimTarget, long lastUpdateGameTime) {
        this.active = active;
        this.hasAimTarget = hasAimTarget;
        this.aimOrigin = aimOrigin;
        this.aimDirection = aimDirection;
        this.aimTarget = aimTarget;
        this.lastUpdateGameTime = lastUpdateGameTime;
    }

    public boolean active() {
        return active;
    }

    public boolean hasAimTarget() {
        return hasAimTarget;
    }

    public Vec3 aimOrigin() {
        return aimOrigin;
    }

    public Vec3 aimDirection() {
        return aimDirection;
    }

    public Vec3 aimTarget() {
        return aimTarget;
    }

    public long lastUpdateGameTime() {
        return lastUpdateGameTime;
    }

    public long ageTicks(long currentGameTime) {
        return Math.max(0L, currentGameTime - lastUpdateGameTime);
    }

    public boolean isExpired(long currentGameTime, long expireTicks) {
        return ageTicks(currentGameTime) > expireTicks;
    }
}
