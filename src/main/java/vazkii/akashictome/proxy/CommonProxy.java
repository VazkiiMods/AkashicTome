package vazkii.akashictome.proxy;

import net.minecraft.block.Block;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class CommonProxy {
	public boolean openWikiPage(World world, Block block, RayTraceResult pos) {
		return false;
	}
}
