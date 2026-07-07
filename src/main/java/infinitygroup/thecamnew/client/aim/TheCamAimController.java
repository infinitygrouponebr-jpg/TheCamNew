package infinitygroup.thecamnew.client.aim;

import net.minecraft.client.player.LocalPlayer;

public final class TheCamAimController {
    private TheCamAimController() {}

    public static void alignPlayerToAim(LocalPlayer player) {
        // Disabled for now to prevent camera/player rotation feedback loops during free aim.
    }
}
