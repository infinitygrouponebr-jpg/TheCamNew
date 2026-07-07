package infinitygroup.thecamnew.client;

import infinitygroup.thecamnew.TheCamNew;
import infinitygroup.thecamnew.client.hud.TheCamCrosshairOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = TheCamNew.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TheCamClientModEvents {
    private static final ResourceLocation RDR2_CROSSHAIR_LAYER = ResourceLocation.fromNamespaceAndPath(TheCamNew.MODID, "rdr2_crosshair");

    private TheCamClientModEvents() {}

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, RDR2_CROSSHAIR_LAYER, TheCamCrosshairOverlay::render);
    }
}
