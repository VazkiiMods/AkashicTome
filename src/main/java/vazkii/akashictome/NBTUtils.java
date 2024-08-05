package vazkii.akashictome;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

//Some Nbt Utils ported from ARL which doesn't exist anymore
public class NBTUtils {
    public static CompoundTag getNBT(ItemStack stack) {
        if(!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        return stack.getTag();
    }

    public static boolean verifyExistence(ItemStack stack, String tag) {
        return !stack.isEmpty() && stack.hasTag() && getNBT(stack).contains(tag);
    }

    public static String getString(ItemStack stack, String tag, String defaultExpected) {
        return verifyExistence(stack, tag) ? getNBT(stack).getString(tag) : defaultExpected;
    }

    public static void setString(ItemStack stack, String tag, String s) {
        getNBT(stack).putString(tag, s);
    }

    public static CompoundTag getCompound(ItemStack stack, String tag, boolean nullifyOnFail) {
        return verifyExistence(stack, tag) ? getNBT(stack).getCompound(tag) : nullifyOnFail ? null : new CompoundTag();
    }
}
