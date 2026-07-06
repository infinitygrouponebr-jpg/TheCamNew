package infinitygroup.thecamnew;

import com.mojang.logging.LogUtils;
import infinitygroup.thecamnew.common.config.TheCamCommonConfig;
import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import infinitygroup.thecamnew.network.TheCamNetworking;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(TheCamNew.MODID)
public final class TheCamNew {
    public static final String MODID = "the_cam_new";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TheCamNew(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(TheCamNetworking::registerPayloadHandlers);

        modContainer.registerConfig(ModConfig.Type.CLIENT, TheCamClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, TheCamCommonConfig.SPEC);
    }
}
