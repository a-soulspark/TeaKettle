package com.soulspark.kettle.core;

import com.soulspark.kettle.core.init.ModEffects;
import com.soulspark.kettle.core.init.ModFeatures;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEvents {
	@SubscribeEvent
	public static void onBiomeLoad(BiomeLoadingEvent event) {
		if ((event.getName() == null && event.getCategory() == Biome.Category.FOREST) || BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName()), BiomeDictionary.Type.FOREST)) {
			event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.TEA_BUSH);
		}
	}
	
	@SubscribeEvent
	public static void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		BlockState state = world.getBlockState(pos);
		PlayerEntity player = event.getPlayer();
		
		boolean isCaffeinated = player.isPotionActive(ModEffects.CAFFEINE.get());
		boolean isZen = player.isPotionActive(ModEffects.ZEN.get());
		
		if (state.isBed(world, pos, event.getEntityLiving())) {
			if (world.isRemote) event.setCancellationResult(ActionResultType.CONSUME);
			/*
			else if (isZen && !isCaffeinated) {
				event.setCancellationResult(ActionResultType.CONSUME);
				trySleep(state, pos, world, player);
			}*/
			else if (!isZen && isCaffeinated) {
				event.setUseBlock(Event.Result.DENY);
				player.sendStatusMessage(new TranslationTextComponent("block.minecraft.bed.caffeine"), true);
			}
		}
	}
	
	@SubscribeEvent
	static void onSleepTimeCheck(SleepingTimeCheckEvent event) {
		if (event.getEntityLiving().isPotionActive(ModEffects.ZEN.get()) && !event.getEntityLiving().isPotionActive(ModEffects.CAFFEINE.get())) event.setResult(Event.Result.ALLOW);
	}
	
	/*
	static ActionResultType trySleep(BlockState state, BlockPos pos, World worldIn, PlayerEntity player) {
		if (state.get(BedBlock.PART) != BedPart.HEAD) {
			pos = pos.offset(state.get(BedBlock.HORIZONTAL_FACING));
			state = worldIn.getBlockState(pos);
			if (!state.isIn(state.getBlock())) {
				return ActionResultType.CONSUME;
			}
		}
		
		if (!BedBlock.doesBedWork(worldIn)) {
			worldIn.removeBlock(pos, false);
			BlockPos blockpos = pos.offset(state.get(BedBlock.HORIZONTAL_FACING).getOpposite());
			if (worldIn.getBlockState(blockpos).isIn(state.getBlock())) {
				worldIn.removeBlock(blockpos, false);
			}
			
			worldIn.createExplosion(null, DamageSource.func_233546_a_(), null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
			return ActionResultType.SUCCESS;
		} else if (state.get(BedBlock.OCCUPIED)) {
			if (!tryWakeUpVillager(worldIn, pos)) {
				player.sendStatusMessage(new TranslationTextComponent("block.minecraft.bed.occupied"), true);
			}
			
			return ActionResultType.SUCCESS;
		} else {
			player.trySleep(pos);
			return ActionResultType.SUCCESS;
		}
	}
	
	static boolean tryWakeUpVillager(World world, BlockPos pos) {
		List<VillagerEntity> list = world.getEntitiesWithinAABB(VillagerEntity.class, new AxisAlignedBB(pos), LivingEntity::isSleeping);
		if (list.isEmpty()) return false;
		else {
			list.get(0).wakeUp();
			return true;
		}
	}
	 */
}