package vazkii.akashictome.client;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.Registries;

@Mod(value = AkashicTome.MOD_ID, dist = Dist.CLIENT)
public class AkashicTomeClient {

	public AkashicTomeClient(IEventBus bus, ModContainer modContainer) {
		bus.addListener(this::addToCreativeTab);

		modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}

	private void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(Registries.TOME);
		}
	}
}
