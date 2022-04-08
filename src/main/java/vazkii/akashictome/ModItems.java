package vazkii.akashictome;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ModItems {

	public static Item tome;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRegistryInit(RegistryEvent.Register<?> event) {
		if (tome == null)
			tome = new TomeItem();
	}

}
