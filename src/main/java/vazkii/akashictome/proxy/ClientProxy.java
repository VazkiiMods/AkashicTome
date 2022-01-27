package vazkii.akashictome.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.common.MinecraftForge;
import vazkii.akashictome.client.TomeScreen;
import vazkii.akashictome.client.HUDHandler;

public class ClientProxy extends CommonProxy {

	@Override
	public void updateEquippedItem() {
		Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.MAIN_HAND);
	}
	
	@Override
	public void initHUD() {
		MinecraftForge.EVENT_BUS.register(new HUDHandler());
	}
	
	@Override
	public void openTomeGUI(Player player, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.player == player)
			mc.setScreen(new TomeScreen(stack));
	}

}
