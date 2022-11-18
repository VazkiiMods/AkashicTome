package vazkii.akashictome;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegisterEvent;

public final class ModItems {

	public static Item tome;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRegistryInit(RegisterEvent.RegisterHelper<?> event) {
		if (tome == null)
			tome = new TomeItem();
	}

}
