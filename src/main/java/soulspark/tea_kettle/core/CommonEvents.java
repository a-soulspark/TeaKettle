package soulspark.tea_kettle.core;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
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
import soulspark.tea_kettle.core.compat.CompatHandler;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModEffects;
import soulspark.tea_kettle.core.init.ModFeatures;
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.util.TeaKettleUtils;

import static soulspark.tea_kettle.core.util.TeaKettleTags.LAVENDER_TEA;

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
		
		double sweetnessFactor = event.getEntityLiving().getPersistentData().getDouble("TeaKettleSweetnessFactor");
		if (sweetnessFactor != 0) {
			EffectInstance oldEffect = event.getPotionEffect();
			EffectInstance newEffect = new EffectInstance(oldEffect.getPotion(), (int) (oldEffect.getDuration() * sweetnessFactor), oldEffect.getAmplifier(), oldEffect.isAmbient(), oldEffect.doesShowParticles());
			event.getEntityLiving().getPersistentData().remove("TeaKettleSweetnessFactor");
			event.getEntityLiving().addPotionEffect(newEffect);
			event.setResult(Event.Result.DENY);
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
	
	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		ItemStack handStack = event.getItemStack();
		Item item = handStack.getItem();
		PlayerEntity player = event.getPlayer();
		BlockState state = world.getBlockState(event.getPos());
		
		// grabbing milk from CfB's milk jars
		if (CompatHandler.onRightClickBlock(event) != ActionResultType.PASS) return;
		// grabbing blocks (like cups and kettles) while sneaking
		if (event.getHand() == Hand.MAIN_HAND && player.isSneaking() && state.getBlock() instanceof IGrabbable) {
			IGrabbable grabbable = (IGrabbable) state.getBlock();
			ItemStack grabStack = grabbable.getGrabStack(state, event.getWorld(), event.getPos()).copy();
			
			if (handStack.isEmpty()) player.setHeldItem(Hand.MAIN_HAND, grabStack);
			else if (handStack.isItemEqual(grabStack) && handStack.getCount() < handStack.getMaxStackSize()) handStack.grow(1);
			else if (!player.inventory.addItemStackToInventory(grabStack)) return;
			
			if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
			else player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
			grabbable.grab(event.getWorld(), event.getPos());
			
			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.func_233537_a_(world.isRemote));
		}
		// filling up the empty kettle with water from a cauldron
		else if (handStack.getItem() == ModItems.EMPTY_KETTLE.get() && state.getBlock() == Blocks.CAULDRON && state.get(CauldronBlock.LEVEL) == 3) {
			player.setHeldItem(event.getHand(), DrinkHelper.fill(handStack.copy(), player, new ItemStack(ModItems.WATER_KETTLE.get())));
			world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			((CauldronBlock) Blocks.CAULDRON).setWaterLevel(world, event.getPos(), state, 0);
			
			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.func_233537_a_(world.isRemote));
		}
		else if (player.isSneaking() && event.getFace() != null) {
			BlockItem blockItem = ModBlocks.TEA_ITEM_TO_BLOCK.get(item.getRegistryName());
			if (blockItem != null) {
				ItemStack blockItemStack = new ItemStack(blockItem);
				if (handStack.hasTag()) blockItemStack.setTag(handStack.getTag().copy());
				
				ActionResultType result = blockItem.tryPlace(new BlockItemUseContext(player, event.getHand(), blockItemStack,
						new BlockRayTraceResult(player.getLookVec(), event.getFace(), event.getPos(), false)));
				
				if (result.isSuccessOrConsume()) {
					if (!player.abilities.isCreativeMode) handStack.shrink(1);
					event.setCanceled(true);
					event.setCancellationResult(ActionResultType.SUCCESS);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onItemUsed(LivingEntityUseItemEvent.Tick event) {
		ItemStack handStack = event.getItem();
		if (!event.getEntityLiving().world.isRemote && event.getDuration() == 1 && ModBlocks.TEA_ITEM_TO_BLOCK.containsKey(handStack.getItem().getRegistryName())) {
			Food food = handStack.getItem().getFood();
			double sweetnessFactor;
			// only lengthen if the food has effects
			if (food != null && food.getEffects().size() > 0) sweetnessFactor = 0.75 + 0.25 * Math.pow(TeaKettleUtils.getSweetness(handStack) * 2, 1.6);
			else if (handStack.getItem().isIn(LAVENDER_TEA)) sweetnessFactor = 1 + Math.max(0, TeaKettleUtils.getSweetness(handStack) * 2 - 0.5);
			else return;
			
			event.getEntityLiving().getPersistentData().putDouble("TeaKettleSweetnessFactor", sweetnessFactor);
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