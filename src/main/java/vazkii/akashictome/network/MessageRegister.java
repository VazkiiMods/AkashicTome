package vazkii.akashictome.network;

import net.minecraftforge.fml.relauncher.Side;
import vazkii.akashictome.network.message.MessageMorphTome;
import vazkii.akashictome.network.message.MessageUnmorphTome;
import vazkii.arl.network.NetworkHandler;

public class MessageRegister {

	public static void init() {
		NetworkHandler.register(MessageMorphTome.class, Side.SERVER);
		NetworkHandler.register(MessageUnmorphTome.class, Side.SERVER);
	}

	
}
