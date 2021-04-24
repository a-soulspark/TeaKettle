package soulspark.tea_kettle.core.init;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import soulspark.tea_kettle.TeaKettle;

public class ModParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TeaKettle.MODID);
	
	public static final RegistryObject<BasicParticleType> STEAM = PARTICLES.register("steam", () -> new BasicParticleType(false));
	public static final RegistryObject<BasicParticleType> TEA_STEAM = PARTICLES.register("tea_steam", () -> new BasicParticleType(false));
	public static final RegistryObject<BasicParticleType> WEAK_TEA_STEAM = PARTICLES.register("weak_tea_steam", () -> new BasicParticleType(false));
	public static final RegistryObject<BasicParticleType> STRONG_TEA_STEAM = PARTICLES.register("strong_tea_steam", () -> new BasicParticleType(false));
	
}
