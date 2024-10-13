package vazkii.akashictome;

import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
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

import vazkii.akashictome.network.MessageUnmorphTome;
import vazkii.akashictome.network.NetworkHandler;

import java.util.HashMap;
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
		if (!event.getPlayer().isDiscrete())
			return;

		ItemEntity e = event.getEntity();
		ItemStack stack = e.getItem();
		if (!stack.isEmpty() && isAkashicTome(stack) && !stack.is(Registries.TOME.get())) {
			CompoundTag morphData = stack.getOrDefault(Registries.TOME_DATA, new CompoundTag()).copy();
			String currentMod = stack.getOrDefault(Registries.DEFINED_MOD, getModFromStack(stack));

			ItemStack morph = makeMorphedStack(stack, MINECRAFT, morphData, e.registryAccess());
			CompoundTag newMorphData = morph.getOrDefault(Registries.TOME_DATA, new CompoundTag()).copy();
			newMorphData.remove(currentMod);
			morph.set(Registries.TOME_DATA, newMorphData);

			if (!e.level().isClientSide) {
				ItemEntity newItem = new ItemEntity(e.level(), e.getX(), e.getY(), e.getZ(), morph);
				e.level().addFreshEntity(newItem);
			}

			ItemStack copy = stack.copy();

			copy.remove(DataComponents.CUSTOM_NAME);

			copy.remove(Registries.IS_MORPHING);
			copy.remove(Registries.DISPLAY_NAME);
			copy.remove(Registries.DEFINED_MOD);
			copy.remove(Registries.TOME_DATA);
			System.out.println(copy.getComponents());

			e.setItem(copy);
		}
	}

	public static String getModFromState(BlockState state) {
		return getModOrAlias(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace());
	}

	public static String getModFromStack(ItemStack stack) {
		return getModOrAlias(stack.isEmpty() ? MINECRAFT : stack.getItem().getCreatorModId(stack));
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

	public static boolean doesStackHaveModAttached(ItemStack stack, String mod) { //TODO what was this used for?
		if (!stack.has(Registries.TOME_DATA))
			return false;

		CompoundTag morphData = stack.getOrDefault(Registries.TOME_DATA, new CompoundTag());
		return morphData.contains(mod);
	}

	public static ItemStack getShiftStackForMod(ItemStack stack, String mod, RegistryAccess registryAccess) {
		if (!stack.has(Registries.TOME_DATA))
			return stack;

		String currentMod = getModFromStack(stack);
		String defined = stack.getOrDefault(Registries.DEFINED_MOD, "");
		if (!defined.isEmpty())
			currentMod = defined;

		if (mod.equals(currentMod))
			return stack;

		CompoundTag morphData = stack.getOrDefault(Registries.TOME_DATA, new CompoundTag());
		return makeMorphedStack(stack, mod, morphData, registryAccess);
	}

	public static ItemStack makeMorphedStack(ItemStack currentStack, String targetMod, CompoundTag morphData, RegistryAccess registryAccess) {
		String currentMod = getModFromStack(currentStack);
		String defined = currentStack.getOrDefault(Registries.DEFINED_MOD, "");
		if (!defined.isEmpty())
			currentMod = defined;

		ItemStack copyStack = currentStack.copy();
		copyStack.remove(Registries.TOME_DATA);
		copyStack.save(registryAccess, new CompoundTag());

		if (!currentMod.equalsIgnoreCase(MINECRAFT) && !currentMod.equalsIgnoreCase(AkashicTome.MOD_ID))
			morphData.put(currentMod, copyStack.save(registryAccess, new CompoundTag()).copy());

		ItemStack stack;
		if (targetMod.equals(MINECRAFT))
			stack = new ItemStack(Registries.TOME.get());
		else {
			CompoundTag targetCmp = morphData.getCompound(targetMod);
			morphData.remove(targetMod);

			stack = ItemStack.parseOptional(registryAccess, targetCmp);
			if (stack.isEmpty())
				stack = new ItemStack(Registries.TOME.get());
		}

		stack.set(Registries.TOME_DATA, morphData);
		stack.set(Registries.IS_MORPHING, true);

		if (!stack.is(Registries.TOME.get())) {
			Component component = stack.getOrDefault(Registries.DISPLAY_NAME, stack.getHoverName());
			Component stackName = component.copy().setStyle(Style.EMPTY.applyFormats(ChatFormatting.GREEN));
			Component comp = Component.translatable("akashictome.sudo_name", stackName);
			stack.set(DataComponents.CUSTOM_NAME, comp);
		}

		stack.setCount(1);
		return stack;
	}

	private static final Map<String, String> modNames = new HashMap<>();

	static {
		for (IModInfo modEntry : ModList.get().getMods())
			modNames.put(modEntry.getModId().toLowerCase(Locale.ENGLISH), modEntry.getDisplayName());
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

		return stack.getOrDefault(Registries.IS_MORPHING, false);
	}

}
