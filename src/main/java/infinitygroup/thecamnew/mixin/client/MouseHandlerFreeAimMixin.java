package infinitygroup.thecamnew.mixin.client;

import infinitygroup.thecamnew.client.aim.TheCamFreeAimState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerFreeAimMixin {
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void thecamnew$captureFreeAim(double movementTime, CallbackInfo ci) {
        if (!TheCamFreeAimState.shouldCaptureMouse(this.minecraft)) {
            return;
        }

        TheCamFreeAimState.addMouseDelta(this.accumulatedDX, this.accumulatedDY);
        ci.cancel();
    }
}
