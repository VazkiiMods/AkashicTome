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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import vazkii.akashictome.AkashicTome;
import vazkii.akashictome.ConfigHandler;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registries;
import vazkii.akashictome.network.MessageMorphTome;
import vazkii.akashictome.network.NetworkHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TomeScreen extends Screen {

	private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(AkashicTome.MOD_ID, "textures/models/book.png");
	private final BookModel BOOK_MODEL;

	final ItemStack tome;
	String definedMod;

	public TomeScreen(ItemStack tome) {
		super(Component.empty());
		this.tome = tome;
		BOOK_MODEL = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && this.definedMod != null) {
			System.out.println("Defined mod: " + this.definedMod);
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
		PoseStack matrixStack = pGuiGraphics.pose();
		this.definedMod = null;
		super.render(pGuiGraphics, mouseX, mouseY, partialTicks);

		List<ItemStack> stacks = new ArrayList<>();

		if (this.tome.has(Registries.TOME_DATA)) {
			CompoundTag data = this.tome.getOrDefault(Registries.TOME_DATA, new CompoundTag());
			List<String> keys = Lists.newArrayList(data.getAllKeys());
			Collections.sort(keys);

			for (String s : keys) {
				CompoundTag cmp = data.getCompound(s);
				if (cmp != null) {
					ItemStack modStack = ItemStack.parseOptional(minecraft.level.registryAccess(), cmp);
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
			String tempDefinedMod = MorphingHandler.getModFromStack(tooltipStack);
			String mod = ChatFormatting.GRAY + MorphingHandler.getModNameForId(tempDefinedMod);
			tempDefinedMod = tooltipStack.getOrDefault(Registries.DEFINED_MOD, tempDefinedMod);

			Component comp = tooltipStack.get(Registries.DISPLAY_NAME);
			if (comp == null)
				comp = tooltipStack.getHoverName();

			List<Component> tooltipList = Arrays.asList(comp, Component.literal(mod));

			pGuiGraphics.renderComponentTooltip(this.font, tooltipList, mouseX, mouseY);
			this.definedMod = tempDefinedMod;
		}

		if (!ConfigHandler.hideBookRender.get()) {
			float f = 1.0F;
			float f1 = 0.0F;
			Lighting.setupForEntityInInventory();
			matrixStack.pushPose();
			matrixStack.translate((startX + endX) / 2.0, startY - 45, 100.0F);
			float f2 = 100.0F;
			matrixStack.scale(-f2, f2, f2);
			matrixStack.mulPose(Axis.XP.rotationDegrees(30.0F));
			matrixStack.translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
			float f3 = -(1.0F - f) * 90.0F - 91.0F;
			matrixStack.mulPose(Axis.YP.rotationDegrees(f3));
			matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
			float f4 = Mth.clamp(Mth.frac(f1 + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
			float f5 = Mth.clamp(Mth.frac(f1 + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
			this.BOOK_MODEL.setupAnim(0.0F, f4, f5, f);
			VertexConsumer vertexconsumer = pGuiGraphics.bufferSource().getBuffer(this.BOOK_MODEL.renderType(BOOK_TEXTURE));
			this.BOOK_MODEL.renderToBuffer(matrixStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, -1);
			pGuiGraphics.flush();
			matrixStack.popPose();
			Lighting.setupFor3DItems();

		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
