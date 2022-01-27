package vazkii.akashictome.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
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
        Player player = context.getSender();
        if(player != null) {
        	context.enqueueWork(() -> {
        		ItemStack tomeStack = player.getMainHandItem();
        		InteractionHand hand = InteractionHand.MAIN_HAND;
        		
        		boolean hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == ModItems.tome;
        		if(!hasTome) {
        			tomeStack = player.getOffhandItem();
        			hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == ModItems.tome;
        			hand = InteractionHand.OFF_HAND;
        		}
        		
        		if(hasTome) {
        			ItemStack newStack = MorphingHandler.getShiftStackForMod(tomeStack, modid);
        			System.out.println(newStack);
            		player.setItemInHand(hand, newStack);
        		}
        	});
        }
		
		return true;
	}
	
}
