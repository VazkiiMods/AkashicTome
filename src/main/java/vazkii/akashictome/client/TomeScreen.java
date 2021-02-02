package vazkii.akashictome.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.network.MessageMorphTome;
import vazkii.arl.util.DropInHandler;
import vazkii.arl.util.ItemNBTHelper;

public class TomeScreen extends Screen {

	private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation("akashictome:textures/models/book.png");
	private static final BookModel BOOK_MODEL = new BookModel();

	final ItemStack tome;
	String definedMod;

	public TomeScreen(ItemStack tome) {
		super(new StringTextComponent(""));
		this.tome = tome;
	}

	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		if(p_mouseClicked_5_ == 0 && this.definedMod != null) {
			AkashicTome.sendToServer(new MessageMorphTome(this.definedMod));
			this.minecraft.displayGuiScreen(null);
			return true;
		}

		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.definedMod = null;
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		List<ItemStack> stacks = new ArrayList<>();

		if(this.tome.hasTag()) {
			CompoundNBT data = this.tome.getTag().getCompound(MorphingHandler.TAG_TOME_DATA);
			List<String> keys = Lists.newArrayList(data.keySet());
			Collections.sort(keys);

			for(String s : keys) {
				CompoundNBT cmp = data.getCompound(s);
				if(cmp != null) {
					ItemStack modStack = ItemStack.read(cmp);
					stacks.add(modStack);
				}
			}
		}

		MainWindow window = this.minecraft.getMainWindow();
		int centerX = window.getScaledWidth() / 2;
		int centerY = window.getScaledHeight() / 2;

		int amountPerRow = 6;
		int rows = stacks.size() / amountPerRow + 1;
		int iconSize = 20;

		int startX = centerX - (amountPerRow * iconSize) / 2;
		int startY = centerY - (rows * iconSize) + 45;

		int padding = 4;
		int extra = 2;
		fill(matrixStack, startX - padding, startY - padding, startX + iconSize * amountPerRow + padding, startY + iconSize * rows + padding, 0x22000000);
		fill(matrixStack, startX - padding - extra, startY - padding - extra, startX + iconSize * amountPerRow + padding + extra, startY + iconSize * rows + padding + extra, 0x22000000);

		ItemStack tooltipStack = ItemStack.EMPTY;

		if(!stacks.isEmpty()) {
			RenderHelper.enableStandardItemLighting();
			for(int i = 0; i < stacks.size(); i++) {
				int x = startX + (i % amountPerRow) * iconSize;
				int y = startY + (i / amountPerRow) * iconSize;
				ItemStack stack = stacks.get(i);

				if(mouseX > x && mouseY > y && mouseX <= (x + 16) && mouseY <= (y + 16)) {
					tooltipStack = stack;
					y -= 2;
				}

				this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
			}
			RenderHelper.disableStandardItemLighting();
		}
		
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.matrixMode(GL11.GL_PROJECTION);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		int k = (int)this.minecraft.getMainWindow().getGuiScaleFactor();
		RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
		RenderSystem.translatef(0F, -0.9F, 0F);
		RenderSystem.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);

		//MatrixStack matrixstack = new MatrixStack();
		matrixStack.push();
		MatrixStack.Entry matrixstack$entry = matrixStack.getLast();
		matrixstack$entry.getMatrix().setIdentity();
		matrixstack$entry.getNormal().setIdentity();
		matrixStack.translate(0.0D, 3.3F, 1984.0D);
		float scale = 20F;
		matrixStack.scale(scale, scale, scale);
		matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
		matrixStack.rotate(Vector3f.XP.rotationDegrees(50.0F));
		matrixStack.rotate(Vector3f.YP.rotationDegrees(4F * 90F - 90F));

		RenderSystem.enableRescaleNormal();
		BOOK_MODEL.setBookState(0.0F, 1F, 0F, 1F);
		IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		IVertexBuilder ivertexbuilder = irendertypebuffer$impl.getBuffer(BOOK_MODEL.getRenderType(BOOK_TEXTURE));
		BOOK_MODEL.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		irendertypebuffer$impl.finish();
		matrixStack.pop();
		RenderSystem.matrixMode(5889);
		RenderSystem.viewport(0, 0, this.minecraft.getMainWindow().getFramebufferWidth(), this.minecraft.getMainWindow().getFramebufferHeight());
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
		RenderHelper.setupGui3DDiffuseLighting();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		if(!tooltipStack.isEmpty()) {
			CompoundNBT name = ItemNBTHelper.getCompound(tooltipStack, MorphingHandler.TAG_TOME_DISPLAY_NAME, false);
			String tempDefinedMod  = MorphingHandler.getModFromStack(tooltipStack);
			String mod = TextFormatting.GRAY + MorphingHandler.getModNameForId(tempDefinedMod);
			tempDefinedMod = ItemNBTHelper.getString(tooltipStack, MorphingHandler.TAG_ITEM_DEFINED_MOD, tempDefinedMod);
			
			String trueName = name.getString("text");
			//vazkii.arl.util.RenderHelper.renderTooltip(mouseX, mouseY, Arrays.asList(new String[] { trueName, mod }));
			List<StringTextComponent> tooltipList = Arrays.stream(new String[] {trueName, mod}).map(StringTextComponent::new).collect(Collectors.toList());

			GuiUtils.drawHoveringText(matrixStack, tooltipList, mouseX, mouseY, this.width, this.height, -1, this.font);
			this.definedMod = tempDefinedMod;
		}
	}

}
