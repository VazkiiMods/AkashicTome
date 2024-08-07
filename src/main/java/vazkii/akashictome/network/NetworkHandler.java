package vazkii.akashictome.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import vazkii.akashictome.AkashicTome;

public class NetworkHandler {
    private static SimpleChannel channel;
    private static int id = 0;

    public static void register() {
        final String protocolVersion = "1";
        channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(AkashicTome.MOD_ID, "main"))
                .networkProtocolVersion(() -> protocolVersion)
                .clientAcceptedVersions(protocolVersion::equals)
                .serverAcceptedVersions(protocolVersion::equals)
                .simpleChannel();
        channel.registerMessage(id++, MessageMorphTome.class, MessageMorphTome::serialize, MessageMorphTome::deserialize, MessageMorphTome::handle);
        channel.registerMessage(id++, MessageUnmorphTome.class, MessageUnmorphTome::serialize, MessageUnmorphTome::deserialize, MessageUnmorphTome::handle);
    }

    public static <MSG> void sendToServer(MSG msg) {
        channel.sendToServer(msg);
    }

}
