package vazkii.akashictome;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(AkashicTome.MOD_ID)
public class AkashicRecipeSerializer {

    @ObjectHolder("attachment")
    public static RecipeSerializer<AttachementRecipe> ATTACHMENT;

    @Mod.EventBusSubscriber(modid = AkashicTome.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void onRecipeSerializerRegistry(final RegistryEvent.Register<RecipeSerializer<?>> event) {
            IForgeRegistry<RecipeSerializer<?>> registry = event.getRegistry();
            registry.register(new SimpleRecipeSerializer<>(AttachementRecipe::new).setRegistryName(AkashicTome.MOD_ID, "attachment"));
        }
    }
}
