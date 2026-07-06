package infinitygroup.thecamnew.common;

import infinitygroup.thecamnew.TheCamNew;
import infinitygroup.thecamnew.common.aim.TheCamAimStateStore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = TheCamNew.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class TheCamServerEvents {
    private TheCamServerEvents() {}

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        TheCamAimStateStore.prune(event.getServer().overworld().getGameTime());
    }
}
