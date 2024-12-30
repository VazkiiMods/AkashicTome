package vazkii.akashictome.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.ConfigHandler;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registries;
import vazkii.akashictome.data_components.ToolContentComponent;
import vazkii.akashictome.network.MessageMorphTome;
import vazkii.akashictome.network.NetworkHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TomeScreen extends Screen {

	private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(AkashicTome.MOD_ID, "textures/models/book.png");
	private final BookModel BOOK_MODEL;

	final ItemStack tome;
	String definedMod;

	public TomeScreen(ItemStack tome) {
		super(Component.empty());
		this.tome = tome.copy();
		BOOK_MODEL = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && this.definedMod != null) {
			NetworkHandler.sendToServer(new MessageMorphTome(this.definedMod));
			this.minecraft.setScreen(null);
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTicks) {
		PoseStack poseStack = pGuiGraphics.pose();
		this.definedMod = null;
		super.render(pGuiGraphics, mouseX, mouseY, partialTicks);

		if (!tome.has(Registries.TOOL_CONTENT)) {
			return;
		}

		List<ItemStack> stacks = Lists.newArrayList();

		ToolContentComponent contents = tome.get(Registries.TOOL_CONTENT);
		if (contents != null && !contents.isEmpty()) {
			stacks = new ArrayList<>(contents.getItems());
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

		int endX = startX + iconSize * amountPerRow;
		int endY = startY + iconSize * rows;
		pGuiGraphics.fill(startX - padding, startY - padding, endX + padding, endY + padding, 0x22000000);
		pGuiGraphics.fill(startX - padding - extra, startY - padding - extra, endX + padding + extra, endY + padding + extra, 0x22000000);

		ItemStack tooltipStack = ItemStack.EMPTY;

		if (!stacks.isEmpty()) {
			for (int i = 0; i < stacks.size(); i++) {
				int x = startX + (i % amountPerRow) * iconSize;
				int y = startY + (i / amountPerRow) * iconSize;
				ItemStack stack = stacks.get(i);

				if (mouseX > x && mouseY > y && mouseX <= (x + 16) && mouseY <= (y + 16)) {
					tooltipStack = stack;
					y -= 2;
				}

				pGuiGraphics.renderItem(stack, x, y);
			}
		}

		if (!tooltipStack.isEmpty()) {
			Component ogDisplayName;
			if (tooltipStack.has(Registries.OG_DISPLAY_NAME)) {
				ogDisplayName = tooltipStack.get(Registries.OG_DISPLAY_NAME);
			} else {
				ogDisplayName = tooltipStack.getHoverName();
			}

			String tempDefinedMod = MorphingHandler.getModFromStack(tooltipStack);
			String mod = ChatFormatting.GRAY + MorphingHandler.getModNameForId(tempDefinedMod);

			if (tooltipStack.has(Registries.DEFINED_MOD)) {
				tempDefinedMod = tooltipStack.get(Registries.DEFINED_MOD);
			}

			List<Component> tooltipList = Arrays.asList(ogDisplayName, Component.literal(mod));

			pGuiGraphics.renderComponentTooltip(this.font, tooltipList, mouseX, mouseY);
			this.definedMod = tempDefinedMod;
		}

		if (!ConfigHandler.hideBookRender.get()) {
			float f = 1.0F;
			float f1 = 0.0F;
			Lighting.setupForEntityInInventory();
			poseStack.pushPose();
			poseStack.translate((startX + endX) / 2.0, startY - 45, 100.0F);
			float f2 = 100.0F;
			poseStack.scale(-f2, f2, f2);
			poseStack.mulPose(Axis.XP.rotationDegrees(30.0F));
			poseStack.translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
			float f3 = -(1.0F - f) * 90.0F - 91.0F;
			poseStack.mulPose(Axis.YP.rotationDegrees(f3));
			poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
			float f4 = Mth.clamp(Mth.frac(f1 + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
			float f5 = Mth.clamp(Mth.frac(f1 + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
			this.BOOK_MODEL.setupAnim(0.0F, f4, f5, f);
			VertexConsumer vertexconsumer = pGuiGraphics.bufferSource().getBuffer(this.BOOK_MODEL.renderType(BOOK_TEXTURE));
			this.BOOK_MODEL.renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, -1);
			pGuiGraphics.flush();
			poseStack.popPose();
			Lighting.setupFor3DItems();

		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
