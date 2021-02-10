package com.soulspark.kettle.core.init;

import com.soulspark.kettle.Kettle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticles {
	public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Kettle.MODID);
	
	public static final RegistryObject<BasicParticleType> STEAM = PARTICLES.register("steam", () -> new BasicParticleType(true));
}
