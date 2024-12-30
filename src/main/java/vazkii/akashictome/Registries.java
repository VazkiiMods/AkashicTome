package vazkii.akashictome;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import vazkii.akashictome.data_components.ToolContentComponent;

import java.util.function.Supplier;

public final class Registries {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AkashicTome.MOD_ID);
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, AkashicTome.MOD_ID);
	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(AkashicTome.MOD_ID);

	public static final Supplier<DataComponentType<ToolContentComponent>> TOOL_CONTENT = DATA_COMPONENTS.registerComponentType("tool_content", builder -> builder.persistent(ToolContentComponent.CODEC).networkSynchronized(ToolContentComponent.STREAM_CODEC).cacheEncoding());
	public static final Supplier<DataComponentType<Boolean>> IS_MORPHED = DATA_COMPONENTS.registerComponentType("is_morphed", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
	public static final Supplier<DataComponentType<Component>> OG_DISPLAY_NAME = DATA_COMPONENTS.register("og_display_name", () -> DataComponentType.<Component>builder().persistent(ComponentSerialization.FLAT_CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).build());
	public static final Supplier<DataComponentType<String>> DEFINED_MOD = DATA_COMPONENTS.register("defined_mod", () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

	public static final DeferredItem<Item> TOME = ITEMS.registerItem("tome", TomeItem::new);

	public static final Supplier<RecipeSerializer<AttachementRecipe>> ATTACHMENT = SERIALIZERS.register("attachment", () -> new SimpleCraftingRecipeSerializer<>(AttachementRecipe::new));

}
