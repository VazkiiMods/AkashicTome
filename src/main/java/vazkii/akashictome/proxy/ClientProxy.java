package vazkii.akashictome.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import vazkii.akashictome.client.HUDHandler;
import vazkii.akashictome.client.TomeScreen;

public class ClientProxy extends CommonProxy {

	@Override
	public void updateEquippedItem() {
		Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.MAIN_HAND);
	}

	@Override
	public void initHUD() {
		NeoForge.EVENT_BUS.register(new HUDHandler());
	}

	@Override
	public void openTomeGUI(Player player, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == player)
			mc.setScreen(new TomeScreen(stack));
	}

}
