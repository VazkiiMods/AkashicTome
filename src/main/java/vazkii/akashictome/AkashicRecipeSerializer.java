package vazkii.akashictome;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

public class AkashicRecipeSerializer {

	public static final RecipeSerializer<AttachementRecipe> ATTACHMENT = new SimpleRecipeSerializer<>(AttachementRecipe::new);

	@Mod.EventBusSubscriber(modid = AkashicTome.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Registration {
		@SubscribeEvent
		public static void onRecipeSerializerRegistry(final RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
			event.register(new ResourceLocation(AkashicTome.MOD_ID, "attachment"), ATTACHMENT);
		}
	}
}
