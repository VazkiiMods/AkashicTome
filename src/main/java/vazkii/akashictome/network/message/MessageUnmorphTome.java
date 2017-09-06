package vazkii.akashictome.network.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.arl.network.NetworkMessage;

public class MessageUnmorphTome extends NetworkMessage {

	public MessageUnmorphTome() { }
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		EntityPlayer player = context.getServerHandler().player;
		ItemStack stack = player.getHeldItemMainhand();
		if(stack != null && MorphingHandler.isAkashicTome(stack) && stack.getItem() != ModItems.tome) {
			ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, MorphingHandler.MINECRAFT);
			player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
			AkashicTome.proxy.updateEquippedItem();
		}
		
		return null;
	}
	
}
