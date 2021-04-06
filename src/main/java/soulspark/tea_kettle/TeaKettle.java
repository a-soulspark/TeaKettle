package soulspark.tea_kettle;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soulspark.tea_kettle.core.ClientEvents;
import soulspark.tea_kettle.core.CommonEvents;
import soulspark.tea_kettle.core.compat.CFBInteropProxy;
import soulspark.tea_kettle.core.compat.CompatHandler;
import soulspark.tea_kettle.core.compat.SimplyTeaInteropProxy;
import soulspark.tea_kettle.core.init.ClientInitEvents;
import soulspark.tea_kettle.core.init.CommonInitEvents;
import soulspark.tea_kettle.core.init.RegistryHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TeaKettle.MODID)
public class TeaKettle {
	public static final String MODID = "tea_kettle";
	
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();
	
	public static IEventBus MOD_BUS;
	
	public TeaKettle() {
		MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
		RegistryHandler.register(MOD_BUS);
		
		// registers common events, and client-only events only if in client (obviously)
		registerCommonEvents();
		DistExecutor.runWhenOn(Dist.CLIENT, () -> TeaKettle::registerClientEvents);
		
		if (ModList.get().isLoaded("simplytea")) CompatHandler.PROXIES.add(new SimplyTeaInteropProxy());
		// TODO: if (ModList.get().isLoaded("abundance")) CompatHandler.PROXIES.add(new AbundanceInteropProxy());
		if (ModList.get().isLoaded("cookingforblockheads")) CompatHandler.PROXIES.add(new CFBInteropProxy());
	}
	
	public static void registerCommonEvents() {
		MinecraftForge.EVENT_BUS.register(CommonEvents.class);
		MOD_BUS.register(CommonInitEvents.class);
	}
	
	public static void registerClientEvents() {
		MinecraftForge.EVENT_BUS.register(ClientEvents.class);
		MOD_BUS.register(ClientInitEvents.class);
	}
}
