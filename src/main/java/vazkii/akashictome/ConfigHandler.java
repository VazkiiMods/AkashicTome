package vazkii.akashictome;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {

	public static ForgeConfigSpec.BooleanValue allItems;
	public static ForgeConfigSpec.ConfigValue<List<String>> whitelistedItems, whitelistedNames, blacklistedMods;
	public static ForgeConfigSpec.ConfigValue<List<String>> aliasesList;
	
	static final ConfigHandler CONFIG;
	static final ForgeConfigSpec CONFIG_SPEC;

	static{
		final Pair<ConfigHandler, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigHandler::new);
		CONFIG = specPair.getLeft();
		CONFIG_SPEC = specPair.getRight();
	}

	public ConfigHandler(ForgeConfigSpec.Builder builder) {
		allItems = builder.define("Allow all items to be added", false);
		
		whitelistedItems = builder.defineList("Whitelisted Items",
				Arrays.asList("roots:runedtablet",
						"opencomputers:tool:4", 
						"immersiveengineering:tool:3", 
						"integrateddynamics:on_the_dynamics_of_integration", 
						"theoneprobe:probenote",
						"evilcraft:origins_of_darkness",
						"draconicevolution:info_tablet",
						"charset:tablet"));
		
		whitelistedNames = builder.define("Whitelisted Names", Arrays.asList("book", "tome", "lexicon", "nomicon", "manual", "knowledge", "pedia", "compendium", "guide", "codex", "journal"));
		
		blacklistedMods = builder.define("Blacklisted Mods", Arrays.asList());

		aliasesList = builder.define("Mod Aliases",
				Arrays.asList("nautralpledge=botania",
				"thermalexpansion=thermalfoundation",
				"thermaldynamics=thermalfoundation",
				"thermalcultivation=thermalfoundation", 
				"redstonearsenal=thermalfoundation",
				"rftoolsdim=rftools",
				"rftoolspower=rftools",
				"rftoolscontrol=rftools",
				"ae2stuff=appliedenergistics2",
				"animus=bloodmagic",
				"integrateddynamics=integratedtunnels",
				"mekanismgenerators=mekanism",
				"mekanismtools=mekanism",
				"deepresonance=rftools",
				"xnet=rftools",
				"buildcrafttransport=buildcraft",
				"buildcraftfactory=buildcraft",
				"buildcraftsilicon=buildcraft"));

	}
}
