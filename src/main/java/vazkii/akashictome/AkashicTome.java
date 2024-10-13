package vazkii.akashictome;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import vazkii.akashictome.network.NetworkHandler;
import vazkii.akashictome.proxy.ClientProxy;
import vazkii.akashictome.proxy.CommonProxy;

@Mod(AkashicTome.MOD_ID)
public class AkashicTome {

	public static final String MOD_ID = "akashictome";
	public static CommonProxy proxy;

	public AkashicTome(IEventBus bus, ModContainer container, Dist dist) {
		bus.addListener(NetworkHandler::register);

		Registries.COMPONENT_TYPES.register(bus);
		Registries.ITEMS.register(bus);
		Registries.SERIALIZERS.register(bus);

		bus.addListener(this::addToCreativeTab);
		container.registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC);

		proxy = dist.isClient() ? new ClientProxy() : new CommonProxy();
		proxy.preInit();
	}

	private void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(Registries.TOME);
		}
	}
}
