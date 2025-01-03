package vazkii.akashictome;

import com.google.common.collect.Lists;

import net.neoforged.neoforge.common.ModConfigSpec;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Predicate;

public class ConfigHandler {

	public static ModConfigSpec.BooleanValue allItems;
	public static ModConfigSpec.ConfigValue<List<? extends String>> whitelistedItems, whitelistedNames, blacklistedMods, blacklistedItems;
	public static ModConfigSpec.ConfigValue<List<? extends String>> aliasesList;
	public static ModConfigSpec.BooleanValue hideBookRender;

	static final ConfigHandler CONFIG;
	static final ModConfigSpec CONFIG_SPEC;

	static {
		final Pair<ConfigHandler, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ConfigHandler::new);
		CONFIG = specPair.getLeft();
		CONFIG_SPEC = specPair.getRight();
	}

	public ConfigHandler(ModConfigSpec.Builder builder) {
		allItems = builder.define("Allow all items to be added", false);

		Predicate<Object> validator = o -> o instanceof String;

		whitelistedItems = builder.defineList("Whitelisted Items",
				Lists.newArrayList("roots:runedtablet",
						"opencomputers:tool:4",
						"immersiveengineering:tool:3",
						"integrateddynamics:on_the_dynamics_of_integration",
						"theoneprobe:probenote",
						"evilcraft:origins_of_darkness",
						"draconicevolution:info_tablet",
						"charset:tablet",
						"antiqueatlas:antique_atlas",
						"theurgy:grimiore",
						"tconstruct:materials_and_you",
						"tconstruct:puny_smelting",
						"tconstruct:mighty_smelting",
						"tconstruct:tinkers_gadgetry",
						"tconstruct:fantastic_foundry",
						"tetra:holo",
						"occultism:dictionary_of_spirits"),
				validator);

		whitelistedNames = builder.defineList("Whitelisted Names",
				Lists.newArrayList("book",
						"tome",
						"lexicon",
						"nomicon",
						"manual",
						"knowledge",
						"pedia",
						"compendium",
						"guide",
						"codex",
						"dictionary",
						"journal"),
				validator);

		blacklistedMods = builder.defineList("Blacklisted Mods", Lists.newArrayList(), validator);

		blacklistedItems = builder.defineList("Blacklisted Items", Lists.newArrayList(), validator);

		aliasesList = builder.defineList("Mod Aliases",
				Lists.newArrayList("nautralpledge=botania",
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
				validator);

		hideBookRender = builder.define("Hide Book Render", false);
	}
}
