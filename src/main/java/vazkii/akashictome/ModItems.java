package vazkii.akashictome;

import net.minecraft.item.Item;

public final class ModItems {

	public static Item tome;
	
	public static void init() {
		tome = new ItemTome();
	}
	
}
