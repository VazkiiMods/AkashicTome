package vazkii.akashictome;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;

import vazkii.akashictome.network.MessageUnmorphTome;
import vazkii.akashictome.network.NetworkHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class MorphingHandler {

	public static final MorphingHandler INSTANCE = new MorphingHandler();

	public static final String MINECRAFT = "minecraft";

	public static final String TAG_MORPHING = "akashictome:is_morphing";
	public static final String TAG_TOME_DATA = "akashictome:data";
	public static final String TAG_TOME_DISPLAY_NAME = "akashictome:displayName";
	public static final String TAG_ITEM_DEFINED_MOD = "akashictome:definedMod";

	@SubscribeEvent
	public void onPlayerLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
		ItemStack stack = event.getItemStack();
		if (!stack.isEmpty() && isAkashicTome(stack) && !stack.is(Registries.TOME.get())) {
			NetworkHandler.sendToServer(new MessageUnmorphTome()); //TODO fix
		}
	}

	@SubscribeEvent
	public void onItemDropped(ItemTossEvent event) {
		if (!event.getPlayer().isDiscrete())
			return;

		ItemEntity e = event.getEntity();
		ItemStack stack = e.getItem();
		if (!stack.isEmpty() && isAkashicTome(stack) && !stack.is(Registries.TOME.get())) {
			CompoundTag morphData = stack.getTag().getCompound(TAG_TOME_DATA).copy();
			String currentMod = NBTUtils.getString(stack, TAG_ITEM_DEFINED_MOD, getModFromStack(stack));

			ItemStack morph = makeMorphedStack(stack, MINECRAFT, morphData);
			CompoundTag newMorphData = morph.getTag().getCompound(TAG_TOME_DATA);
			newMorphData.remove(currentMod);

			if (!e.getCommandSenderWorld().isClientSide) {
				ItemEntity newItem = new ItemEntity(e.getCommandSenderWorld(), e.getX(), e.getY(), e.getZ(), morph);
				e.getCommandSenderWorld().addFreshEntity(newItem);
			}

			ItemStack copy = stack.copy();
			CompoundTag copyCmp = copy.getTag();
			if (copyCmp == null) {
				copyCmp = new CompoundTag();
				copy.setTag(copyCmp);
			}

			copyCmp.remove("display");
			/*
			Component displayName = null;
			CompoundTag nameCmp = (CompoundTag) copyCmp.get(TAG_TOME_DISPLAY_NAME);
			if (nameCmp != null)
				displayName = Component.literal(nameCmp.getString("text"));
			if (displayName != null && !displayName.getString().isEmpty() && displayName != copy.getHoverName())
				copy.setHoverName(displayName);

			 */

			copyCmp.remove(TAG_MORPHING);
			copyCmp.remove(TAG_TOME_DISPLAY_NAME);
			copyCmp.remove(TAG_TOME_DATA);

			e.setItem(copy);
		}
	}

	public static String getModFromState(BlockState state) {
		return getModOrAlias(ForgeRegistries.BLOCKS.getKey(state.getBlock()).getNamespace());
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
		if (!stack.hasTag())
			return false;

		CompoundTag morphData = stack.getTag().getCompound(TAG_TOME_DATA);
		return morphData.contains(mod);
	}

	public static ItemStack getShiftStackForMod(ItemStack stack, String mod) {
		if (!stack.hasTag())
			return stack;

		String currentMod = getModFromStack(stack);
		String defined = NBTUtils.getString(stack, TAG_ITEM_DEFINED_MOD, "");
		if (!defined.isEmpty())
			currentMod = defined;

		if (mod.equals(currentMod))
			return stack;

		CompoundTag morphData = stack.getTag().getCompound(TAG_TOME_DATA);
		return makeMorphedStack(stack, mod, morphData);
	}

	public static ItemStack makeMorphedStack(ItemStack currentStack, String targetMod, CompoundTag morphData) {
		String currentMod = getModFromStack(currentStack);
		String defined = NBTUtils.getString(currentStack, TAG_ITEM_DEFINED_MOD, "");
		if (!defined.isEmpty())
			currentMod = defined;

		CompoundTag currentCmp = new CompoundTag();
		currentStack.save(currentCmp);
		currentCmp = currentCmp.copy();
		if (currentCmp.contains("tag"))
			currentCmp.getCompound("tag").remove(TAG_TOME_DATA);

		if (!currentMod.equalsIgnoreCase(MINECRAFT) && !currentMod.equalsIgnoreCase(AkashicTome.MOD_ID))
			morphData.put(currentMod, currentCmp);

		ItemStack stack;
		if (targetMod.equals(MINECRAFT))
			stack = new ItemStack(Registries.TOME.get());
		else {
			CompoundTag targetCmp = morphData.getCompound(targetMod);
			morphData.remove(targetMod);

			stack = ItemStack.of(targetCmp);
			if (stack.isEmpty())
				stack = new ItemStack(Registries.TOME.get());
		}

		if (!stack.hasTag())
			stack.setTag(new CompoundTag());

		CompoundTag stackCmp = stack.getTag();
		stackCmp.put(TAG_TOME_DATA, morphData);
		stackCmp.putBoolean(TAG_MORPHING, true);

		if (!stack.is(Registries.TOME.get())) {
			CompoundTag displayName = new CompoundTag();
			CompoundTag ogDisplayName = displayName;
			displayName.putString("text", Component.Serializer.toJson(stack.getHoverName()));

			if (stackCmp.contains(TAG_TOME_DISPLAY_NAME))
				displayName = (CompoundTag) stackCmp.get(TAG_TOME_DISPLAY_NAME);
			else
				stackCmp.put(TAG_TOME_DISPLAY_NAME, displayName);


			MutableComponent rawComp = Component.Serializer.fromJson(displayName.getString("text"));
			if (rawComp == null) {
				stackCmp.put(TAG_TOME_DISPLAY_NAME, displayName);
				displayName = ogDisplayName;
			}

			Component stackName = rawComp.setStyle(Style.EMPTY.applyFormats(ChatFormatting.GREEN));
			Component comp = Component.translatable("akashictome.sudo_name", stackName);
			stack.setHoverName(comp);
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

		return stack.hasTag() && stack.getTag().getBoolean(TAG_MORPHING);
	}

}
