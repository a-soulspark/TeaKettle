package soulspark.tea_kettle.core.init;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.particles.SteamParticle;
import soulspark.tea_kettle.common.particles.TeaSteamParticle;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = TeaKettle.MODID)
public class ModParticleFactories {
	@SubscribeEvent
	public static void registerFactories(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particles.registerFactory(ModParticles.STEAM.get(), SteamParticle.Factory::new);
		Minecraft.getInstance().particles.registerFactory(ModParticles.TEA_STEAM.get(), (sprite) -> new TeaSteamParticle.Factory(sprite, 0.7f, 0.7f, 0.7f, 0.5f));
		Minecraft.getInstance().particles.registerFactory(ModParticles.WEAK_TEA_STEAM.get(), (sprite) -> new TeaSteamParticle.Factory(sprite, 1.1f, 1.1f, 1.1f, 0.9f));
		Minecraft.getInstance().particles.registerFactory(ModParticles.STRONG_TEA_STEAM.get(), (sprite) -> new TeaSteamParticle.Factory(sprite, 1, 0.75f, 0.6f, 1.0f));
	}
}
