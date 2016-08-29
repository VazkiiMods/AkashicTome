package vazkii.akashictome.proxy;

import net.minecraft.block.Block;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class CommonProxy {

	public void updateEquippedItem() {
		// NO-OP
	}

	public void initHUD() {
		// NO-OP
	}
	
	public boolean openWikiPage(World world, Block block, RayTraceResult pos) {
		return false;
	}
	
}
