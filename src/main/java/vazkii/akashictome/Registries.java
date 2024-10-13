package vazkii.akashictome;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class Registries {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AkashicTome.MOD_ID);
	public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, AkashicTome.MOD_ID);
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, AkashicTome.MOD_ID);

	public static final Supplier<DataComponentType<Boolean>> IS_MORPHING = COMPONENT_TYPES.register("is_morphing", () -> DataComponentType.<Boolean>builder()
			.persistent(Codec.BOOL)
			.networkSynchronized(ByteBufCodecs.BOOL)
			.build());

	public static final Supplier<DataComponentType<CompoundTag>> TOME_DATA = COMPONENT_TYPES.register("tome_data", () -> DataComponentType.<CompoundTag>builder()
			.persistent(CompoundTag.CODEC)
			.networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
			.build());

	public static final Supplier<DataComponentType<Component>> DISPLAY_NAME = COMPONENT_TYPES.register("display_name", () -> DataComponentType.<Component>builder()
			.persistent(ComponentSerialization.FLAT_CODEC)
			.networkSynchronized(ComponentSerialization.STREAM_CODEC)
			.build());

	public static final Supplier<DataComponentType<String>> DEFINED_MOD = COMPONENT_TYPES.register("defined_mod", () -> DataComponentType.<String>builder()
			.persistent(Codec.STRING)
			.networkSynchronized(ByteBufCodecs.STRING_UTF8)
			.build());

	public static final DeferredItem<TomeItem> TOME = ITEMS.register("tome", TomeItem::new);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AttachementRecipe>> ATTACHMENT = SERIALIZERS.register("attachment", () -> new SimpleCraftingRecipeSerializer<>(AttachementRecipe::new));

}
