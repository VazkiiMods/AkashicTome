package vazkii.akashictome;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TomeItem extends Item {

	public TomeItem() {
		super(new Properties().stacksTo(1));
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
			if (!ItemStack.isSameItem(newStack, stack)) { //TODO test if sameTags as well
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
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
		if (!stack.hasTag() || !stack.getTag().contains(MorphingHandler.TAG_TOME_DATA))
			return;

		CompoundTag data = stack.getTag().getCompound(MorphingHandler.TAG_TOME_DATA);
		if (data.getAllKeys().isEmpty())
			return;

		if (Screen.hasShiftDown()) {
			List<String> keys = Lists.newArrayList(data.getAllKeys());
			Collections.sort(keys);
			String currMod = "";

			for (String s : keys) {
				CompoundTag cmp = data.getCompound(s);
				if (cmp != null) {
					ItemStack modStack = ItemStack.of(cmp);
					if (!modStack.isEmpty()) {
						String name = modStack.getHoverName().getString();
						if (modStack.hasTag() && modStack.getTag().contains(MorphingHandler.TAG_TOME_DISPLAY_NAME)) {
							CompoundTag rawName = ((CompoundTag) modStack.getTag().get(MorphingHandler.TAG_TOME_DISPLAY_NAME));
							Component nameComp = Component.Serializer.fromJson(rawName.getString("text"));
							if (nameComp != null)
								name = nameComp.getString();
						}
						String mod = MorphingHandler.getModFromStack(modStack);

						if (!currMod.equals(mod))
							tooltip.add(Component.literal(MorphingHandler.getModNameForId(mod)).setStyle(Style.EMPTY.applyFormats(ChatFormatting.AQUA)));
						tooltip.add(Component.literal(" ┠ " + name));

						currMod = mod;
					}
				}
			}
		} else {
			tooltip.add(Component.translatable(AkashicTome.MOD_ID + ".misc.shift_for_info"));
		}
	}

}
