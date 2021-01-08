package vazkii.akashictome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import vazkii.arl.item.BasicItem;
import vazkii.arl.util.TooltipHandler;

public class TomeItem extends BasicItem {

	public TomeItem() {
		super("tome", new Properties().maxStackSize(1).group(ItemGroup.TOOLS));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity playerIn = context.getPlayer();
		Hand hand = context.getHand();
		World worldIn = context.getWorld();
		BlockPos pos = context.getPos();
		ItemStack stack = playerIn.getHeldItem(hand);
		
		if(playerIn.isDiscrete()) {
			String mod = MorphingHandler.getModFromState(worldIn.getBlockState(pos)); 
			ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, mod);
			if(!ItemStack.areItemsEqual(newStack, stack)) {
				playerIn.setHeldItem(hand, newStack);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		AkashicTome.proxy.openTomeGUI(playerIn, stack);
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		if(!stack.hasTag() || !stack.getTag().contains(MorphingHandler.TAG_TOME_DATA))
			return;

		CompoundNBT data = stack.getTag().getCompound(MorphingHandler.TAG_TOME_DATA);
		if(data.keySet().size() == 0)
			return;

		List<String> tooltipList = new ArrayList<>();

		TooltipHandler.tooltipIfShift(tooltipList, () -> {
			List<String> keys = Lists.newArrayList(data.keySet());
			Collections.sort(keys);
			String currMod = "";

			for(String s : keys) {
				CompoundNBT cmp = data.getCompound(s);
				if(cmp != null) {
					ItemStack modStack = ItemStack.read(cmp);
					if(!modStack.isEmpty()) {
						String name = modStack.getDisplayName().getString();
						if(modStack.hasTag() && modStack.getTag().contains(MorphingHandler.TAG_TOME_DISPLAY_NAME))
							name = modStack.getTag().getString(MorphingHandler.TAG_TOME_DISPLAY_NAME);
						String mod = MorphingHandler.getModFromStack(modStack);

						if(!currMod.equals(mod)) 
							tooltip.add(new StringTextComponent(MorphingHandler.getModNameForId(mod)).setStyle(Style.EMPTY.createStyleFromFormattings(TextFormatting.AQUA)));
						tooltip.add(new StringTextComponent(" \u2520 " + name));

						currMod = mod;
					}
				}
			}
		}
		);
		
		tooltipList.forEach(tip -> tooltip.add(new StringTextComponent(tip)));
	}

}
