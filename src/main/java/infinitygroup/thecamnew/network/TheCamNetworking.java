package infinitygroup.thecamnew.network;

import infinitygroup.thecamnew.TheCamNew;
import infinitygroup.thecamnew.common.aim.TheCamMeleeAttackHandler;
import infinitygroup.thecamnew.common.aim.TheCamAimStateStore;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class TheCamNetworking {
    private TheCamNetworking() {}

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(TheCamAimSyncPayload.TYPE, TheCamAimSyncPayload.STREAM_CODEC, TheCamNetworking::handleAimSync);
        registrar.playToServer(TheCamMeleeAttackPayload.TYPE, TheCamMeleeAttackPayload.STREAM_CODEC, TheCamNetworking::handleMeleeAttack);
    }

    public static void sendAimPayload(TheCamAimSyncPayload payload) {
        if (payload != null && payload.isFinite() && Minecraft.getInstance().getConnection() != null) {
            PacketDistributor.sendToServer(payload);
        }
    }

    public static void sendClearPayload() {
        if (Minecraft.getInstance().getConnection() != null) {
            PacketDistributor.sendToServer(TheCamAimSyncPayload.clear());
        }
    }

    public static void sendMeleeAttackPayload() {
        if (Minecraft.getInstance().getConnection() != null) {
            PacketDistributor.sendToServer(new TheCamMeleeAttackPayload());
        }
    }

    private static void handleAimSync(TheCamAimSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player;
            try {
                player = context.player();
            } catch (UnsupportedOperationException ex) {
                return;
            }

            if (player == null) {
                return;
            }

            UUID playerId = player.getUUID();
            long gameTime = player.level().getGameTime();

            if (!payload.active()) {
                TheCamAimStateStore.clear(playerId);
                return;
            }

            if (!payload.isFinite()) {
                TheCamNew.LOGGER.warn("Rejected invalid The Cam aim payload from {}", player.getScoreboardName());
                TheCamAimStateStore.clear(playerId);
                return;
            }

            Vec3 origin = payload.aimOrigin();
            Vec3 direction = payload.aimDirection();
            Vec3 target = payload.aimTarget();

            if (direction.lengthSqr() <= 1.0E-8D) {
                TheCamAimStateStore.clear(playerId);
                return;
            }

            TheCamAimStateStore.update(playerId, true, payload.hasAimTarget(), origin, direction, target, gameTime);
        });
    }

    private static void handleMeleeAttack(TheCamMeleeAttackPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player;
            try {
                player = context.player();
            } catch (UnsupportedOperationException ex) {
                return;
            }

            if (player == null) {
                return;
            }

            TheCamMeleeAttackHandler.handle(player);
        });
    }
}
