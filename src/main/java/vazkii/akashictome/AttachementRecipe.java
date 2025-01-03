package vazkii.akashictome;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import vazkii.akashictome.data_components.ToolContentComponent;

public class AttachementRecipe extends CustomRecipe {

	public AttachementRecipe(CraftingBookCategory pCategory) {
		super(pCategory);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		boolean foundTool = false;
		boolean foundTarget = false;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (isTarget(stack)) {
					if (foundTarget) {
						return false;
					}
					foundTarget = true;
				} else if (stack.is(Registries.TOME.get())) {
					if (foundTool) {
						return false;
					}
					foundTool = true;
				} else {
					return false;
				}
			}
		}

		return foundTool && foundTarget;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
		ItemStack tool = ItemStack.EMPTY;
		ItemStack target = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(Registries.TOME.get())) {
					tool = stack;
				} else {
					target = stack;
				}
			}
		}

		if (!tool.has(Registries.TOOL_CONTENT))
			return ItemStack.EMPTY;
		ItemStack copy = tool.copy();
		ToolContentComponent contents = copy.get(Registries.TOOL_CONTENT);
		if (contents == null) {
			return ItemStack.EMPTY;
		}

		String mod = MorphingHandler.getModFromStack(target);
		String modRoot = mod;
		int tries = 0;

		while (contents.hasDefinedMod(mod) && tries < 99) {
			mod = modRoot + "_" + tries;
			tries++;
		}

		target.set(Registries.DEFINED_MOD, mod);

		ToolContentComponent.Mutable mutable = new ToolContentComponent.Mutable(contents);
		if (!target.isEmpty()) {
			mutable.tryInsert(target);
		}

		copy.set(Registries.TOOL_CONTENT, mutable.toImmutable());

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
		if (ConfigHandler.whitelistedItems.get().contains(registryName) || ConfigHandler.whitelistedItems.get().contains(registryName + ":" + stack.getDamageValue())) {
			return true;
		}

		if (ConfigHandler.blacklistedItems.get().contains(registryName) || ConfigHandler.blacklistedItems.get().contains(registryName + ":" + stack.getDamageValue())) {
			return false;
		}

		String itemName = registryNameRL.getPath().toLowerCase();
		for (String s : ConfigHandler.whitelistedNames.get()) {
			if (itemName.contains(s.toLowerCase())) {
				return true;
			}
		}

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
