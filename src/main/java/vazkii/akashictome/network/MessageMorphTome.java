package vazkii.akashictome.network;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.arl.network.IMessage;

@SuppressWarnings("serial")
public class MessageMorphTome implements IMessage {

	private String modid; // non-final as ARL uses reflection to deser this

	public MessageMorphTome() {
		this("minecraft"); // a sane default?
	}

	public MessageMorphTome(String modid) {
		this.modid = modid;
	}

	@Override
	public boolean receive(NetworkEvent.Context context) {
		Player player = context.getSender();
		if (player != null) {
			context.enqueueWork(() -> {
				ItemStack tomeStack = player.getMainHandItem();
				InteractionHand hand = InteractionHand.MAIN_HAND;

				boolean hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == ModItems.tome;
				if (!hasTome) {
					tomeStack = player.getOffhandItem();
					hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == ModItems.tome;
					hand = InteractionHand.OFF_HAND;
				}

				if (hasTome) {
					ItemStack newStack = MorphingHandler.getShiftStackForMod(tomeStack, modid);
					player.setItemInHand(hand, newStack);
				}
			});
		}

		return true;
	}

}
