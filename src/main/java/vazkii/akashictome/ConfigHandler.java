package vazkii.akashictome;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigHandler {
	public static ForgeConfigSpec.BooleanValue allItems;

	public static ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistedNames;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedMods;

	public static final Map<String, String> aliases = new HashMap<>();
	public static final Set<ResourceLocation> whitelistedItems = new HashSet<>();

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> rawAliases;
	private static ForgeConfigSpec.ConfigValue<List<? extends String>> rawWhitelistedItems;

	static final ForgeConfigSpec SPEC = new ForgeConfigSpec.Builder().configure(ConfigHandler::new).getRight();
	
	private ConfigHandler(ForgeConfigSpec.Builder builder) {
		allItems = builder.comment("Allow all items to be added to the tome").define("allowAllItems", false);

		rawWhitelistedItems = builder.comment("Whitelisted Items").defineList("whitelistedItems", Arrays.asList(
				"roots:runedtablet",
//				"opencomputers:tool:4", TODO check all those/find replacements
//				"immersiveengineering:tool:3",
				"integrateddynamics:on_the_dynamics_of_integration",
				"theoneprobe:probenote",
				"evilcraft:origins_of_darkness",
				"draconicevolution:info_tablet",
				"charset:tablet"),
				s -> s instanceof String && ResourceLocation.isResouceNameValid((String) s));

		whitelistedNames = builder.defineList("Whitelisted Names",
				Arrays.asList("book", "tome", "lexicon", "nomicon", "manual",
						"knowledge", "pedia", "compendium", "guide", "codex", "journal"),
				s -> s instanceof String);

		blacklistedMods = builder.defineList("Blacklisted Mods", Collections.emptyList(), s -> true);

		rawAliases = builder.defineList("Mod Aliases", Arrays.asList(
				"naturalpledge=botania",
				"incorporeal=botania",
				"thermalexpansion=thermalfoundation",
				"thermaldynamics=thermalfoundation",
				"thermalcultivation=thermalfoundation",
				"redstonearsenal=thermalfoundation",
				"rftoolsdim=rftools",
				"ae2stuff=appliedenergistics2",
				"animus=bloodmagic",
				"integrateddynamics=integratedtunnels",
				"mekanismgenerators=mekanism",
				"mekanismtools=mekanism"),
				s -> s instanceof String && ((String) s).split("=", 2).length == 2);
	}

	public static void onConfigLoad(ModConfig.Loading event) {
		reload();
	}

	public static void onConfigReload(ModConfig.ConfigReloading event) {
		reload();
	}

	private static void reload() {
		aliases.clear();
		for (String s : rawAliases.get())
			if (s.matches(".+?=.+")) {
				String[] tokens = s.toLowerCase().split("=");
				aliases.put(tokens[0], tokens[1]);
			}

		whitelistedItems.clear();
		for (String s : rawWhitelistedItems.get()) {
			whitelistedItems.add(new ResourceLocation(s));
		}
	}
}
