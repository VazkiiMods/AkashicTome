package vazkii.akashictome;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.akashictome.client.HUDHandler;
import vazkii.akashictome.network.PacketHandler;

@Mod(AkashicTome.MOD_ID)
public class AkashicTome {

	public static final String MOD_ID = "akashictome";

	public AkashicTome() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.SPEC);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addGenericListener(Item.class, Registrar::registerItems);
		bus.addGenericListener(IRecipeSerializer.class, Registrar::registerRecipeSerializers);
		bus.addListener(ConfigHandler::onConfigLoad);
		bus.addListener(ConfigHandler::onConfigReload);

		bus.addListener(this::setup);
		bus.addListener(this::clientSetup);
	}

	private void setup(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
		PacketHandler.init();
	}

	private void clientSetup(FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new HUDHandler());
	}

}
