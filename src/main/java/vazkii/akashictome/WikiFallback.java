package vazkii.akashictome;

import vazkii.botania.api.wiki.IWikiProvider;
import vazkii.botania.api.wiki.SimpleWikiProvider;
import vazkii.botania.api.wiki.WikiHooks;

public class WikiFallback {

	public static void doWikiRegister() {
		registerModWiki("Minecraft", new SimpleWikiProvider("Minecraft Wiki", "http://minecraft.gamepedia.com/%s"));

		IWikiProvider technicWiki = new SimpleWikiProvider("Technic Wiki", "http://wiki.technicpack.net/%s");
		IWikiProvider mekanismWiki = new SimpleWikiProvider("Mekanism Wiki", "http://wiki.aidancbrady.com/wiki/%s");
		IWikiProvider buildcraftWiki = new SimpleWikiProvider("BuildCraft Wiki", "http://www.mod-buildcraft.com/wiki/doku.php?id=%s");

		registerModWiki("Mekanism", mekanismWiki);
		registerModWiki("MekanismGenerators", mekanismWiki);
		registerModWiki("MekanismTools", mekanismWiki);
		registerModWiki("EnderIO", new SimpleWikiProvider("EnderIO Wiki", "http://wiki.enderio.com/%s"));
		registerModWiki("TropiCraft", new SimpleWikiProvider("Tropicraft Wiki", "http://wiki.tropicraft.net/wiki/%s"));
		registerModWiki("RandomThings", new SimpleWikiProvider("Random Things Wiki", "http://randomthingsminecraftmod.wikispaces.com/%s"));
		registerModWiki("Witchery", new SimpleWikiProvider("Witchery Wiki", "https://sites.google.com/site/witcherymod/%s", "-", true));
		registerModWiki("AppliedEnergistics2", new SimpleWikiProvider("AE2 Wiki", "http://ae-mod.info/%s"));
		registerModWiki("BigReactors", technicWiki);
		registerModWiki("BuildCraft|Core", buildcraftWiki);
		registerModWiki("BuildCraft|Builders", buildcraftWiki);
		registerModWiki("BuildCraft|Energy", buildcraftWiki);
		registerModWiki("BuildCraft|Factory", buildcraftWiki);
		registerModWiki("BuildCraft|Silicon", buildcraftWiki);
		registerModWiki("BuildCraft|Transport", buildcraftWiki);
		registerModWiki("ArsMagica2", new SimpleWikiProvider("ArsMagica2 Wiki", "http://wiki.arsmagicamod.com/wiki/%s"));
		registerModWiki("PneumaticCraft", new SimpleWikiProvider("PneumaticCraft Wiki", "http://www.minemaarten.com/wikis/pneumaticcraft-wiki/pneumaticcraft-wiki-%s"));
		registerModWiki("StevesCarts2", new SimpleWikiProvider("Steve's Carts Wiki", "http://stevescarts2.wikispaces.com/%s"));
		registerModWiki("GanysSurface", new SimpleWikiProvider("Gany's Surface Wiki", "http://ganys-surface.wikia.com/wiki/%s"));
		registerModWiki("GanysNether", new SimpleWikiProvider("Gany's Nether Wiki", "http://ganys-nether.wikia.com/wiki/%s"));
		registerModWiki("GanysEnd", new SimpleWikiProvider("Gany's End Wiki", "http://ganys-end.wikia.com/wiki/%s"));
	}
	
	public static void registerModWiki(String mod, IWikiProvider provider) {
		WikiHooks.registerModWiki(mod, provider);
	}
	
	
}
