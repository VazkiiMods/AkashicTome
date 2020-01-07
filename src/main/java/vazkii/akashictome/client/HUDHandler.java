package vazkii.akashictome.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
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
import net.minecraftforge.fluids.IFluidBlock;
import org.lwjgl.opengl.GL11;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registrar;

public class HUDHandler {

	@SubscribeEvent
	public void onDrawScreen(RenderGameOverlayEvent.Post event) {
		if (event.getType() != ElementType.ALL)
			return;

		Minecraft mc = Minecraft.getInstance();
		RayTraceResult pos = mc.objectMouseOver;
		MainWindow window = event.getWindow();

		if (pos != null && pos.getType() == RayTraceResult.Type.BLOCK) {
			BlockRayTraceResult result = ((BlockRayTraceResult) pos);
			ItemStack tomeStack = mc.player.getHeldItemMainhand();

			boolean hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == Registrar.TOME;
			if (!hasTome) {
				tomeStack = mc.player.getHeldItemOffhand();
				hasTome = !tomeStack.isEmpty() && tomeStack.getItem() == Registrar.TOME;
			}

			if (!hasTome)
				return;

			tomeStack = tomeStack.copy();

			BlockState state = mc.world.getBlockState(result.getPos());
			Block block = state.getBlock();

			if (!block.isAir(state, mc.world, result.getPos()) && !(block instanceof IFluidBlock || block instanceof FlowingFluidBlock)) {
				ItemStack drawStack = ItemStack.EMPTY;
				String line1 = "";
				String line2 = "";

				String mod = MorphingHandler.getModFromState(state);
				ItemStack morphStack = MorphingHandler.getShiftStackForMod(tomeStack, mod);
				if (!morphStack.isEmpty() && !ItemStack.areItemsEqual(morphStack, tomeStack)) {
					drawStack = morphStack;
					line1 = MorphingHandler.getMorphedDisplayName(morphStack).getFormattedText();
					line2 = TextFormatting.GRAY + I18n.format("akashictome.clickMorph");
				} else { //TODO reimplement/remove wiki hooks
//					IWikiProvider provider = WikiHooks.getWikiFor(block);
//					String url = provider.getWikiURL(mc.world, pos, mc.player);
//					if (url != null && !url.isEmpty()) {
//						drawStack = new ItemStack(Registrar.tome);
//						line1 = provider.getBlockName(mc.world, pos, mc.player);
//						line2 = "@ " + TextFormatting.AQUA + provider.getWikiName(mc.world, pos, mc.player);
//					}
				}

				if (!drawStack.isEmpty()) {
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					int sx = window.getScaledWidth() / 2 - 17;
					int sy = window.getScaledHeight() / 2 + 2;

					mc.getItemRenderer().renderItemIntoGUI(drawStack, sx, sy);
					GlStateManager.disableLighting();
					mc.fontRenderer.drawStringWithShadow(line1, sx + 20, sy + 4, 0xFFFFFFFF);
					mc.fontRenderer.drawStringWithShadow(line2, sx + 25, sy + 14, 0xFFFFFFFF);
					GlStateManager.color4f(1F, 1F, 1F, 1F);
				}
			}
		}
	}

}
