package vazkii.akashictome.proxy;

import java.awt.Desktop;
import java.net.URI;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import vazkii.akashictome.client.GuiTome;
import vazkii.akashictome.client.HUDHandler;
import vazkii.botania.api.wiki.IWikiProvider;
import vazkii.botania.api.wiki.WikiHooks;

public class ClientProxy extends CommonProxy {

	@Override
	public void updateEquippedItem() {
		Minecraft.getMinecraft().entityRenderer.itemRenderer.resetEquippedProgress(EnumHand.MAIN_HAND); 
	}
	
	@Override
	public void initHUD() {
		MinecraftForge.EVENT_BUS.register(new HUDHandler());
	}
	
	@Override
	public void openTomeGUI(EntityPlayer player, ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.player == player)
			mc.displayGuiScreen(new GuiTome(stack));
	}
	
	@Override
	public boolean openWikiPage(World world, Block block, RayTraceResult pos) {
		IWikiProvider wiki = WikiHooks.getWikiFor(block);
		String url = wiki.getWikiURL(world, pos, Minecraft.getMinecraft().player);
		if(url != null && !url.isEmpty()) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

}
