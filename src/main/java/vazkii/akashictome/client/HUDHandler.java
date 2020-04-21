package vazkii.akashictome.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.akashictome.ModItems;
import vazkii.akashictome.MorphingHandler;
import vazkii.arl.util.ItemNBTHelper;

public class HUDHandler {

	@SubscribeEvent
	public void onDrawScreen(RenderGameOverlayEvent.Post event) {
		if(event.getType() != ElementType.ALL)
			return;
		
		Minecraft mc = Minecraft.getInstance();
		RayTraceResult pos = mc.objectMouseOver;
		MainWindow res = event.getWindow();
		
		if(pos != null && pos instanceof BlockRayTraceResult) {
			BlockRayTraceResult bpos = (BlockRayTraceResult) pos;
			ItemStack tomeStack = mc.player.getHeldItemMainhand();
			
			boolean hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == ModItems.tome;
			if(!hasTome) {
				tomeStack = mc.player.getHeldItemOffhand();
				hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == ModItems.tome;
			}
			
			if(!hasTome)
				return;
			
			tomeStack = tomeStack.copy();
			
			BlockState state = mc.world.getBlockState(bpos.getPos());
			Block block = state.getBlock();
			
			if(!block.isAir(state, mc.world, bpos.getPos())) {
				ItemStack drawStack = ItemStack.EMPTY;
				String line1 = "";
				String line2 = "";
				
				String mod = MorphingHandler.getModFromState(state);
				ItemStack morphStack = MorphingHandler.getShiftStackForMod(tomeStack, mod);
				if(!morphStack.isEmpty() && !ItemStack.areItemsEqual(morphStack, tomeStack)) {
					drawStack = morphStack;
					line1 = ItemNBTHelper.getString(morphStack, MorphingHandler.TAG_TOME_DISPLAY_NAME, "N/A");
					line2 = TextFormatting.GRAY + I18n.format("akashictome.click_morph");
				}
				
				if(!drawStack.isEmpty()) {
					RenderSystem.enableBlend();
					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					int sx = res.getScaledWidth() / 2 - 17;
					int sy = res.getScaledHeight() / 2 + 2;

					mc.getItemRenderer().renderItemIntoGUI(drawStack, sx, sy);
					RenderSystem.disableLighting();
					mc.fontRenderer.drawStringWithShadow(line1, sx + 20, sy + 4, 0xFFFFFFFF);
					mc.fontRenderer.drawStringWithShadow(line2, sx + 25, sy + 14, 0xFFFFFFFF);
					RenderSystem.color4f(1F, 1F, 1F, 1F);
				}
			}
		}
	}
	
}
