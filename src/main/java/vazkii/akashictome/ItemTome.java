package vazkii.akashictome;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import vazkii.akashictome.client.GuiTome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemTome extends Item {

	public ItemTome() {
		super(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {

		PlayerEntity playerIn = context.getPlayer();
		if (playerIn == null)
			return ActionResultType.PASS;

		World worldIn = context.getWorld();
		BlockPos pos = context.getPos();
		Hand hand = context.getHand();
		ItemStack stack = playerIn.getHeldItem(hand);

		if (playerIn.isSneaking()) {
			String mod = MorphingHandler.getModFromState(worldIn.getBlockState(pos));
			ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, mod);
			if (!ItemStack.areItemsEqual(newStack, stack)) {
				playerIn.setHeldItem(hand, newStack);
				return ActionResultType.SUCCESS;
			}

//			if (worldIn.isRemote) {
//				RayTraceResult result = new BlockRayTraceResult(new Vec3d(context.getHitVec().x, context.getHitVec().y, context.getHitVec().z), context.getFace(), pos, false);
//				return AkashicTome.proxy.openWikiPage(worldIn, worldIn.getBlockState(pos).getBlock(), result) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
//			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (worldIn.isRemote) {
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
				if (playerIn == Minecraft.getInstance().player)
					Minecraft.getInstance().displayGuiScreen(new GuiTome(stack));
			});
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World playerIn, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		CompoundNBT tomeData = stack.getChildTag(MorphingHandler.TAG_TOME_DATA);
		if (tomeData == null || tomeData.keySet().size() == 0) {
			tooltip.add(new TranslationTextComponent("akashictome.empty").applyTextStyle(TextFormatting.GRAY));
			return;
		}

		if (Screen.hasShiftDown()) {
			List<String> keys = new ArrayList<>(tomeData.keySet());
			Collections.sort(keys);
			String currMod = "";

			for (String s : keys) {
				CompoundNBT cmp = tomeData.getCompound(s);
				ItemStack modStack = ItemStack.read(cmp);
				if (!modStack.isEmpty()) {
					ITextComponent name = MorphingHandler.getMorphedDisplayName(modStack);
					String mod = MorphingHandler.getModFromStack(modStack);

					if (!currMod.equals(mod))
						tooltip.add(new StringTextComponent(MorphingHandler.getModNameForId(mod)).applyTextStyle(TextFormatting.AQUA));
					tooltip.add(new StringTextComponent(" \u2520 ").appendSibling(name));

					currMod = mod;
				}
			}
		} else {
			tooltip.add(new TranslationTextComponent("akashictome.moreInfo", new StringTextComponent("SHIFT")
					.applyTextStyle(TextFormatting.GREEN)).applyTextStyle(TextFormatting.GRAY));
		}
	}
}

