package soulspark.tea_kettle.core;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import soulspark.tea_kettle.common.blocks.IGrabbable;
import soulspark.tea_kettle.common.items.TeaItem;
import soulspark.tea_kettle.core.init.ModEffects;
import soulspark.tea_kettle.core.init.ModFeatures;

public class CommonEvents {
	@SubscribeEvent
	public static void onBiomeLoad(BiomeLoadingEvent event) {
		if ((event.getName() == null && event.getCategory() == Biome.Category.FOREST) || BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName()), BiomeDictionary.Type.FOREST)) {
			event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.TEA_BUSH);
		}
	}
	
	@SubscribeEvent
	public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
		if (ModList.get().isLoaded("simplytea")) excludeCaffeineEffects(event);
		
		if (TeaItem.sweetnessFactor != 1) {
			event.setResult(Event.Result.DENY);
			
			EffectInstance oldEffect = event.getPotionEffect();
			EffectInstance newEffect = new EffectInstance(oldEffect.getPotion(), (int)(oldEffect.getDuration() * TeaItem.sweetnessFactor), oldEffect.getAmplifier(), oldEffect.isAmbient(), oldEffect.doesShowParticles());
			TeaItem.sweetnessFactor = 1;
			event.getEntityLiving().addPotionEffect(newEffect);
		}
	}
	
	private static void excludeCaffeineEffects(PotionEvent.PotionApplicableEvent event) {
		Effect caffeinated = ForgeRegistries.POTIONS.getValue(new ResourceLocation("simplytea:caffeinated"));
		if (caffeinated != null) {
			if (event.getPotionEffect().getPotion() == caffeinated)
				event.getEntityLiving().removePotionEffect(ModEffects.CAFFEINE.get());
			else if (event.getPotionEffect().getPotion() == ModEffects.CAFFEINE.get())
				event.getEntityLiving().removePotionEffect(caffeinated);
		}
	}
	
	@SubscribeEvent
	public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
		PlayerEntity player = event.getPlayer();
		World world = event.getWorld();
		BlockState state = world.getBlockState(event.getPos());
		
		if (event.getHand() == Hand.MAIN_HAND && player.isSneaking() && state.getBlock() instanceof IGrabbable) {
			ItemStack stack = event.getItemStack();
			IGrabbable grabbable = (IGrabbable) state.getBlock();
			ItemStack grabStack = grabbable.getGrabStack(state, event.getWorld(), event.getPos()).copy();
			
			if (!world.isRemote) {
				if (stack.isEmpty()) player.setHeldItem(Hand.MAIN_HAND, grabStack);
				else if (stack.isItemEqual(grabStack) && stack.getCount() < stack.getMaxStackSize()) stack.grow(1);
				else if (!player.inventory.addItemStackToInventory(grabStack)) return;
			}

			if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
			else player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
			grabbable.grab(state, event.getWorld(), event.getPos());
			
			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.func_233537_a_(world.isRemote));
		}
	}
	
	@SubscribeEvent
	public static void onSleepLocationCheck(SleepingLocationCheckEvent event) {
		LivingEntity entity = event.getEntityLiving();
		PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
		
		if (!entity.isPotionActive(ModEffects.ZEN.get()) && entity.isPotionActive(ModEffects.CAFFEINE.get()) && (player == null || player.getSleepTimer() > 50)) {
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
	public static void onWakeUpEvent(SleepFinishedTimeEvent event) {
		for (PlayerEntity player : event.getWorld().getPlayers()) player.removePotionEffect(ModEffects.ZEN.get());
	}
}