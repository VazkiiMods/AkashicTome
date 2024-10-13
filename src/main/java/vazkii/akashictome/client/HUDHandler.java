package vazkii.akashictome.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import org.lwjgl.opengl.GL11;

import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registries;

public class HUDHandler {

	@SubscribeEvent
	public void onDrawScreen(RenderGuiLayerEvent.Post event) {
		// if (event.getType() != ElementType.ALL)
		// 	return;

		Minecraft mc = Minecraft.getInstance();
		HitResult pos = mc.hitResult;
		Window res = mc.getWindow();
		GuiGraphics guiGraphics = event.getGuiGraphics();

		if (pos instanceof BlockHitResult bpos) {
			ItemStack tomeStack = mc.player.getMainHandItem();

			boolean hasTome = !tomeStack.isEmpty() && tomeStack.is(Registries.TOME.get());
			if (!hasTome) {
				tomeStack = mc.player.getOffhandItem();
				hasTome = !tomeStack.isEmpty() && tomeStack.is(Registries.TOME.get());
			}

			if (!hasTome)
				return;

			tomeStack = tomeStack.copy();

			BlockState state = mc.level.getBlockState(bpos.getBlockPos());

			if (!state.isAir()) {
				ItemStack drawStack = ItemStack.EMPTY;
				Component line1 = Component.empty();
				Component line2 = Component.empty();

				String mod = MorphingHandler.getModFromState(state);
				ItemStack morphStack = MorphingHandler.getShiftStackForMod(tomeStack, mod, mc.level.registryAccess());

				if (!morphStack.isEmpty() && !ItemStack.isSameItemSameComponents(morphStack, tomeStack)) {
					drawStack = morphStack;
					line1 = morphStack.getOrDefault(Registries.DISPLAY_NAME, Component.empty());
					line2 = Component.translatable("akashictome.click_morph").withStyle(ChatFormatting.GRAY);
				}

				if (!drawStack.isEmpty() && !line1.getString().isEmpty()) {
					RenderSystem.enableBlend();
					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					int sx = res.getGuiScaledWidth() / 2 - 17;
					int sy = res.getGuiScaledHeight() / 2 + 2;
					guiGraphics.renderItem(drawStack, sx, sy);
					guiGraphics.drawString(mc.font, line1.copy().withStyle(ChatFormatting.GREEN), sx + 20, sy + 4, 0xFFFFFFFF);
					guiGraphics.drawString(mc.font, line2, sx + 25, sy + 14, 0xFFFFFFFF);
				}
			}
		}
	}

}
