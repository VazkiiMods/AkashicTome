package vazkii.akashictome;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import vazkii.akashictome.network.PacketHandler;
import vazkii.akashictome.network.message.MessageUnmorphTome;

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
		if (isAkashicTome(stack) && stack.getItem() != Registrar.TOME) {
			PacketHandler.sendToServer(new MessageUnmorphTome());
		}
	}

	@SubscribeEvent
	public void onItemDropped(ItemTossEvent event) {
		if (!event.getPlayer().isSneaking())
			return;

		ItemEntity e = event.getEntityItem();
		ItemStack stack = e.getItem();
		if (!stack.isEmpty() && isAkashicTome(stack) && stack.getItem() != Registrar.TOME) {
			CompoundNBT morphData = stack.getChildTag(TAG_TOME_DATA).copy();
			String currentMod = getDefinedModFromStack(stack);

			ItemStack morph = makeMorphedStack(stack, MINECRAFT, morphData);
			CompoundNBT newMorphData = morph.getChildTag(TAG_TOME_DATA);
			newMorphData.remove(currentMod);

			if (!e.getEntityWorld().isRemote) {
				ItemEntity newItem = new ItemEntity(e.getEntityWorld(), e.posX, e.posY, e.posZ, morph);
				e.getEntityWorld().addEntity(newItem);
			}

			ItemStack copy = stack.copy();
			CompoundNBT copyCmp = copy.getTag();
			if (copyCmp == null) {
				copyCmp = new CompoundNBT();
				copy.setTag(copyCmp);
			}

			copyCmp.remove("display");

			String displayName = copyCmp.getString(TAG_TOME_DISPLAY_NAME);
			if (!displayName.isEmpty()) {
				ITextComponent deserializedName = ITextComponent.Serializer.fromJson(displayName);
				if (!deserializedName.equals(copy.getDisplayName()))
					copy.setDisplayName(deserializedName);
			}

			copyCmp.remove(TAG_MORPHING);
			copyCmp.remove(TAG_TOME_DISPLAY_NAME);
			copyCmp.remove(TAG_TOME_DATA);
			copyCmp.remove(TAG_ITEM_DEFINED_MOD);

			e.setItem(copy);
		}
	}

	public static String getModFromState(BlockState state) {
		return getModOrAlias(state.getBlock().getRegistryName().getNamespace());
	}

	public static String getModFromStack(ItemStack stack) {
		return getModOrAlias(stack.isEmpty() ? MINECRAFT : stack.getItem().getCreatorModId(stack));
	}

	public static String getDefinedModFromStack(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains(TAG_ITEM_DEFINED_MOD)) {
			return stack.getTag().getString(TAG_ITEM_DEFINED_MOD);
		}
		return getModFromStack(stack);
	}

	public static String getModOrAlias(String mod) {
		return ConfigHandler.aliases.getOrDefault(mod, mod);
	}

	public static ITextComponent getMorphedDisplayName(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains(TAG_TOME_DISPLAY_NAME)) {
			String string = stack.getTag().getString(TAG_TOME_DISPLAY_NAME);
			if (!string.isEmpty())
				return ITextComponent.Serializer.fromJson(string);
		}
		return stack.getDisplayName();
	}

	public static ItemStack getShiftStackForMod(ItemStack stack, String mod) {
		if (!stack.hasTag())
			return stack;

		String currentMod = getModFromStack(stack);
		if (mod.equals(currentMod))
			return stack;

		CompoundNBT morphData = stack.getOrCreateChildTag(TAG_TOME_DATA);
		return makeMorphedStack(stack, mod, morphData);
	}

	public static ItemStack makeMorphedStack(ItemStack currentStack, String targetMod, CompoundNBT morphData) {
		String currentMod = getDefinedModFromStack(currentStack);

		CompoundNBT currentCmp = currentStack.write(new CompoundNBT()).copy();

		if (currentCmp.contains("tag"))
			currentCmp.getCompound("tag").remove(TAG_TOME_DATA);

		if (!currentMod.equalsIgnoreCase(MINECRAFT) && !currentMod.equalsIgnoreCase(AkashicTome.MOD_ID))
			morphData.put(currentMod, currentCmp);

		ItemStack stack;
		if (targetMod.equals(MINECRAFT))
			stack = new ItemStack(Registrar.TOME);
		else {
			CompoundNBT targetCmp = morphData.getCompound(targetMod);
			morphData.remove(targetMod);

			stack = ItemStack.read(targetCmp);
			if (stack.isEmpty())
				stack = new ItemStack(Registrar.TOME);
		}
		
		CompoundNBT stackCmp = stack.getOrCreateTag();
		stackCmp.put(TAG_TOME_DATA, morphData);
		stackCmp.putBoolean(TAG_MORPHING, true);

		if (stack.getItem() != Registrar.TOME) {
			ITextComponent displayName = stack.getDisplayName();
			if (stackCmp.contains(TAG_TOME_DISPLAY_NAME))
				displayName = ITextComponent.Serializer.fromJson(stackCmp.getString(TAG_TOME_DISPLAY_NAME));
			else stackCmp.putString(TAG_TOME_DISPLAY_NAME, ITextComponent.Serializer.toJson(stack.getDisplayName()));

			stack.setDisplayName(new TranslationTextComponent("akashictome.sudo_name", displayName.deepCopy().applyTextStyle(TextFormatting.GREEN)));
		}

		stack.setCount(1);
		return stack;
	}

	private static final Map<String, String> modNames = new HashMap<>();

	static {
		for (ModInfo modEntry : ModList.get().getMods())
			modNames.put(modEntry.getModId(), modEntry.getDisplayName());
	}

	public static String getModNameForId(String modId) {
		modId = modId.toLowerCase(Locale.ENGLISH);
		return modNames.getOrDefault(modId, modId);
	}

	public static boolean isAkashicTome(ItemStack stack) {
		if (stack.isEmpty())
			return false;

		if (stack.getItem() == Registrar.TOME)
			return true;

		return stack.hasTag() && stack.getTag().getBoolean(TAG_MORPHING);
	}

}
