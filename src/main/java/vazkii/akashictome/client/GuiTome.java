package vazkii.akashictome.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.network.PacketHandler;
import vazkii.akashictome.network.message.MessageMorphTome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GuiTome extends Screen {

	private static final ResourceLocation texture = new ResourceLocation("akashictome:textures/models/book.png");
	private static final BookModel modelBook = new BookModel();

	private final ItemStack tome;

	private ItemStack tooltipStack = ItemStack.EMPTY;
	private int currentX, currentY;

	public GuiTome(ItemStack tome) {
		super(tome.getDisplayName());
		this.tome = tome;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (mouseButton == 0 && !tooltipStack.isEmpty()) {
			if (mouseX > currentX && mouseY > currentY && mouseX <= (currentX + 16) && mouseY <= (currentY + 16)) {
				String definedMod = MorphingHandler.getDefinedModFromStack(tooltipStack);
				PacketHandler.sendToServer(new MessageMorphTome(definedMod));
				minecraft.displayGuiScreen(null);
				return true;
			}
		}
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);

		List<ItemStack> stacks = new ArrayList<>();

		CompoundNBT tomeData = tome.getChildTag(MorphingHandler.TAG_TOME_DATA);
		if (tomeData != null) {
			List<String> keys = new ArrayList<>(tomeData.keySet());
			Collections.sort(keys);

			for (String s : keys) {
				CompoundNBT cmp = tomeData.getCompound(s);
				ItemStack modStack = ItemStack.read(cmp);
				stacks.add(modStack);
			}
		}

		MainWindow window = minecraft.mainWindow;
		int scaledWidth = window.getScaledWidth();
		int scaledHeight = window.getScaledHeight();
		int guiScaleFactor = (int) window.getGuiScaleFactor();

		int centerX = scaledWidth / 2;
		int centerY = scaledHeight / 2;

		int amountPerRow = 6;
		int rows = stacks.size() / amountPerRow + 1;
		int iconSize = 20;

		int startX = centerX - (amountPerRow * iconSize) / 2;
		int startY = centerY - (rows * iconSize) + 45;

		int padding = 4;
		int extra = 2;
		fill(startX - padding, startY - padding, startX + iconSize * amountPerRow + padding, startY + iconSize * rows + padding, 0x22000000);
		fill(startX - padding - extra, startY - padding - extra, startX + iconSize * amountPerRow + padding + extra, startY + iconSize * rows + padding + extra, 0x22000000);

		tooltipStack = ItemStack.EMPTY;

		if (!stacks.isEmpty()) {
			RenderHelper.enableGUIStandardItemLighting();
			for (int i = 0; i < stacks.size(); i++) {
				int x = startX + (i % amountPerRow) * iconSize;
				int y = startY + (i / amountPerRow) * iconSize;
				ItemStack stack = stacks.get(i);

				if (mouseX > x && mouseY > y && mouseX <= (x + 16) && mouseY <= (y + 16)) {
					tooltipStack = stack;
					currentX = x;
					currentY = y;
					y -= 2;
				}

				itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
			}
			RenderHelper.disableStandardItemLighting();
		}

		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.viewport((scaledWidth - 320) / 2 * guiScaleFactor, (scaledHeight - 240) / 2 * guiScaleFactor, 320 * guiScaleFactor, 240 * guiScaleFactor);
		GlStateManager.translatef(0F, -0.15F, 0F);
		GlStateManager.multMatrix(Matrix4f.perspective(90F, 1.3333334F, 9F, 80F));
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.translatef(-0.1F, -9F, -16F);
		GlStateManager.scalef(1F, 1F, 1F);
		GlStateManager.rotatef(-100F, 1, 0F, 0F);
		GlStateManager.rotatef(4F * 90F - 90F, 0F, 1F, 0F);
		GlStateManager.rotatef(180F, 1F, 0F, 0F);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		minecraft.getTextureManager().bindTexture(texture);
		GlStateManager.enableRescaleNormal();
		modelBook.render(0F, 0F, 0F, 1F, 0F, 1.2F);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.viewport(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.color4f(1F, 1F, 1F, 1F);

		if (!tooltipStack.isEmpty()) {
			ITextComponent formattedName = MorphingHandler.getMorphedDisplayName(tooltipStack);

			String definedMod = MorphingHandler.getModFromStack(tooltipStack);
			String mod = TextFormatting.GRAY + MorphingHandler.getModNameForId(definedMod);
			renderTooltip(Arrays.asList(formattedName.getFormattedText(), mod), mouseX, mouseY);

		}
	}

}
