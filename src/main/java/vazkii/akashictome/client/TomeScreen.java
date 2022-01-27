package vazkii.akashictome.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.network.MessageMorphTome;
import vazkii.arl.util.ItemNBTHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;

public class TomeScreen extends Screen {

	private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation("akashictome:textures/models/book.png");
	private final BookModel BOOK_MODEL;

	final ItemStack tome;
	String definedMod;

	public TomeScreen(ItemStack tome) {
		super(new TextComponent(""));
		this.tome = tome;
		BOOK_MODEL = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
	}

	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		if(p_mouseClicked_5_ == 0 && this.definedMod != null) {
			AkashicTome.sendToServer(new MessageMorphTome(this.definedMod));
			this.minecraft.setScreen(null);
			return true;
		}

		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.definedMod = null;
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		List<ItemStack> stacks = new ArrayList<>();

		if(this.tome.hasTag()) {
			CompoundTag data = this.tome.getTag().getCompound(MorphingHandler.TAG_TOME_DATA);
			List<String> keys = Lists.newArrayList(data.getAllKeys());
			Collections.sort(keys);

			for(String s : keys) {
				CompoundTag cmp = data.getCompound(s);
				if(cmp != null) {
					ItemStack modStack = ItemStack.of(cmp);
					stacks.add(modStack);
				}
			}
		}

		Window window = this.minecraft.getWindow();
		int centerX = window.getGuiScaledWidth() / 2;
		int centerY = window.getGuiScaledHeight() / 2;

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
			for(int i = 0; i < stacks.size(); i++) {
				int x = startX + (i % amountPerRow) * iconSize;
				int y = startY + (i / amountPerRow) * iconSize;
				ItemStack stack = stacks.get(i);

				if(mouseX > x && mouseY > y && mouseX <= (x + 16) && mouseY <= (y + 16)) {
					tooltipStack = stack;
					y -= 2;
				}

				this.minecraft.getItemRenderer().renderAndDecorateItem(stack, x, y);
			}
		}
		
/*
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.matrixMode(GL11.GL_PROJECTION);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		int k = (int)this.minecraft.getWindow().getGuiScale();
		RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
		RenderSystem.translatef(0F, -0.9F, 0F);
		RenderSystem.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
*/

		//MatrixStack matrixstack = new MatrixStack();
		matrixStack.pushPose();
		PoseStack.Pose matrixstack$entry = matrixStack.last();
		matrixstack$entry.pose().setIdentity();
		matrixstack$entry.normal().setIdentity();
		matrixStack.translate(0.0D, 3.3F, 1984.0D);
		float scale = 20F;
		matrixStack.scale(scale, scale, scale);
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(50.0F));
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(4F * 90F - 90F));

		//RenderSystem.enableRescaleNormal();
		BOOK_MODEL.setupAnim(0.0F, 1F, 0F, 1F);
		MultiBufferSource.BufferSource irendertypebuffer$impl = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		VertexConsumer ivertexbuilder = irendertypebuffer$impl.getBuffer(BOOK_MODEL.renderType(BOOK_TEXTURE));
		BOOK_MODEL.renderToBuffer(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		irendertypebuffer$impl.endBatch();
		matrixStack.popPose();
/*
		RenderSystem.matrixMode(5889);
		RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
*/
		Lighting.setupFor3DItems();
		//RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		if(!tooltipStack.isEmpty()) {
			CompoundTag name = ItemNBTHelper.getCompound(tooltipStack, MorphingHandler.TAG_TOME_DISPLAY_NAME, false);
			String tempDefinedMod  = MorphingHandler.getModFromStack(tooltipStack);
			String mod = ChatFormatting.GRAY + MorphingHandler.getModNameForId(tempDefinedMod);
			tempDefinedMod = ItemNBTHelper.getString(tooltipStack, MorphingHandler.TAG_ITEM_DEFINED_MOD, tempDefinedMod);
			
			String trueName = name.getString("text");
			//vazkii.arl.util.RenderHelper.renderTooltip(mouseX, mouseY, Arrays.asList(new String[] { trueName, mod }));
			List<TextComponent> tooltipList = Arrays.stream(new String[] {trueName, mod}).map(TextComponent::new).collect(Collectors.toList());

			renderComponentTooltip(matrixStack, tooltipList, mouseX, mouseY, this.font);
			this.definedMod = tempDefinedMod;
		}
	}

}
