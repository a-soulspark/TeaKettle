package com.soulspark.kettle.core.init;

import com.soulspark.kettle.Kettle;
import com.soulspark.kettle.common.items.CupItem;
import com.soulspark.kettle.common.items.KettleItem;
import com.soulspark.kettle.common.items.TeaItem;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Kettle.MODID);
	
	public static final RegistryObject<KettleItem> KETTLE = ITEMS.register("kettle", () ->
			new KettleItem(ModBlocks.KETTLE.get(), new Item.Properties().group(ItemGroup.DECORATIONS).maxStackSize(1)));
	
	public static final RegistryObject<CupItem> CUP = ITEMS.register("cup", () ->
			new CupItem(ModBlocks.CUP.get(), new Item.Properties().group(ItemGroup.DECORATIONS).maxStackSize(16)));
	
	/* Teas */
	
	public static final RegistryObject<TeaItem> GREEN_TEA = ITEMS.register("green_tea", () ->
			new TeaItem(ModBlocks.GREEN_TEA.get(), new Item.Properties().group(ItemGroup.FOOD).maxStackSize(1).food(new Food.Builder().hunger(4).saturation(0.2f)
					.effect(new EffectInstance(Effects.REGENERATION, 150, 0), 1)
					.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 900, 0), 1).build())));
	
	public static final RegistryObject<TeaItem> WHITE_TEA = ITEMS.register("white_tea", () ->
			new TeaItem(ModBlocks.WHITE_TEA.get(), new Item.Properties().group(ItemGroup.FOOD).maxStackSize(1).food(new Food.Builder().hunger(4).saturation(0.2f)
					.effect(new EffectInstance(Effects.INSTANT_HEALTH, 1), 1)
					.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 300, 0), 1).build())));
	
	public static final RegistryObject<TeaItem> OOLONG_TEA = ITEMS.register("oolong_tea", () ->
			new TeaItem(ModBlocks.OOLONG_TEA.get(), new Item.Properties().group(ItemGroup.FOOD).maxStackSize(1).food(new Food.Builder().hunger(4).saturation(0.3f)
					.effect(new EffectInstance(Effects.ABSORPTION, 400, 0), 1)
					.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 1800, 0), 1).build())));
	
	public static final RegistryObject<TeaItem> BLACK_TEA = ITEMS.register("black_tea", () ->
			new TeaItem(ModBlocks.BLACK_TEA.get(), new Item.Properties().group(ItemGroup.FOOD).maxStackSize(1).food(new Food.Builder().hunger(4).saturation(0.4f)
					.effect(new EffectInstance(Effects.SPEED, 300, 0), 1)
					.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 3600, 0), 1).build())));
	
	public static final RegistryObject<TeaItem> BAMBOO_TEA = ITEMS.register("bamboo_tea", () ->
			new TeaItem(ModBlocks.BAMBOO_TEA.get(), new Item.Properties().group(ItemGroup.FOOD).maxStackSize(1).food(new Food.Builder().hunger(4).saturation(0.3f)
					.effect(new EffectInstance(Effects.RESISTANCE, 200, 0), 1).build())));
	
	// TODO: fix caffeine right click (no anim)
	// TODO: make kettle not empty when in creative
	// TODO: (maybe) add full kettle to creative inventory?
	//  prob not bc it doesn't differ enough from other kettles
	
	public static final RegistryObject<TeaItem> ROSE_TEA = ITEMS.register("rose_tea", () ->
			new TeaItem(ModBlocks.ROSE_TEA.get(), new Item.Properties().group(ItemGroup.FOOD).maxStackSize(1).food(new Food.Builder().hunger(4).saturation(0.2f)
					.effect(() -> new EffectInstance(ModEffects.ZEN.get(), 1800, 0), 1).build())));
	
	/* Tea Ingredients */
	
	public static final RegistryObject<Item> ROSE_PETAL = ITEMS.register("rose_petal", () ->
			new Item(new Item.Properties().group(ItemGroup.MISC)));
	
	public static final RegistryObject<Item> BAMBOO_LEAF = ITEMS.register("bamboo_leaf", () ->
			new Item(new Item.Properties().group(ItemGroup.MISC)));
	
	public static final RegistryObject<BlockNamedItem> TEA_LEAF = ITEMS.register("tea_leaf", () ->
			new BlockNamedItem(ModBlocks.TEA_BUSH.get(), new Item.Properties().group(ItemGroup.MISC)));
	
	public static final RegistryObject<Item> WHITE_TEA_LEAF = ITEMS.register("white_tea_leaf", () ->
			new Item(new Item.Properties().group(ItemGroup.MISC)));
	
	public static final RegistryObject<Item> OOLONG_TEA_LEAF = ITEMS.register("oolong_tea_leaf", () ->
			new Item(new Item.Properties().group(ItemGroup.MISC)));
	
	public static final RegistryObject<Item> BLACK_TEA_LEAF = ITEMS.register("black_tea_leaf", () ->
			new Item(new Item.Properties().group(ItemGroup.MISC)));
}
