package thecam.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class TheCamAimApi {
    private TheCamAimApi() {}

    public static boolean isAimActive(Player player) {
        return infinitygroup.thecamnew.api.TheCamAimApi.isAimActive(player);
    }

    public static boolean isTheCamAimActive(Player player) {
        return infinitygroup.thecamnew.api.TheCamAimApi.isTheCamAimActive(player);
    }

    public static boolean hasAimTarget(Player player) {
        return infinitygroup.thecamnew.api.TheCamAimApi.hasAimTarget(player);
    }

    public static Vec3 getAimOrigin(Player player) {
        return infinitygroup.thecamnew.api.TheCamAimApi.getAimOrigin(player);
    }

    public static Vec3 getAimDirection(Player player) {
        return infinitygroup.thecamnew.api.TheCamAimApi.getAimDirection(player);
    }

    public static Vec3 getAimTarget(Player player) {
        return infinitygroup.thecamnew.api.TheCamAimApi.getAimTarget(player);
    }
}
