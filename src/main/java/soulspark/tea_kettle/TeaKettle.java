package soulspark.tea_kettle;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soulspark.tea_kettle.common.Config;
import soulspark.tea_kettle.core.ClientEvents;
import soulspark.tea_kettle.core.CommonEvents;
import soulspark.tea_kettle.core.compat.*;
import soulspark.tea_kettle.core.init.ClientInitEvents;
import soulspark.tea_kettle.core.init.CommonInitEvents;
import soulspark.tea_kettle.core.init.RegistryHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TeaKettle.MODID)
public class TeaKettle {
	public static final String MODID = "tea_kettle";
	
	// Directly reference a log4j logger.
	@SuppressWarnings("unused")
	public static final Logger LOGGER = LogManager.getLogger();
	
	public TeaKettle() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "tea_kettle-common.toml");

		IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
		RegistryHandler.register(MOD_BUS);
		
		// registers common events, and client-only events only if in client (obviously)
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> registerClientEvents(MOD_BUS));
		registerCommonEvents(MOD_BUS);

		// register compatibility proxies
		if (ModList.get().isLoaded("simplytea")) CompatHandler.PROXIES.add(new SimplyTeaInteropProxy());
		if (ModList.get().isLoaded("abundance")) CompatHandler.PROXIES.add(new AbundanceInteropProxy());
		if (ModList.get().isLoaded("neapolitan")) CompatHandler.PROXIES.add(new NeapolitanInteropProxy());
		if (ModList.get().isLoaded("endergetic")) CompatHandler.PROXIES.add(new EndergeticInteropProxy());
		if (ModList.get().isLoaded("cookingforblockheads")) CompatHandler.PROXIES.add(new CFBInteropProxy());
//		if (ModList.get().isLoaded("eanimod")) CompatHandler.PROXIES.add(new EAnimInteropProxy()); failed attempt to add Genetic Animals support, may come back to later
		//if (ModList.get().isLoaded("supplementaries")) CompatHandler.PROXIES.add(new SupplementariesInteropProxy()); natively supported, implemented by @MehVahdJukaar
	}
	
	public static void registerCommonEvents(IEventBus modBus) {
		MinecraftForge.EVENT_BUS.register(CommonEvents.class);
		modBus.register(CommonInitEvents.class);
	}
	
	public static void registerClientEvents(IEventBus modBus) {
		MinecraftForge.EVENT_BUS.register(ClientEvents.class);
		modBus.register(ClientInitEvents.class);
	}
}
