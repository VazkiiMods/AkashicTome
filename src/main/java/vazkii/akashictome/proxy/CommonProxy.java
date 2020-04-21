package vazkii.akashictome.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import vazkii.akashictome.MorphingHandler;

public class CommonProxy {

	public void preInit() {
//		RecipeHandler.addShapelessRecipe(new ItemStack(ModItems.tome), new ItemStack(Items.BOOK), new ItemStack(Blocks.BOOKSHELF));
//		RecipeHandler.addShapelessRecipe(new ItemStack(ModItems.tome), new ItemStack(Items.BOOK), "bookshelf"); TODO

		MinecraftForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
		initHUD();
	}
	
	public void updateEquippedItem() {
		// NO-OP
	}

	public void initHUD() {
		// NO-OP
	}
	
	public void openTomeGUI(PlayerEntity player, ItemStack stack) {
		// NO-OP
	}
	
}
