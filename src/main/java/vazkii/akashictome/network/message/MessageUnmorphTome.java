package vazkii.akashictome.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.akashictome.MorphingHandler;
import vazkii.akashictome.Registrar;

import java.util.function.Supplier;

public class MessageUnmorphTome {

	public MessageUnmorphTome() { }

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		ItemStack stack = player.getHeldItemMainhand();
		if (MorphingHandler.isAkashicTome(stack) && stack.getItem() != Registrar.TOME) {
			ItemStack newStack = MorphingHandler.getShiftStackForMod(stack, MorphingHandler.MINECRAFT);
			newStack.removeChildTag(MorphingHandler.TAG_MORPHING);
			player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
					Minecraft.getInstance().getFirstPersonRenderer().resetEquippedProgress(Hand.MAIN_HAND));
		}

		ctx.get().setPacketHandled(true);
	}
}
