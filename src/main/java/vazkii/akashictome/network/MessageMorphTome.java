package vazkii.akashictome.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.arl.network.IMessage;

public class MessageMorphTome implements IMessage {
	
	private static final long serialVersionUID = 8242919341713210397L;
	
	public String modid;
	
	public MessageMorphTome() { }
	
	public MessageMorphTome(String modid) {
		this.modid = modid;
	}
	
	@Override
	public boolean receive(NetworkEvent.Context context) {
        PlayerEntity player = context.getSender();
        if(player != null) {
        	context.enqueueWork(() -> {
        		ItemStack tomeStack = player.getHeldItemMainhand();
        		Hand hand = Hand.MAIN_HAND;
        		
        		boolean hasTome = tomeStack != null && tomeStack.getItem() == ModItems.tome;
        		if(!hasTome) {
        			tomeStack = player.getHeldItemOffhand();
        			hasTome = tomeStack != null && tomeStack.getItem() == ModItems.tome;
        			hand = Hand.OFF_HAND;
        		}
        		
        		if(hasTome) {
        			ItemStack newStack = MorphingHandler.getShiftStackForMod(tomeStack, modid);
        			System.out.println(newStack);
            		player.setHeldItem(hand, newStack);
        		}
        	});
        }
		
		return true;
	}
	
}
