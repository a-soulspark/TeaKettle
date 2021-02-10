package com.soulspark.kettle;

import com.soulspark.kettle.core.ClientEvents;
import com.soulspark.kettle.core.CommonEvents;
import com.soulspark.kettle.core.init.ClientInitEvents;
import com.soulspark.kettle.core.init.CommonInitEvents;
import com.soulspark.kettle.core.init.RegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Kettle.MODID)
public class Kettle {
	public static final String MODID = "kettle";
	
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();
	
	public static IEventBus MOD_BUS;
	
	public Kettle() {
		MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
		RegistryHandler.register(MOD_BUS);
		
		// registers common events, and client-only events only if in client (obviously)
		registerCommonEvents();
		DistExecutor.runWhenOn(Dist.CLIENT, () -> Kettle::registerClientEvents);
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
