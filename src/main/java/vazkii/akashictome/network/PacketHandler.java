package vazkii.akashictome.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.network.message.MessageMorphTome;
import vazkii.akashictome.network.message.MessageUnmorphTome;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(AkashicTome.MOD_ID, AkashicTome.MOD_ID),
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void init() {
		CHANNEL.registerMessage(0, MessageMorphTome.class, MessageMorphTome::encode, MessageMorphTome::decode, MessageMorphTome::handle);
		CHANNEL.registerMessage(1, MessageUnmorphTome.class, (msg, buf) -> {}, buf -> new MessageUnmorphTome(), MessageUnmorphTome::handle);
	}

	public static void sendToServer(Object message) {
		CHANNEL.sendToServer(message);
	}
}
