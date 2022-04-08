package vazkii.akashictome;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import vazkii.akashictome.network.MessageMorphTome;
import vazkii.akashictome.network.MessageUnmorphTome;
import vazkii.akashictome.proxy.ClientProxy;
import vazkii.akashictome.proxy.CommonProxy;
import vazkii.arl.network.IMessage;
import vazkii.arl.network.NetworkHandler;

@Mod(AkashicTome.MOD_ID)
public class AkashicTome {

	public static final String MOD_ID = "akashictome";
	public static NetworkHandler NETWORKHANDLER;
	public static CommonProxy proxy;

	public AkashicTome() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonSetup);
		bus.register(ModItems.class);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC);

		proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
		proxy.preInit();

		NETWORKHANDLER = new NetworkHandler(MOD_ID, 1);
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		NETWORKHANDLER.register(MessageMorphTome.class, NetworkDirection.PLAY_TO_SERVER);
		NETWORKHANDLER.register(MessageUnmorphTome.class, NetworkDirection.PLAY_TO_SERVER);
	}

	public static void sendToServer(IMessage msg) {
		NETWORKHANDLER.sendToServer(msg);
	}

}
