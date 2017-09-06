package vazkii.akashictome.network.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.arl.network.NetworkMessage;

public class MessageMorphTome extends NetworkMessage {

	public String modid;
	
	public MessageMorphTome() { }
	
	public MessageMorphTome(String modid) {
		this.modid = modid;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		EntityPlayer player = context.getServerHandler().player;
		ItemStack tomeStack = player.getHeldItemMainhand();
		EnumHand hand = EnumHand.MAIN_HAND;
		
		boolean hasTome = tomeStack != null && tomeStack.getItem() == ModItems.tome;
		if(!hasTome) {
			tomeStack = player.getHeldItemOffhand();
			hasTome = tomeStack != null && tomeStack.getItem() == ModItems.tome;
			hand = EnumHand.OFF_HAND;
		}
		
		if(!hasTome)
			return null;
		
		ItemStack newStack = MorphingHandler.getShiftStackForMod(tomeStack, modid);
		player.setHeldItem(hand, newStack);
		
		return null;
	}
	
}
