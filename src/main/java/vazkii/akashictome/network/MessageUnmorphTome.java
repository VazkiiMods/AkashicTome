package vazkii.akashictome.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registries;

import java.util.function.Supplier;

public class MessageUnmorphTome {
	public MessageUnmorphTome() {}

	public static void serialize(final MessageUnmorphTome msg, final FriendlyByteBuf buf) {}

	public static MessageUnmorphTome deserialize(final FriendlyByteBuf buf) {
		return new MessageUnmorphTome();
	}

	public static void handle(MessageUnmorphTome msg, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		Player player = context.getSender();
		if (player != null) {
			context.enqueueWork(() -> {
				ItemStack stack = player.getMainHandItem();
				if (!stack.isEmpty() && MorphingHandler.isAkashicTome(stack) && !stack.is(Registries.TOME.get())) {
					ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, MorphingHandler.MINECRAFT);
					var inventory = player.getInventory();
					inventory.setItem(inventory.selected, newStack);
					AkashicTome.proxy.updateEquippedItem();
				}
			});
		}
		context.setPacketHandled(true);
	}

}
