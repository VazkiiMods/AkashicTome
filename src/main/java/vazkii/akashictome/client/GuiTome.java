package vazkii.akashictome.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.network.message.MessageMorphTome;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.util.ItemNBTHelper;

public class GuiTome extends GuiScreen {

	ResourceLocation texture = new ResourceLocation("akashictome:textures/models/book.png");
	ModelBook modelBook = new ModelBook();
	
	ItemStack tome;
	
	public GuiTome(ItemStack tome) {
		this.tome = tome;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		List<ItemStack> stacks = new ArrayList();

		if(tome.hasTagCompound()) {
			NBTTagCompound data = tome.getTagCompound().getCompoundTag(MorphingHandler.TAG_TOME_DATA);
			List<String> keys = new ArrayList(data.getKeySet());
			Collections.sort(keys);
			
			for(String s : keys) {
				NBTTagCompound cmp = data.getCompoundTag(s);
				if(cmp != null) {
					ItemStack modStack = new ItemStack(cmp);
					stacks.add(modStack);
				}
			}
		}

		ScaledResolution res = new ScaledResolution(mc);
		int centerX = res.getScaledWidth() / 2;
		int centerY = res.getScaledHeight() / 2;
		
		int amountPerRow = 6;
		int rows = stacks.size() / amountPerRow + 1;
		int iconSize = 20;
		
		int startX = centerX - (amountPerRow * iconSize) / 2;
		int startY = centerY - (rows * iconSize) + 45;
		
		int padding = 4;
		int extra = 2;
		drawRect(startX - padding, startY - padding, startX + iconSize * amountPerRow + padding, startY + iconSize * rows + padding, 0x22000000);
		drawRect(startX - padding - extra, startY - padding - extra, startX + iconSize * amountPerRow + padding + extra, startY + iconSize * rows + padding + extra, 0x22000000);

		ItemStack tooltipStack = ItemStack.EMPTY;
		
		if(!stacks.isEmpty()) {
			RenderHelper.enableGUIStandardItemLighting();
			for(int i = 0; i < stacks.size(); i++) {
				int x = startX + (i % amountPerRow) * iconSize;
				int y = startY + (i / amountPerRow) * iconSize;
				ItemStack stack = stacks.get(i);
				
				if(mouseX > x && mouseY > y && mouseX <= (x + 16) && mouseY <= (y + 16)) {
					tooltipStack = stack;
					y -= 2;
				}
				
				itemRender.renderItemAndEffectIntoGUI(stack, x, y);
			}
			RenderHelper.disableStandardItemLighting();
		}
		
		GL11.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glViewport((res.getScaledWidth() - 320) / 2 * res.getScaleFactor(), (res.getScaledHeight() - 240) / 2 * res.getScaleFactor(), 320 * res.getScaleFactor(), 240 * res.getScaleFactor());
		GL11.glTranslatef(0F, -0.15F, 0F);
		GLU.gluPerspective(90F, 1.3333334F, 9F, 80F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		RenderHelper.enableStandardItemLighting();
		GL11.glTranslatef(-0.1F, -9F, -16F);
		GL11.glScalef(1F, 1F, 1F);
		GL11.glRotatef(-100F, 1, 0F, 0F);
		GL11.glRotatef(4F * 90F - 90F, 0F, 1F, 0F);
		GL11.glRotatef(180F, 1F, 0F, 0F);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(texture);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		modelBook.render(null, 0F, 0F, 0F, 1F, 0F, 1.2F);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		if(!tooltipStack.isEmpty()) {
			String name = ItemNBTHelper.getString(tooltipStack, MorphingHandler.TAG_TOME_DISPLAY_NAME, tooltipStack.getDisplayName());
			String definedMod = MorphingHandler.getModFromStack(tooltipStack);
			String mod = TextFormatting.GRAY + MorphingHandler.getModNameForId(definedMod);
			definedMod = ItemNBTHelper.getString(tooltipStack, MorphingHandler.TAG_ITEM_DEFINED_MOD, definedMod);
			vazkii.arl.util.RenderHelper.renderTooltip(mouseX, mouseY, Arrays.asList(new String[] { name, mod }));
			
			if(Mouse.isButtonDown(0)) {
				NetworkHandler.INSTANCE.sendToServer(new MessageMorphTome(definedMod));
				mc.displayGuiScreen(null);
			}
		}
	}

}
