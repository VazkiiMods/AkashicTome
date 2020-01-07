package vazkii.akashictome.network.message;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registrar;

import java.util.function.Supplier;

public class MessageMorphTome {

	private final String modid;

	public MessageMorphTome(String modid) {
		this.modid = modid;
	}

	public static MessageMorphTome decode(PacketBuffer buffer) {
		return new MessageMorphTome(buffer.readString(64));
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeString(modid, 64);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);

		ServerPlayerEntity player = ctx.get().getSender();
		ItemStack tomeStack = player.getHeldItemMainhand();
		Hand hand = Hand.MAIN_HAND;

		boolean hasTome = tomeStack.getItem() == Registrar.TOME;
		if (!hasTome) {
			tomeStack = player.getHeldItemOffhand();
			hasTome = tomeStack.getItem() == Registrar.TOME;
			hand = Hand.OFF_HAND;
		}

		if (!hasTome)
			return;

		ItemStack newStack = MorphingHandler.getShiftStackForMod(tomeStack, modid);
		player.setHeldItem(hand, newStack);
	}
}
