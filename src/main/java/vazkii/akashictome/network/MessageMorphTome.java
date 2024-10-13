package vazkii.akashictome.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registries;

public record MessageMorphTome(String modid) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, MessageMorphTome> CODEC = CustomPacketPayload.codec(
			MessageMorphTome::serialize,
			MessageMorphTome::new);
	public static final Type<MessageMorphTome> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(AkashicTome.MOD_ID, "morph_tome"));

	public MessageMorphTome(final FriendlyByteBuf buf) {
		this(buf.readUtf());
	}

	public void serialize(final FriendlyByteBuf buf) {
		buf.writeUtf(modid);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static void handle(MessageMorphTome msg, final IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.player() instanceof ServerPlayer player) {
				ItemStack tomeStack = player.getMainHandItem();
				InteractionHand hand = InteractionHand.MAIN_HAND;

				boolean hasTome = !tomeStack.isEmpty() && tomeStack.is(Registries.TOME.get());
				if (!hasTome) {
					tomeStack = player.getOffhandItem();
					hasTome = !tomeStack.isEmpty() && tomeStack.is(Registries.TOME.get());
					hand = InteractionHand.OFF_HAND;
				}

				if (hasTome) {
					ItemStack newStack = MorphingHandler.getShiftStackForMod(tomeStack, msg.modid, player.registryAccess());
					player.setItemInHand(hand, newStack);
				}
			}
		})
				.exceptionally(e -> {
					// Handle exception
					ctx.disconnect(Component.translatable("akashictome.networking.morph_tome.failed", e.getMessage()));
					return null;
				});
	}
}
