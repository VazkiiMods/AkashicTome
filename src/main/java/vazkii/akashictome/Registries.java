package vazkii.akashictome;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class Registries {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AkashicTome.MOD_ID);
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AkashicTome.MOD_ID);

	public static final RegistryObject<Item> TOME = ITEMS.register("tome", TomeItem::new);

	public static final RegistryObject<RecipeSerializer<AttachementRecipe>> ATTACHMENT = SERIALIZERS.register("attachment", () -> new SimpleCraftingRecipeSerializer<>(AttachementRecipe::new));

}
