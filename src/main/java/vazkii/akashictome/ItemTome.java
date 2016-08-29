package vazkii.akashictome;

import java.util.List;

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
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import vazkii.arl.item.ItemMod;

public class ItemTome extends ItemMod {

	public ItemTome() {
		super("tome");
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.TOOLS);

		GameRegistry.addRecipe(new AttachementRecipe());
		RecipeSorter.register("akashictome:attachment", AttachementRecipe.class, Category.SHAPELESS, "");
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		AkashicTome.proxy.openTomeGUI(itemStackIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey(MorphingHandler.TAG_TOME_DATA))
			return;

		NBTTagCompound data = stack.getTagCompound().getCompoundTag(MorphingHandler.TAG_TOME_DATA);
		if(data.getKeySet().size() == 0)
			return;

		tooltipIfShift(tooltip, () -> {
			for(String s : data.getKeySet()) {
				NBTTagCompound cmp = data.getCompoundTag(s);
				if(cmp != null) {
					ItemStack modStack = ItemStack.loadItemStackFromNBT(cmp);
					if(modStack != null) {
						String name = modStack.getDisplayName();
						if(modStack.hasTagCompound() && modStack.getTagCompound().hasKey(MorphingHandler.TAG_TOME_DISPLAY_NAME))
							name = modStack.getTagCompound().getString(MorphingHandler.TAG_TOME_DISPLAY_NAME);

						tooltip.add(" " + MorphingHandler.getModNameForId(s) + " : " + name);
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
