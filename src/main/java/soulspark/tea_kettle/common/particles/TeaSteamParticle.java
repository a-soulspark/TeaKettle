package soulspark.tea_kettle.common.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class TeaSteamParticle extends RisingParticle {
	protected TeaSteamParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, float r, float g, float b, float scale, IAnimatedSprite spriteWithAge) {
		super(world, x, y, z, 0.1F, 0.1F, 0.1F, motionX, motionY, motionZ, scale, spriteWithAge, 1, 8, 0.004D, true);
		float f = world.rand.nextFloat() * 0.3f + 0.7f;
		particleRed = Math.min(1, r * f);
		particleGreen = Math.min(1, g * f);
		particleBlue = Math.min(1, b * f);
		particleAlpha = Math.min(0.9f, scale);
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public void tick() {
		super.tick();
		if (this.age < this.maxAge) {
			this.motionX *= 0.85F;
			this.motionZ *= 0.85F;
		}
	}
	
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;
		private final float r, g, b, scale;
		
		public Factory(IAnimatedSprite sprite, float r, float g, float b, float scale) {
			spriteSet = sprite;
			this.r = r;
			this.g = g;
			this.b = b;
			this.scale = scale;
		}
		
		@Nullable
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new TeaSteamParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, r, g, b, scale, spriteSet);
		}
	}
}
