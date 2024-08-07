package vazkii.akashictome.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.NBTUtils;
import vazkii.akashictome.Registries;

public class HUDHandler {

	@SubscribeEvent
	public void onDrawScreen(RenderGuiOverlayEvent.Post event) {
		// if (event.getType() != ElementType.ALL)
		// 	return;

		Minecraft mc = Minecraft.getInstance();
		HitResult pos = mc.hitResult;
		Window res = event.getWindow();
		GuiGraphics guiGraphics = event.getGuiGraphics();

		if (pos != null && pos instanceof BlockHitResult) {
			BlockHitResult bpos = (BlockHitResult) pos;
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
				MutableComponent line1 = null;
				String line2 = "";

				String mod = MorphingHandler.getModFromState(state);
				ItemStack morphStack = MorphingHandler.getShiftStackForMod(tomeStack, mod);
        
				if (!morphStack.isEmpty() && !ItemStack.isSameItemSameTags(morphStack, tomeStack)) {
					drawStack = morphStack;
					line1 = NBTUtils.getCompound(morphStack, MorphingHandler.TAG_TOME_DISPLAY_NAME, false).getString("text");
					line2 = ChatFormatting.GRAY + I18n.get("akashictome.click_morph");
				}
				MutableComponent line1Component = Component.Serializer.fromJson(line1);

				if (!drawStack.isEmpty() && line1Component != null) {
					RenderSystem.enableBlend();
					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					int sx = res.getGuiScaledWidth() / 2 - 17;
					int sy = res.getGuiScaledHeight() / 2 + 2;
					guiGraphics.renderItem(drawStack, sx, sy);
					guiGraphics.drawString(mc.font, line1Component.withStyle(ChatFormatting.GREEN), sx + 20, sy + 4, 0xFFFFFFFF);
					guiGraphics.drawString(mc.font, line2, sx + 25, sy + 14, 0xFFFFFFFF);
				}
			}
		}
	}

}
