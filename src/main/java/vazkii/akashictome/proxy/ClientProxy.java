package vazkii.akashictome.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.model.ModelLoader;
import vazkii.akashictome.ConfigHandler;
import vazkii.akashictome.AkashicTome;

public class ClientProxy extends CommonProxy {

	@Override
	public void updateEquippedItem() {
		Minecraft.getMinecraft().entityRenderer.itemRenderer.resetEquippedProgress(EnumHand.MAIN_HAND); 
	}

}
