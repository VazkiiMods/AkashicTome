package vazkii.akashictome.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import vazkii.akashictome.client.TomeScreen;
import vazkii.akashictome.client.HUDHandler;

public class ClientProxy extends CommonProxy {

	@Override
	public void updateEquippedItem() {
		Minecraft.getInstance().gameRenderer.itemRenderer.resetEquippedProgress(Hand.MAIN_HAND);
	}
	
	@Override
	public void initHUD() {
		MinecraftForge.EVENT_BUS.register(new HUDHandler());
	}
	
	@Override
	public void openTomeGUI(PlayerEntity player, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.player == player)
			mc.displayGuiScreen(new TomeScreen(stack));
	}

}
