package vazkii.akashictome.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registries;

import java.util.function.Supplier;

public class MessageMorphTome {
	public String modid;

	public MessageMorphTome() {}

	public MessageMorphTome(String modid) {
		this.modid = modid;
	}

	public static void serialize(final MessageMorphTome msg, final FriendlyByteBuf buf) {
		buf.writeUtf(msg.modid);
	}

	public static MessageMorphTome deserialize(final FriendlyByteBuf buf) {
		final MessageMorphTome msg = new MessageMorphTome();
		msg.modid = buf.readUtf();
		return msg;
	}

	public static void handle(MessageMorphTome msg, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		Player player = context.getSender();
		if (player != null) {
			context.enqueueWork(() -> {
				ItemStack tomeStack = player.getMainHandItem();
				InteractionHand hand = InteractionHand.MAIN_HAND;

				boolean hasTome = !tomeStack.isEmpty() && tomeStack.is(Registries.TOME.get());
				if (!hasTome) {
					tomeStack = player.getOffhandItem();
					hasTome = !tomeStack.isEmpty() && tomeStack.is(Registries.TOME.get());
					hand = InteractionHand.OFF_HAND;
				}

				if (hasTome) {
					ItemStack newStack = MorphingHandler.getShiftStackForMod(tomeStack, msg.modid);
					player.setItemInHand(hand, newStack);
				}
			});
		}
		context.setPacketHandled(true);
	}
}
