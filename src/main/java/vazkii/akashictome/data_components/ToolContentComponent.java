package vazkii.akashictome.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import vazkii.akashictome.Registries;

import java.util.ArrayList;
import java.util.List;

public class ToolContentComponent {
	public static final ToolContentComponent EMPTY = new ToolContentComponent(List.of());
	public static final Codec<ToolContentComponent> CODEC = ItemStack.CODEC
			.listOf()
			.flatXmap(ToolContentComponent::checkAndCreate, component -> DataResult.success(component.items));
	public static final StreamCodec<RegistryFriendlyByteBuf, ToolContentComponent> STREAM_CODEC = ItemStack.STREAM_CODEC
			.apply(ByteBufCodecs.list())
			.map(ToolContentComponent::new, component -> component.items);
	final List<ItemStack> items;

	public ToolContentComponent(List<ItemStack> contents) {
		this.items = contents;
	}

	private static DataResult<ToolContentComponent> checkAndCreate(List<ItemStack> stacks) {
		return DataResult.success(new ToolContentComponent(stacks));
	}

	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	public List<ItemStack> getItems() {
		return this.items;
	}

	public boolean hasDefinedMod(String mod) {
		for (ItemStack stack : this.items) {
			if (stack.has(Registries.DEFINED_MOD)) {
				if (stack.get(Registries.DEFINED_MOD).equals(mod)) {
					return true;
				}
			} else if (BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals(mod)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return object instanceof ToolContentComponent component && ItemStack.listMatches(this.items, component.items);
		}
	}

	@Override
	public int hashCode() {
		return ItemStack.hashStackList(this.items);
	}

	@Override
	public String toString() {
		return "ToolContents" + this.items;
	}

	public static class Mutable {
		private final List<ItemStack> items;

		public Mutable(ToolContentComponent component) {
			this.items = new ArrayList<>(component.items);
		}

		public void tryInsert(ItemStack stack) {
			if (!stack.isEmpty()) {
				ItemStack itemstack1 = stack.copy();
				this.items.add(itemstack1);
			}
		}

		public void remove(ItemStack stack) {
			this.items.remove(stack);
		}

		public ToolContentComponent toImmutable() {
			return new ToolContentComponent(List.copyOf(this.items));
		}
	}
}
