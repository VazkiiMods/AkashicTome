package vazkii.akashictome;

import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegisterEvent;
import vazkii.arl.util.RegistryHelper;

public final class ModItems {

	public static final RecipeSerializer<AttachementRecipe> ATTACHMENT = new SimpleRecipeSerializer<>(AttachementRecipe::new);
	
	public static Item tome;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRegistryInit(RegisterEvent event) {
		if (tome == null) {
			tome = new TomeItem();
			
			RegistryHelper.register(ATTACHMENT, AkashicTome.MOD_ID + ":attachment", Registry.RECIPE_SERIALIZER_REGISTRY);
		}
	}

}
