package vazkii.akashictome.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
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

		if(!tooltipStack.isEmpty()) {
			CompoundTag name = ItemNBTHelper.getCompound(tooltipStack, MorphingHandler.TAG_TOME_DISPLAY_NAME, false);
			String tempDefinedMod  = MorphingHandler.getModFromStack(tooltipStack);
			String mod = ChatFormatting.GRAY + MorphingHandler.getModNameForId(tempDefinedMod);
			tempDefinedMod = ItemNBTHelper.getString(tooltipStack, MorphingHandler.TAG_ITEM_DEFINED_MOD, tempDefinedMod);

			String trueName = name.getString("text");
			List<TextComponent> tooltipList = Arrays.stream(new String[] {trueName, mod}).map(TextComponent::new).collect(Collectors.toList());

			renderComponentTooltip(matrixStack, tooltipList, mouseX, mouseY, this.font);
			this.definedMod = tempDefinedMod;
		}

		// [VanillaCopy] EnchantmentScreen, but locked in open position, at different location, and bigger
		Lighting.setupForFlatItems();
		int guiScale = (int)this.minecraft.getWindow().getGuiScale();
		int viewportWidth = 320;
		int viewportHeight = 240;
		RenderSystem.viewport((this.width - viewportWidth) / 2 * guiScale, (this.height - viewportHeight) / 2 * guiScale, viewportWidth * guiScale, viewportHeight * guiScale);
		Matrix4f projMat = Matrix4f.createTranslateMatrix(-0.34F, 0.23F, 0.0F);
		projMat.multiply(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
		RenderSystem.backupProjectionMatrix();
		RenderSystem.setProjectionMatrix(projMat);
		matrixStack.pushPose();
		PoseStack.Pose pose = matrixStack.last();
		pose.pose().setIdentity();
		pose.normal().setIdentity();
		matrixStack.translate(6.3D, 3.3F, 1984.0D); // Akashic: Position at bottom of screen
		float scale = 15.0F; // Akashic: bigger
		matrixStack.scale(scale, scale, scale);
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(20.0F));
		float f1 = 1.0F; // Akashic: lock in open position Mth.lerp(p_98763_, this.oOpen, this.open);
		matrixStack.translate((1.0F - f1) * 0.2F, (1.0F - f1) * 0.1F, (1.0F - f1) * 0.25F);
		float f2 = -(1.0F - f1) * 90.0F - 90.0F;
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(f2));
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		float f3 = 0.0F /* Akashic: no flip Mth.lerp(p_98763_, this.oFlip, this.flip) */ + 0.25F;
		float f4 = 0.0F /* Akashic: no flip Mth.lerp(p_98763_, this.oFlip, this.flip) */ + 0.75F;
		f3 = (f3 - (float)Mth.fastFloor(f3)) * 1.6F - 0.3F;
		f4 = (f4 - (float)Mth.fastFloor(f4)) * 1.6F - 0.3F;
		if (f3 < 0.0F) {
			f3 = 0.0F;
		}

		if (f4 < 0.0F) {
			f4 = 0.0F;
		}

		if (f3 > 1.0F) {
			f3 = 1.0F;
		}

		if (f4 > 1.0F) {
			f4 = 1.0F;
		}

		BOOK_MODEL.setupAnim(0.0F, f3, f4, f1);
		MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		VertexConsumer buffer = buffers.getBuffer(BOOK_MODEL.renderType(BOOK_TEXTURE));
		BOOK_MODEL.renderToBuffer(matrixStack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		buffers.endBatch();
		matrixStack.popPose();
		RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
		RenderSystem.restoreProjectionMatrix();
		Lighting.setupFor3DItems();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
