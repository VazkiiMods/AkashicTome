package vazkii.akashictome;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AttachementRecipe extends CustomRecipe {

	public AttachementRecipe(CraftingBookCategory pCategory) {
		super(pCategory);
	}

	@Override
	public boolean matches(CraftingInput craftingInput, Level level) {
		boolean foundTool = false;
		boolean foundTarget = false;

		for (int i = 0; i < craftingInput.size(); i++) {
			ItemStack stack = craftingInput.getItem(i);
			if (!stack.isEmpty()) {
				if (isTarget(stack)) {
					if (foundTarget)
						return false;
					foundTarget = true;
				} else if (stack.is(Registries.TOME.get())) {
					if (foundTool)
						return false;
					foundTool = true;
				} else
					return false;
			}
		}

		return foundTool && foundTarget;
	}

	@Override
	public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
		ItemStack tool = ItemStack.EMPTY;
		ItemStack target = ItemStack.EMPTY;

		for (int i = 0; i < craftingInput.size(); i++) {
			ItemStack stack = craftingInput.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(Registries.TOME.get()))
					tool = stack;
				else
					target = stack;
			}
		}

		ItemStack copy = tool.copy();

		CompoundTag morphData = copy.getOrDefault(Registries.TOME_DATA, new CompoundTag());

		String mod = MorphingHandler.getModFromStack(target);
		String modRoot = mod;
		int tries = 0;

		while (morphData.contains(mod) && tries < 99) {
			mod = modRoot + "_" + tries;
			tries++;
		}

		if (tries > 0)
			target.set(Registries.DEFINED_MOD, mod);

		Tag stackTag = target.save(provider, new CompoundTag());
		morphData.put(mod, stackTag);
		copy.set(Registries.TOME_DATA, morphData);

		return copy;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	public boolean isTarget(ItemStack stack) {
		if (stack.isEmpty() || MorphingHandler.isAkashicTome(stack))
			return false;

		String mod = MorphingHandler.getModFromStack(stack);

		if (mod.equals(MorphingHandler.MINECRAFT))
			return false;

		if (ConfigHandler.allItems.get())
			return true;

		if (ConfigHandler.blacklistedMods.get().contains(mod))
			return false;

		if (stack.getItem() instanceof IModdedBook)
			return true;

		ResourceLocation registryNameRL = BuiltInRegistries.ITEM.getKey(stack.getItem());
		String registryName = registryNameRL.toString();
		if (ConfigHandler.whitelistedItems.get().contains(registryName) || ConfigHandler.whitelistedItems.get().contains(registryName + ":" + stack.getDamageValue()))
			return true;

		String itemName = registryNameRL.getPath().toLowerCase();
		for (String s : ConfigHandler.whitelistedNames.get())
			if (itemName.contains(s.toLowerCase()))
				return true;

		return false;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider provider) {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput craftingInput) {
		return NonNullList.withSize(craftingInput.size(), ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Registries.ATTACHMENT.get();
	}

}
