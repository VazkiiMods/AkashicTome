package vazkii.akashictome.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import vazkii.akashictome.MorphingHandler;

public class CommonProxy {

	public void preInit() {
		NeoForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
		initHUD();
	}

	public void updateEquippedItem() {
		// NO-OP
	}

	public void initHUD() {
		// NO-OP
	}

	public void openTomeGUI(Player player, ItemStack stack) {
		// NO-OP
	}

}
