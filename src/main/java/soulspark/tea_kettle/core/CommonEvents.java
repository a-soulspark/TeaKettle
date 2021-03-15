package soulspark.tea_kettle.core;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.core.init.ModEffects;
import soulspark.tea_kettle.core.init.ModFeatures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import soulspark.tea_kettle.core.init.ModItems;

public class CommonEvents {
	@SubscribeEvent
	public static void onBiomeLoad(BiomeLoadingEvent event) {
		if ((event.getName() == null && event.getCategory() == Biome.Category.FOREST) || BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName()), BiomeDictionary.Type.FOREST)) {
			event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.TEA_BUSH);
		}
	}
	
	@SubscribeEvent
	public static void onMissingMappings(RegistryEvent.MissingMappings<Item> event) {
		TeaKettle.LOGGER.info("UNIQUE THINGYYYY {}", event.getName());
		for (RegistryEvent.MissingMappings.Mapping<Item> item : event.getAllMappings()) {
			if (item != null && item.key.equals(new ResourceLocation(TeaKettle.MODID, "kettle"))) {
				TeaKettle.LOGGER.info("kajs {}", event.getName());
				item.remap(ModItems.EMPTY_KETTLE.get());
			}
		}
	}
	
	@SubscribeEvent
	public static void onSleepLocationCheck(SleepingLocationCheckEvent event) {
		LivingEntity entity = event.getEntityLiving();
		PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
		
		if (!entity.isPotionActive(ModEffects.ZEN.get()) && entity.isPotionActive(ModEffects.CAFFEINE.get()) && (player == null || player.getSleepTimer() > 60)) {
			event.setResult(Event.Result.DENY);
			if (player != null) player.sendStatusMessage(new TranslationTextComponent("block.minecraft.bed.caffeine"), true);
		}
	}
	
	@SubscribeEvent
	public static void onSleepTimeCheck(SleepingTimeCheckEvent event) {
		boolean zen = event.getEntityLiving().isPotionActive(ModEffects.ZEN.get());
		boolean caffeine = event.getEntityLiving().isPotionActive(ModEffects.CAFFEINE.get());
		
		if (zen && !caffeine) event.setResult(Event.Result.ALLOW);
	}
	
	@SubscribeEvent
	public static void onWakeUpEvent(PlayerWakeUpEvent event) {
		if (event.getPlayer().isPotionActive(ModEffects.ZEN.get())) event.getPlayer().removePotionEffect(ModEffects.ZEN.get());
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