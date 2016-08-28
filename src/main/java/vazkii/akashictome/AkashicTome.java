package vazkii.akashictome;

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
import vazkii.akashictome.proxy.CommonProxy;

@Mod(modid = AkashicTome.MOD_ID, name = AkashicTome.MOD_NAME, version = AkashicTome.VERSION, dependencies = AkashicTome.DEPENDENCIES, guiFactory = AkashicTome.GUI_FACTORY)
public class AkashicTome {

	public static final String MOD_ID = "AkashicTome";
	public static final String MOD_NAME = "Akashic Tome";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-after:Forge@[12.17.0.1909,);required-before:AutoRegLib";
	public static final String GUI_FACTORY = "vazkii.akashictome.GuiFactory";

	@SidedProxy(clientSide = "vazkii.akashictome.proxy.ClientProxy", serverSide = "vazkii.akashictome.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static Item tome;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event.getSuggestedConfigurationFile());

		tome = new ItemTome();

//		GameRegistry.addShapedRecipe(new ItemStack(tome),
//				" GB", " IR", "I  ",
//				'G', new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage()),
//				'B', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
//				'R', new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()),
//				'I', new ItemStack(Items.IRON_INGOT)); TODO

		MinecraftForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
	}

}
