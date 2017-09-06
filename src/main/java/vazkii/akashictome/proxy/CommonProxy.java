package vazkii.akashictome.proxy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.WikiFallback;
import vazkii.akashictome.network.MessageRegister;
import vazkii.arl.recipe.RecipeHandler;

public class CommonProxy {

	public void preInit() {
		ModItems.init();
		
		RecipeHandler.addShapelessRecipe(new ItemStack(ModItems.tome), new ItemStack(Items.BOOK), new ItemStack(Blocks.BOOKSHELF));
		RecipeHandler.addShapelessRecipe(new ItemStack(ModItems.tome), new ItemStack(Items.BOOK), "bookshelf");

		MinecraftForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
		initHUD();
		
		MessageRegister.init();
		
		if(!Loader.isModLoaded("Botania"))
			WikiFallback.doWikiRegister();
	}
	
	public void updateEquippedItem() {
		// NO-OP
	}

	public void initHUD() {
		// NO-OP
	}
	
	public void openTomeGUI(EntityPlayer player, ItemStack stack) {
		// NO-OP
	}
	
	public boolean openWikiPage(World world, Block block, RayTraceResult pos) {
		return false;
	}
	
}
