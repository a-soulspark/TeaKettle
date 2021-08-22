package soulspark.tea_kettle.core.init;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.blocks.*;
import soulspark.tea_kettle.core.util.TeaKettleUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = TeaKettle.MODID)
public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TeaKettle.MODID);
	public static final ArrayList<CupBlock> CUPS = new ArrayList<>();
	
	public static final Map<ResourceLocation, BlockItem> TEA_ITEM_TO_BLOCK = new HashMap<>();
	public static final Map<Block, Block> CAMPFIRE_KETTLES = new HashMap<>();
	
	public static final AbstractBlock.Properties KETTLE_PROPERTIES = AbstractBlock.Properties.create(Material.IRON, MaterialColor.BLUE).hardnessAndResistance(0.5f).sound(SoundType.METAL).notSolid();
	public static final AbstractBlock.Properties CUP_PROPERTIES = AbstractBlock.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.0f).sound(SoundType.STONE).notSolid();
	
	public static final RegistryObject<TeaBushBlock> TEA_BUSH = BLOCKS.register("tea_bush", () ->
			new TeaBushBlock(AbstractBlock.Properties.create(Material.PLANTS).tickRandomly().doesNotBlockMovement().sound(SoundType.SWEET_BERRY_BUSH).notSolid()));
	
	public static final RegistryObject<LegacyKettleBlock> LEGACY_KETTLE = BLOCKS.register("kettle", () ->
			new LegacyKettleBlock(KETTLE_PROPERTIES));
	
	public static final RegistryObject<KettleBlock> EMPTY_KETTLE = BLOCKS.register("empty_kettle", () ->
			new EmptyKettleBlock(KETTLE_PROPERTIES));
	
	public static final RegistryObject<FilledKettleBlock> WATER_KETTLE = BLOCKS.register("water_kettle", () ->
			new FilledKettleBlock(state -> state.get(FilledKettleBlock.HOT) ? ModItems.BOILING_KETTLE.get() : ModItems.WATER_KETTLE.get(), KETTLE_PROPERTIES));
	
	public static final RegistryObject<FilledKettleBlock> MILK_KETTLE = BLOCKS.register("milk_kettle", () ->
			new FilledKettleBlock(state -> state.get(FilledKettleBlock.HOT) ? ModItems.FROTHING_KETTLE.get() : ModItems.MILK_KETTLE.get(), KETTLE_PROPERTIES));
	
	public static final RegistryObject<FilledKettleBlock> CAMPFIRE_AND_KETTLE = BLOCKS.register("campfire_and_kettle", () ->
			new CampfireKettleBlock(ModBlocks::getCampfireAndKettleItem, () -> Blocks.CAMPFIRE, AbstractBlock.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD).setLightLevel(state -> 13).notSolid()));
	
	public static final RegistryObject<FilledKettleBlock> SOUL_CAMPFIRE_AND_KETTLE = BLOCKS.register("soul_campfire_and_kettle", () ->
			new CampfireKettleBlock(ModBlocks::getCampfireAndKettleItem, () -> Blocks.SOUL_CAMPFIRE, AbstractBlock.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD).setLightLevel(state -> 8).notSolid()));
	
	public static final RegistryObject<CupBlock> CUP = BLOCKS.register("cup", () ->
			new CupBlock(CUP_PROPERTIES));
	
	/* Teas */
	public static final RegistryObject<TeaBlock> GREEN_TEA = registerTea("green_tea");
	public static final RegistryObject<TeaBlock> WHITE_TEA = registerTea("white_tea");
	public static final RegistryObject<TeaBlock> OOLONG_TEA = registerTea("oolong_tea");
	public static final RegistryObject<TeaBlock> BLACK_TEA = registerTea("black_tea");
	public static final RegistryObject<TeaBlock> ROSE_TEA = registerTea("rose_tea");
	public static final RegistryObject<TeaBlock> BAMBOO_TEA = registerTea("bamboo_tea");
	/* Lattes */
	public static final RegistryObject<TeaBlock> GREEN_LATTE = registerTea("green_latte");
	public static final RegistryObject<TeaBlock> WHITE_LATTE = registerTea("white_latte");
	public static final RegistryObject<TeaBlock> OOLONG_LATTE = registerTea("oolong_latte");
	public static final RegistryObject<TeaBlock> BLACK_LATTE = registerTea("black_latte");
	
	private static RegistryObject<TeaBlock> registerTea(String name) { return BLOCKS.register(name, () -> new TeaBlock(CUP_PROPERTIES)); }
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		CAMPFIRE_KETTLES.put(Blocks.CAMPFIRE, CAMPFIRE_AND_KETTLE.get());
		CAMPFIRE_KETTLES.put(Blocks.SOUL_CAMPFIRE, SOUL_CAMPFIRE_AND_KETTLE.get());
	}
	
	public static void registerSimplyTea(ResourceLocation name, IForgeRegistry<Block> registry) {
		registerExternalBlock(nameIn -> new SimplyTeaBlock(name, CUP_PROPERTIES), name, registry);
	}
	
	public static void registerExternalBlock(Function<ResourceLocation, Block> constructor, ResourceLocation itemName, IForgeRegistry<Block> registry) {
		ResourceLocation blockName = TeaKettleUtils.getExternalBlockName(itemName);
		Block block = constructor.apply(itemName).setRegistryName(blockName);
		
		registry.register(block);
		TEA_ITEM_TO_BLOCK.put(itemName, new BlockItem(block, new Item.Properties()));
	}
	
	public static Item getCampfireAndKettleItem(BlockState state) {
		if (state.get(CampfireKettleBlock.CONTENT) == CampfireKettleBlock.Content.WATER)
			return state.get(FilledKettleBlock.HOT) ? ModItems.BOILING_KETTLE.get() : ModItems.WATER_KETTLE.get();
		return state.get(FilledKettleBlock.HOT) ? ModItems.FROTHING_KETTLE.get() : ModItems.MILK_KETTLE.get();
	}
}
