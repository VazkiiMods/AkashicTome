package vazkii.akashictome;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforgespi.language.IModInfo;

import vazkii.akashictome.data_components.ToolContentComponent;
import vazkii.akashictome.network.MessageUnmorphTome;
import vazkii.akashictome.network.NetworkHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class MorphingHandler {

	public static final MorphingHandler INSTANCE = new MorphingHandler();

	public static final String MINECRAFT = "minecraft";

	@SubscribeEvent
	public void onPlayerLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
		ItemStack stack = event.getItemStack();
		if (!stack.isEmpty() && isAkashicTome(stack) && !stack.is(Registries.TOME.get())) {
			NetworkHandler.sendToServer(new MessageUnmorphTome());
		}
	}

	@SubscribeEvent
	public void onItemDropped(ItemTossEvent event) {
		if (!event.getPlayer().isDiscrete()) {
			return;
		}

		ItemEntity e = event.getEntity();
		ItemStack stack = e.getItem();

		if (!stack.isEmpty() && isAkashicTome(stack) && !stack.is(Registries.TOME.get())) {
			ToolContentComponent contents = stack.get(Registries.TOOL_CONTENT);
			if (contents == null) {
				return;
			}
			ToolContentComponent.Mutable mutable = new ToolContentComponent.Mutable(contents);

			mutable.remove(stack);
			stack.set(Registries.TOOL_CONTENT, mutable.toImmutable());

			ItemStack morph = makeMorphedStack(stack, MINECRAFT, true);

			if (!e.getCommandSenderWorld().isClientSide) {
				ItemEntity newItem = new ItemEntity(e.getCommandSenderWorld(), e.getX(), e.getY(), e.getZ(), morph);
				e.getCommandSenderWorld().addFreshEntity(newItem);
			}

			ItemStack copy = stack.copy();
			copy.remove(Registries.TOOL_CONTENT);
			copy.remove(Registries.IS_MORPHED);
			copy.remove(DataComponents.CUSTOM_NAME);
			copy.remove(Registries.OG_DISPLAY_NAME);
			copy.remove(Registries.DEFINED_MOD);

			e.setItem(copy);
		}
	}

	public static String getModFromState(BlockState state) {
		return getModOrAlias(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace());
	}

	public static String getModFromStack(ItemStack stack) {
		String modId = stack.getItem().getCreatorModId(stack);
		return getModOrAlias(stack.isEmpty() ? MINECRAFT : modId != null ? modId : MINECRAFT);
	}

	public static String getModOrAlias(String mod) {
		Map<String, String> aliases = new HashMap<>();

		for (String s : ConfigHandler.aliasesList.get())
			if (s.matches(".+?=.+")) {
				String[] tokens = s.toLowerCase().split("=");
				aliases.put(tokens[0], tokens[1]);
			}

		return aliases.getOrDefault(mod, mod);
	}

	public static ItemStack getShiftStackForMod(ItemStack stack, String mod) {
		if (!stack.has(Registries.TOOL_CONTENT)) {
			return stack;
		}

		String currentMod = getModFromStack(stack);

		String defined = "";
		if (stack.has(Registries.DEFINED_MOD)) {
			defined = stack.get(Registries.DEFINED_MOD);
		}

		if (!defined.isEmpty()) {
			currentMod = defined;
		}

		if (mod.equals(currentMod)) {
			return stack;
		}

		return makeMorphedStack(stack, mod, false);
	}

	public static ItemStack makeMorphedStack(ItemStack currentStack, String targetMod, boolean calledOnRemove) {
		String currentMod = getModFromStack(currentStack);

		String defined = "";
		if (currentStack.has(Registries.DEFINED_MOD)) {
			defined = currentStack.get(Registries.DEFINED_MOD);
		}

		if (!defined.isEmpty()) {
			currentMod = defined;
		}

		ToolContentComponent currentContent = currentStack.get(Registries.TOOL_CONTENT);
		currentStack.remove(Registries.TOOL_CONTENT);
		ToolContentComponent newStackComponent = new ToolContentComponent(List.of(currentStack));
		if (currentContent == null)
			return ItemStack.EMPTY;

		ToolContentComponent.Mutable mutable = getMutable(currentContent, newStackComponent, currentMod, calledOnRemove);

		ItemStack stack;
		if (targetMod.equals(MINECRAFT)) {
			stack = new ItemStack(Registries.TOME.get());
		} else {
			stack = getStackFromMod(currentContent, targetMod);

			if (stack.isEmpty()) {
				stack = new ItemStack(Registries.TOME.get());
			}
		}

		mutable.remove(stack);

		stack.set(Registries.TOOL_CONTENT, mutable.toImmutable());
		stack.set(Registries.IS_MORPHED, true);

		if (!stack.is(Registries.TOME.get())) {
			Component hoverName = getOrSetOGName(stack);
			Component stackName = Component.literal(hoverName.getString()).setStyle(Style.EMPTY.applyFormats(ChatFormatting.GREEN));
			Component comp = Component.translatable("akashictome.sudo_name", stackName);
			stack.set(DataComponents.CUSTOM_NAME, comp);
		}

		stack.setCount(1);
		return stack;
	}

	private static Component getOrSetOGName(ItemStack stack) {
		Component hoverName = stack.getHoverName();
		if (!stack.has(Registries.OG_DISPLAY_NAME)) {
			stack.set(Registries.OG_DISPLAY_NAME, hoverName);
		} else {
			hoverName = stack.get(Registries.OG_DISPLAY_NAME);
		}

		return hoverName;
	}

	private static ToolContentComponent.Mutable getMutable(ToolContentComponent currentContent, ToolContentComponent newStackComponent, String currentMod, boolean calledOnRemove) {
		ToolContentComponent.Mutable currentContentMutable = new ToolContentComponent.Mutable(currentContent);
		if (!currentMod.equalsIgnoreCase(MINECRAFT) && !currentMod.equalsIgnoreCase(AkashicTome.MOD_ID) && !calledOnRemove) {
			currentContentMutable.tryInsert(newStackComponent.getItems().getFirst());
		}
		return currentContentMutable;
	}

	public static ItemStack getStackFromMod(ToolContentComponent component, String mod) {
		if (component != null && !component.isEmpty()) {
			for (ItemStack contentStack : component.getItems()) {
				if (contentStack.has(Registries.DEFINED_MOD)) {
					if (contentStack.get(Registries.DEFINED_MOD).equals(mod)) {
						return contentStack;
					} else if (BuiltInRegistries.ITEM.getKey(contentStack.getItem()).getNamespace().equals(mod)) {
						return contentStack;
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	private static final Map<String, String> modNames = new HashMap<>();

	static {
		for (IModInfo modEntry : ModList.get().getMods()) {
			modNames.put(modEntry.getModId().toLowerCase(Locale.ENGLISH), modEntry.getDisplayName());
		}
	}

	public static String getModNameForId(String modId) {
		modId = modId.toLowerCase(Locale.ENGLISH);
		return modNames.getOrDefault(modId, modId);
	}

	public static boolean isAkashicTome(ItemStack stack) {
		if (stack.isEmpty())
			return false;

		if (stack.is(Registries.TOME.get()))
			return true;

		return stack.has(Registries.IS_MORPHED) && Boolean.TRUE.equals(stack.get(Registries.IS_MORPHED));
	}

}
