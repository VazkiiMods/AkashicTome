package vazkii.akashictome.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.arl.network.IMessage;

public class MessageUnmorphTome implements IMessage {

	private static final long serialVersionUID = 836964163475506394L;

	public MessageUnmorphTome() { }

	@Override
	public boolean receive(NetworkEvent.Context context) {
		PlayerEntity player = context.getSender();
		if(player != null) {		
			context.enqueueWork(() -> {
				ItemStack stack = player.getHeldItemMainhand();
				if(stack != null && MorphingHandler.isAkashicTome(stack) && stack.getItem() != ModItems.tome) {
					ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, MorphingHandler.MINECRAFT);
					player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
					AkashicTome.proxy.updateEquippedItem();
				}
			});
		}

		return true;
	}

}
