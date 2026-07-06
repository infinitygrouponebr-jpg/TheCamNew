package infinitygroup.thecamnew.network;

import infinitygroup.thecamnew.TheCamNew;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record TheCamAimSyncPayload(
        boolean active,
        boolean hasAimTarget,
        Vec3 aimOrigin,
        Vec3 aimDirection,
        Vec3 aimTarget) implements CustomPacketPayload {

    public static final Type<TheCamAimSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TheCamNew.MODID, "aim_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TheCamAimSyncPayload> STREAM_CODEC = StreamCodec.ofMember(
            TheCamAimSyncPayload::write,
            TheCamAimSyncPayload::new);

    public static TheCamAimSyncPayload clear() {
        return new TheCamAimSyncPayload(false, false, new Vec3(0.0D, 0.0D, 0.0D), new Vec3(0.0D, 0.0D, 0.0D), new Vec3(0.0D, 0.0D, 0.0D));
    }

    public TheCamAimSyncPayload(RegistryFriendlyByteBuf buf) {
        this(
                buf.readBoolean(),
                buf.readBoolean(),
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(active);
        buf.writeBoolean(hasAimTarget);
        buf.writeDouble(aimOrigin.x);
        buf.writeDouble(aimOrigin.y);
        buf.writeDouble(aimOrigin.z);
        buf.writeDouble(aimDirection.x);
        buf.writeDouble(aimDirection.y);
        buf.writeDouble(aimDirection.z);
        buf.writeDouble(aimTarget.x);
        buf.writeDouble(aimTarget.y);
        buf.writeDouble(aimTarget.z);
    }

    public boolean isFinite() {
        return isFiniteVec(aimOrigin) && isFiniteVec(aimDirection) && isFiniteVec(aimTarget);
    }

    private static boolean isFiniteVec(Vec3 vec) {
        return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
