package vazkii.akashictome;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public final class Registrar {

	@ObjectHolder(AkashicTome.MOD_ID + ":tome")
	public static Item TOME;

	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemTome().setRegistryName(AkashicTome.MOD_ID, "tome"));
	}

	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		event.getRegistry().register(AttachementRecipe.SERIALIZER.setRegistryName(new ResourceLocation(AkashicTome.MOD_ID, "tome_attachment")));
	}
}
