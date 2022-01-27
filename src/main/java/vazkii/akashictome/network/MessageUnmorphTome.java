package vazkii.akashictome.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.arl.network.IMessage;

public class MessageUnmorphTome implements IMessage {

	private static final long serialVersionUID = 836964163475506394L;

	public MessageUnmorphTome() { }

	@Override
	public boolean receive(NetworkEvent.Context context) {
		Player player = context.getSender();
		if(player != null) {		
			context.enqueueWork(() -> {
				ItemStack stack = player.getMainHandItem();
				if(!stack.isEmpty() && MorphingHandler.isAkashicTome(stack) && stack.getItem() != ModItems.tome) {
					ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, MorphingHandler.MINECRAFT);
					var inventory = player.getInventory();
					inventory.setItem(inventory.selected, newStack);
					AkashicTome.proxy.updateEquippedItem();
				}
			});
		}

		return true;
	}

}
