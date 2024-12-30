package vazkii.akashictome.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registries;

public record MessageUnmorphTome() implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, MessageUnmorphTome> CODEC = CustomPacketPayload.codec(
			MessageUnmorphTome::serialize,
			MessageUnmorphTome::new);

	public static final Type<MessageUnmorphTome> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(AkashicTome.MOD_ID, "unmorph_tome"));

	public MessageUnmorphTome(final FriendlyByteBuf buf) {
		this();
	}

	public void serialize(final FriendlyByteBuf buf) {}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static void handle(MessageUnmorphTome msg, final IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.player() instanceof ServerPlayer player) {
				ItemStack stack = player.getMainHandItem();
				if (!stack.isEmpty() && MorphingHandler.isAkashicTome(stack) && !stack.is(Registries.TOME.get())) {
					ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, MorphingHandler.MINECRAFT);
					var inventory = player.getInventory();
					inventory.setItem(inventory.selected, newStack);
					AkashicTome.proxy.updateEquippedItem();
				}
			}
		});
	}

}
