package vazkii.akashictome;

import akka.io.Tcp.Message;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.akashictome.proxy.CommonProxy;
import vazkii.arl.network.NetworkHandler;

@Mod(modid = AkashicTome.MOD_ID, name = AkashicTome.MOD_NAME, version = AkashicTome.VERSION, dependencies = AkashicTome.DEPENDENCIES, guiFactory = AkashicTome.GUI_FACTORY)
public class AkashicTome {

	public static final String MOD_ID = "akashictome";
	public static final String MOD_NAME = "Akashic Tome";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-before:autoreglib";
	public static final String GUI_FACTORY = "vazkii.akashictome.client.GuiFactory";

	@SidedProxy(clientSide = "vazkii.akashictome.proxy.ClientProxy", serverSide = "vazkii.akashictome.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		
		proxy.preInit();
	}

}
