package soulspark.tea_kettle.core.init;

import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.blocks.CampfireKettleBlock.Content;
import soulspark.tea_kettle.common.blocks.TeaBlock;
import soulspark.tea_kettle.common.items.*;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TeaKettle.MODID);
	
	private static final Item.Properties KETTLE_PROPERTIES = new Item.Properties().maxStackSize(1);
	public static final ArrayList<RegistryObject<TeaItem>> TEAS = new ArrayList<>();
	
	public static final RegistryObject<LegacyKettleItem> LEGACY_KETTLE = ITEMS.register("kettle", () -> new LegacyKettleItem(KETTLE_PROPERTIES));
	
	public static final RegistryObject<EmptyKettleItem> EMPTY_KETTLE = ITEMS.register("empty_kettle", () ->
			new EmptyKettleItem(ModBlocks.EMPTY_KETTLE.get(), KETTLE_PROPERTIES.group(ItemGroup.DECORATIONS).maxStackSize(16)));
	
	public static final RegistryObject<FilledKettleItem> WATER_KETTLE = ITEMS.register("water_kettle", () ->
			new FilledKettleItem(ModBlocks.WATER_KETTLE.get(), false, Content.WATER, KETTLE_PROPERTIES.group(ItemGroup.SEARCH).maxStackSize(1)));
	
	public static final RegistryObject<FilledKettleItem> BOILING_KETTLE = ITEMS.register("boiling_kettle", () ->
			new FilledKettleItem(ModBlocks.WATER_KETTLE.get(), true, Content.WATER, KETTLE_PROPERTIES.group(ItemGroup.DECORATIONS).containerItem(EMPTY_KETTLE.get())));
	
	public static final RegistryObject<FilledKettleItem> MILK_KETTLE = ITEMS.register("milk_kettle", () ->
			new FilledKettleItem(ModBlocks.MILK_KETTLE.get(), false, Content.MILK, KETTLE_PROPERTIES.group(ItemGroup.SEARCH)));
	
	public static final RegistryObject<FilledKettleItem> FROTHING_KETTLE = ITEMS.register("frothing_kettle", () ->
			new FilledKettleItem(ModBlocks.MILK_KETTLE.get(), true, Content.MILK, KETTLE_PROPERTIES.group(ItemGroup.DECORATIONS).containerItem(EMPTY_KETTLE.get())));
	
	public static final RegistryObject<CupItem> CUP = ITEMS.register("cup", () ->
			new CupItem(ModBlocks.CUP.get(), new Item.Properties().group(ItemGroup.DECORATIONS).maxStackSize(16)));
	
	/* Teas */
	
	public static final RegistryObject<TeaItem> GREEN_TEA = registerTea("green_tea", ModBlocks.GREEN_TEA, new Food.Builder()
			.effect(new EffectInstance(Effects.REGENERATION, 120, 0), 1).saturation(0.1f)
			.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 900, 0), 1));
	
	public static final RegistryObject<TeaItem> WHITE_TEA = registerTea("white_tea", ModBlocks.WHITE_TEA, new Food.Builder()
			.effect(new EffectInstance(Effects.REGENERATION, 140, 0), 1).saturation(0.3f)
			.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 120, 0), 1));
	
	public static final RegistryObject<TeaItem> OOLONG_TEA = registerTea("oolong_tea", ModBlocks.OOLONG_TEA, new Food.Builder()
			.effect(new EffectInstance(Effects.ABSORPTION, 240, 0), 1).saturation(0.1f)
			.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 1200, 0), 1));
	
	public static final RegistryObject<TeaItem> BLACK_TEA = registerTea("black_tea", ModBlocks.BLACK_TEA, new Food.Builder()
			.effect(new EffectInstance(Effects.SPEED, 160, 0), 1).saturation(0.2f)
			.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 1800, 0), 1));
	
	public static final RegistryObject<TeaItem> BAMBOO_TEA = registerTea("bamboo_tea", ModBlocks.BAMBOO_TEA, new Food.Builder()
			.effect(new EffectInstance(Effects.RESISTANCE, 240, 0), 1).saturation(0.3f));
	
	public static final RegistryObject<TeaItem> ROSE_TEA = registerTea("rose_tea", ModBlocks.ROSE_TEA, new Food.Builder()
			.effect(() -> new EffectInstance(ModEffects.ZEN.get(), 300, 0), 1).saturation(0.2f));
	
	/* Lattes */
	
	public static final RegistryObject<TeaItem> GREEN_LATTE = registerLatte("green_latte", ModBlocks.GREEN_LATTE, new Food.Builder()
			.effect(new EffectInstance(Effects.REGENERATION, 100, 1), 1).saturation(0.3f)
			.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 600, 0), 1));
	
	public static final RegistryObject<TeaItem> WHITE_LATTE = registerLatte("white_latte", ModBlocks.WHITE_LATTE, new Food.Builder()
			.effect(new EffectInstance(Effects.REGENERATION, 120, 1), 1).saturation(0.5f)
			.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 80, 0), 1));
	
	public static final RegistryObject<TeaItem> OOLONG_LATTE = registerLatte("oolong_latte", ModBlocks.OOLONG_LATTE, new Food.Builder()
			.effect(new EffectInstance(Effects.ABSORPTION, 220, 1), 1).saturation(0.1f)
			.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 800, 0), 1));
	
	public static final RegistryObject<TeaItem> BLACK_LATTE = registerLatte("black_latte", ModBlocks.BLACK_LATTE, new Food.Builder()
			.effect(new EffectInstance(Effects.SPEED, 160, 0), 1).saturation(0.4f)
			.effect(() -> new EffectInstance(ModEffects.CAFFEINE.get(), 1200, 1), 1));
	
	public static RegistryObject<TeaItem> registerTea(String name, RegistryObject<TeaBlock> block, Food.Builder foodBuilder) {
		RegistryObject<TeaItem> entry = ITEMS.register(name, () -> new TeaItem(block.get(), new Item.Properties().group(ItemGroup.FOOD).maxStackSize(1).food(foodBuilder.hunger(3).build())));
		TEAS.add(entry);
		return entry;
	}
	
	public static RegistryObject<TeaItem> registerLatte(String name, RegistryObject<TeaBlock> block, Food.Builder foodBuilder) {
		RegistryObject<TeaItem> entry = ITEMS.register(name, () -> new LatteItem(block.get(), new Item.Properties().group(ItemGroup.FOOD).maxStackSize(1).food(foodBuilder.hunger(4).build())));
		TEAS.add(entry);
		return entry;
	}
	
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
	
	public static final RegistryObject<HoneyBottleItem> HALF_HONEY_BOTTLE = ITEMS.register("half_honey_bottle", () -> new HoneyBottleItem(new Item.Properties().group(ItemGroup.FOOD).maxStackSize(16).food(new Food.Builder()
			.hunger(3).saturation(0.1f).build()).containerItem(Items.GLASS_BOTTLE)));
	
}
