package vazkii.akashictome.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.botania.api.wiki.IWikiProvider;
import vazkii.botania.api.wiki.WikiHooks;

public class HUDHandler {

	@SubscribeEvent
	public void onDrawScreen(RenderGameOverlayEvent.Post event) {
		if(event.getType() != ElementType.ALL)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult pos = mc.objectMouseOver;
		ScaledResolution res = event.getResolution();
		
		if(pos != null && pos.typeOfHit == RayTraceResult.Type.BLOCK) {
			ItemStack tomeStack = mc.player.getHeldItemMainhand();
			
			boolean hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == ModItems.tome;
			if(!hasTome) {
				tomeStack = mc.player.getHeldItemOffhand();
				hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == ModItems.tome;
			}
			
			if(!hasTome)
				return;
			
			tomeStack = tomeStack.copy();
			
			IBlockState state = mc.world.getBlockState(pos.getBlockPos());
			Block block = state.getBlock();
			
			if(!block.isAir(state, mc.world, pos.getBlockPos()) && !(block instanceof BlockLiquid)) {
				ItemStack drawStack = ItemStack.EMPTY;
				String line1 = "";
				String line2 = "";
				
				String mod = MorphingHandler.getModFromState(state);
				ItemStack morphStack = MorphingHandler.getShiftStackForMod(tomeStack, mod);
				if(!morphStack.isEmpty() && !ItemStack.areItemsEqual(morphStack, tomeStack)) {
					drawStack = morphStack;
					line1 = ItemNBTHelper.getString(morphStack, MorphingHandler.TAG_TOME_DISPLAY_NAME, "N/A");
					line2 = TextFormatting.GRAY + I18n.format("akashictome.clickMorph");
				} else {
					IWikiProvider provider = WikiHooks.getWikiFor(block);
					String url = provider.getWikiURL(mc.world, pos, mc.player);
					if(url != null && !url.isEmpty()) {
						drawStack = new ItemStack(ModItems.tome);
						line1 = provider.getBlockName(mc.world, pos, mc.player);
						line2 = "@ " + TextFormatting.AQUA + provider.getWikiName(mc.world, pos, mc.player);
					}
				}
				
				if(!drawStack.isEmpty()) {
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					int sx = res.getScaledWidth() / 2 - 17;
					int sy = res.getScaledHeight() / 2 + 2;

					mc.getRenderItem().renderItemIntoGUI(drawStack, sx, sy);
					GlStateManager.disableLighting();
					mc.fontRenderer.drawStringWithShadow(line1, sx + 20, sy + 4, 0xFFFFFFFF);
					mc.fontRenderer.drawStringWithShadow(line2, sx + 25, sy + 14, 0xFFFFFFFF);
					GlStateManager.color(1F, 1F, 1F, 1F);
				}
			}
		}
	}
	
}
