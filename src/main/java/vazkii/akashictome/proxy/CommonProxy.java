package vazkii.akashictome.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.akashictome.ConfigHandler;
import vazkii.akashictome.ItemTome;
import vazkii.akashictome.MessageMorphTome;
import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.WikiFallback;
import vazkii.arl.network.NetworkHandler;
import vazkii.botania.api.wiki.WikiHooks;

public class CommonProxy {

	public void preInit() {
		ModItems.init();
		
//		GameRegistry.addShapedRecipe(new ItemStack(tome),
//				" GB", " IR", "I  ",
//				'G', new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage()),
//				'B', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
//				'R', new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()),
//				'I', new ItemStack(Items.IRON_INGOT)); TODO
		
		MinecraftForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
		initHUD();
		
		NetworkHandler.register(MessageMorphTome.class, Side.SERVER);
		
		if(!Loader.isModLoaded("Botania"))
			WikiFallback.doWikiRegister();
	}
	
	public void updateEquippedItem() {
		// NO-OP
	}

	public void initHUD() {
		// NO-OP
	}
	
	public void openTomeGUI(ItemStack stack) {
		// NO-OP
	}
	
	public boolean openWikiPage(World world, Block block, RayTraceResult pos) {
		return false;
	}
	
}
