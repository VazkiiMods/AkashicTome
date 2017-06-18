package vazkii.akashictome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import vazkii.arl.item.ItemMod;

public class ItemTome extends ItemMod {

	public ItemTome() {
		super("tome");
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.TOOLS);

		new AttachementRecipe();
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(playerIn.isSneaking()) {
			String mod = MorphingHandler.getModFromState(worldIn.getBlockState(pos)); 
			ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, mod);
			if(!ItemStack.areItemsEqual(newStack, stack)) {
				playerIn.setHeldItem(hand, newStack);
				return EnumActionResult.SUCCESS;
			}

			if(worldIn.isRemote) {
				RayTraceResult result = new RayTraceResult(new Vec3d(hitX, hitY, hitZ), facing, pos);
				return AkashicTome.proxy.openWikiPage(worldIn, worldIn.getBlockState(pos).getBlock(), result) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
			}
		}

		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		AkashicTome.proxy.openTomeGUI(playerIn, stack);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}
	
	@Override
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey(MorphingHandler.TAG_TOME_DATA))
			return;

		NBTTagCompound data = stack.getTagCompound().getCompoundTag(MorphingHandler.TAG_TOME_DATA);
		if(data.getKeySet().size() == 0)
			return;

		tooltipIfShift(tooltip, () -> {
			List<String> keys = new ArrayList(data.getKeySet());
			Collections.sort(keys);
			String currMod = "";
			
			for(String s : keys) {
				NBTTagCompound cmp = data.getCompoundTag(s);
				if(cmp != null) {
					ItemStack modStack = new ItemStack(cmp);
					if(!modStack.isEmpty()) {
						String name = modStack.getDisplayName();
						if(modStack.hasTagCompound() && modStack.getTagCompound().hasKey(MorphingHandler.TAG_TOME_DISPLAY_NAME))
							name = modStack.getTagCompound().getString(MorphingHandler.TAG_TOME_DISPLAY_NAME);
						String mod = MorphingHandler.getModFromStack(modStack);
						
						if(!currMod.equals(mod)) 
							tooltip.add(TextFormatting.AQUA + MorphingHandler.getModNameForId(mod));
						tooltip.add(" \u2520 " + name);
						
						currMod = mod;
					}
				}
			}
		}
				);
	}

	@Override
	public String getModNamespace() {
		return "akashictome";
	}

}
