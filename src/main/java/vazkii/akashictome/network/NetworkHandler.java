package vazkii.akashictome.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {

	public static void registerPayloadHandler(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");

		registrar.playToServer(MessageMorphTome.ID, MessageMorphTome.CODEC, MessageMorphTome::handle);
		registrar.playToServer(MessageUnmorphTome.ID, MessageUnmorphTome.CODEC, MessageUnmorphTome::handle);
	}

	public static void sendToServer(CustomPacketPayload msg) {
		PacketDistributor.sendToServer(msg);
	}

}
