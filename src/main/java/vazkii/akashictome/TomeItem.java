package vazkii.akashictome;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import vazkii.akashictome.data_components.ToolContentComponent;

import java.util.List;

public class TomeItem extends Item {

	public TomeItem(Properties properties) {
		super(properties.stacksTo(1).component(Registries.IS_MORPHED, false).component(Registries.TOOL_CONTENT, ToolContentComponent.EMPTY));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player playerIn = context.getPlayer();
		InteractionHand hand = context.getHand();
		Level worldIn = context.getLevel();
		BlockPos pos = context.getClickedPos();
		ItemStack stack = playerIn.getItemInHand(hand);

		if (playerIn.isShiftKeyDown()) {
			String mod = MorphingHandler.getModFromState(worldIn.getBlockState(pos));
			ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, mod);
			if (!ItemStack.isSameItem(newStack, stack)) {
				playerIn.setItemInHand(hand, newStack);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		AkashicTome.proxy.openTomeGUI(playerIn, stack);
		return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag advanced) {
		if (!stack.has(Registries.TOOL_CONTENT))
			return;

		ToolContentComponent contents = stack.get(Registries.TOOL_CONTENT);
		if (contents == null || contents.isEmpty())
			return;

		if (Screen.hasShiftDown()) {
			String currMod = "";
			for (ItemStack contentStack : contents.getItems()) {
				if (!contentStack.isEmpty()) {
					Component name;
					if (contentStack.has(Registries.OG_DISPLAY_NAME)) {
						name = contentStack.get(Registries.OG_DISPLAY_NAME);
					} else {
						name = contentStack.getHoverName();
					}

					String mod = MorphingHandler.getModFromStack(contentStack);

					if (!currMod.equals(mod)) {
						tooltip.add(Component.literal(MorphingHandler.getModNameForId(mod)).setStyle(Style.EMPTY.applyFormats(ChatFormatting.AQUA)));
					}
					tooltip.add(Component.literal(" ┠ " + name.getString()));
					currMod = mod;
				}
			}
		} else {
			tooltip.add(Component.translatable(AkashicTome.MOD_ID + ".misc.shift_for_info"));
		}
	}

}
