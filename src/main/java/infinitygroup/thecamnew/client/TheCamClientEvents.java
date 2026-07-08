package infinitygroup.thecamnew.client;

import infinitygroup.thecamnew.TheCamNew;
import infinitygroup.thecamnew.client.aim.TheCamClientAimState;
import infinitygroup.thecamnew.client.aim.TheCamActionLookController;
import infinitygroup.thecamnew.client.aim.TheCamFreeAimState;
import infinitygroup.thecamnew.client.camera.TheCamCameraController;
import infinitygroup.thecamnew.client.camera.TheCamCameraController.CameraPose;
import infinitygroup.thecamnew.client.debug.TheCamDebugReporter;
import infinitygroup.thecamnew.client.input.TheCamClientState;
import infinitygroup.thecamnew.common.aim.TheCamAimCalculator;
import infinitygroup.thecamnew.network.TheCamAimSyncPayload;
import infinitygroup.thecamnew.network.TheCamNetworking;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = TheCamNew.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class TheCamClientEvents {
    private TheCamClientEvents() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        TheCamClientState.initializeFromConfig();

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        TheCamFreeAimState.tick(minecraft);

        if (player == null || player.level() == null) {
            TheCamClientAimState.clear();
            TheCamNetworking.sendClearPayload();
            TheCamCameraController.reset();
            return;
        }

        if (!player.isSpectator() && minecraft.options != null) {
            minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }

        CameraPose pose = TheCamCameraController.getLastRenderedPose();
        if (pose == null) {
            pose = TheCamCameraController.computePose(player, 1.0F, false);
        }

        pose = TheCamActionLookController.applyIfNeeded(minecraft, player, pose);

        TheCamAimSyncPayload payload = TheCamFreeAimState.isActive()
                ? TheCamAimCalculator.compute(player, pose, TheCamFreeAimState.cursorNormX(), TheCamFreeAimState.cursorNormY())
                : TheCamAimCalculator.compute(player, pose);
        TheCamClientAimState.update(payload);
        TheCamNetworking.sendAimPayload(payload);
        TheCamDebugReporter.show(player, payload);
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.level() == null || player.isSpectator()) {
            return;
        }

        CameraPose pose = TheCamCameraController.computePose(player, (float) event.getPartialTick());
        event.setYaw(pose.yaw());
        event.setPitch(pose.pitch());
        event.setRoll(0.0F);
        TheCamCameraController.applyToCamera(event.getCamera(), pose);
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("thecamnew")
                        .then(Commands.literal("debug")
                                .then(Commands.literal("aim")
                                        .then(Commands.literal("on").executes(context -> {
                                            TheCamClientState.setDebugEnabled(true);
                                            LocalPlayer player = Minecraft.getInstance().player;
                                            if (player != null) {
                                                player.displayClientMessage(Component.literal("The Cam New debug aim on"), true);
                                            }
                                            return 1;
                                        }))
                                        .then(Commands.literal("off").executes(context -> {
                                            TheCamClientState.setDebugEnabled(false);
                                            LocalPlayer player = Minecraft.getInstance().player;
                                            if (player != null) {
                                                player.displayClientMessage(Component.literal("The Cam New debug aim off"), true);
                                            }
                                            return 1;
                                        })))));
    }
}
