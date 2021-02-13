package soulspark.tea_kettle.common.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class SteamParticle extends RisingParticle {
	protected SteamParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale, IAnimatedSprite spriteWithAge) {
		super(world, x, y, z, 0.1F, 0.1F, 0.1F, motionX, motionY, motionZ, scale, spriteWithAge, 1, 8, 0.004D, true);
		float f = world.rand.nextFloat() * 0.2f + 0.8f;
		particleRed = particleGreen = particleBlue = f;
	}
	
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;
		
		public Factory(IAnimatedSprite sprite) {
			spriteSet = sprite;
		}
		
		@Nullable
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new SteamParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 1.0f, spriteSet);
		}
	}
}
