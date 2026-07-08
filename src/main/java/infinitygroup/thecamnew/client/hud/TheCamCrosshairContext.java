package infinitygroup.thecamnew.client.hud;

import infinitygroup.thecamnew.client.config.TheCamClientConfig;
import java.util.Set;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;

public final class TheCamCrosshairContext {
    private static final Set<String> DEFAULT_CROSSHAIR_ITEM_IDS = Set.of(
            "thecamarsenal:akm_47",
            "thecamarsenal:scarm");

    private TheCamCrosshairContext() {}

    public static boolean shouldShowCrosshair(LocalPlayer player) {
        if (player == null) {
            return false;
        }

        if (!TheCamClientConfig.CROSSHAIR_ONLY_WITH_TOOL_OR_WEAPON.get()) {
            return true;
        }

        return isRelevantHeldItem(player.getMainHandItem());
    }

    public static boolean shouldUseCombatFraming(LocalPlayer player) {
        return player != null
                && TheCamClientConfig.COMBAT_FRAMING_WHEN_HOLDING_TOOL_OR_WEAPON.get()
                && isRelevantHeldItem(player.getMainHandItem());
    }

    public static boolean isRelevantHeldItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.is(ItemTags.SWORDS)
                || stack.is(ItemTags.PICKAXES)
                || stack.is(ItemTags.AXES)
                || stack.is(ItemTags.SHOVELS)
                || stack.is(ItemTags.HOES)) {
            return true;
        }

        return stack.getItem() instanceof ProjectileWeaponItem
                || stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof ShieldItem
                || isWhitelistedItem(stack);
    }

    private static boolean isWhitelistedItem(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId != null && DEFAULT_CROSSHAIR_ITEM_IDS.contains(itemId.toString());
    }
}
