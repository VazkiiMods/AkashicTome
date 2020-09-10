package vazkii.akashictome;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {

	public static ForgeConfigSpec.BooleanValue allItems;
	public static ConfigValue<List<? extends String>> whitelistedItems;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistedNames;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedMods;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> aliasesList;
	
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
						"charset:tablet"),
				s -> s instanceof String);
		
		whitelistedNames = builder.defineList("Whitelisted Names",
				Arrays.asList("book",
						"tome",
						"lexicon",
						"nomicon",
						"manual",
						"knowledge",
						"pedia",
						"compendium",
						"guide",
						"codex",
						"journal"),
				s -> s instanceof String);
		
		blacklistedMods = builder.defineList("Blacklisted Mods",
				Arrays.asList(),
				s -> s instanceof String);

		aliasesList = builder.defineList("Mod Aliases",
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
				"buildcraftsilicon=buildcraft"),
				s -> s instanceof String);

	}
}
