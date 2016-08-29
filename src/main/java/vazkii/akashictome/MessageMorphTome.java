package vazkii.akashictome;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;

public class MessageMorphTome extends NetworkMessage {

	public String modid;
	
	public MessageMorphTome() { }
	
	public MessageMorphTome(String modid) {
		this.modid = modid;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		return super.handleMessage(context);
	}
	
}
