package soulspark.tea_kettle.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.*;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.util.TeaKettleUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = TeaKettle.MODID)
public class DataGen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		if (event.includeServer()) {
			BlockTagsProvider blockTagsProvider = new BlockTagsProvider(gen) {
				@Override
				protected void registerTags() { }
			};
			
			gen.addProvider(new Recipes(gen));
			gen.addProvider(blockTagsProvider);
			gen.addProvider(new TagsProvider(gen, blockTagsProvider, event.getExistingFileHelper()));
			gen.addProvider(new LootProvider(gen));
		}
	}
	
	public static class Recipes extends RecipeProvider implements IConditionBuilder {
		public Recipes(DataGenerator generatorIn) {
			super(generatorIn);
		}
		
		@Override
		protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
			ConditionalRecipe.builder().addCondition(modLoaded("abundance")).addRecipe(
					ShapelessRecipeBuilder
							.shapelessRecipe(ModItems.BAMBOO_TEA.get())
							.addIngredient(ModItems.BAMBOO_LEAF.get()).addCriterion("has_material", hasItem(ModItems.BAMBOO_LEAF.get()))::build
			).build(consumer, new ResourceLocation(TeaKettle.MODID, "abundance_lavender_tea"));
		}
	}
	
	public static class TagsProvider extends ItemTagsProvider {
		public static final Map<ResourceLocation, ITag.INamedTag<Item>> EXTERNAL_BLOCKS_TAGS = new HashMap<>();
		
		public TagsProvider(DataGenerator generatorIn, BlockTagsProvider blockTagsProvider, ExistingFileHelper helper) {
			super(generatorIn, blockTagsProvider, TeaKettle.MODID, helper);
		}
		
		@Override
		protected void registerTags() {
			ModBlocks.TEA_ITEM_TO_BLOCK.forEach((key, value) -> getOrCreateBuilder(tag(key)).addOptional(key));
		}
		
		private static ITag.INamedTag<Item> tag(ResourceLocation itemName) {
			ITag.INamedTag<Item> wrapperTag = ItemTags.makeWrapperTag(TeaKettleUtils.getExternalBlockName(itemName).toString());
			EXTERNAL_BLOCKS_TAGS.put(itemName, wrapperTag);
			return wrapperTag;
		}
	}
	
	public static class BlockTables extends BlockLootTables {
		@Override
		protected void addTables() {
			ModBlocks.TEA_ITEM_TO_BLOCK.forEach((key, value) -> {
				ITag<Item> tag = TagsProvider.EXTERNAL_BLOCKS_TAGS.get(key);
				registerLootTable(value.getBlock(), block -> LootTable.builder()
						.addLootPool(withSurvivesExplosion(value, LootPool.builder()
								.rolls(ConstantRange.of(1)).addEntry(TagLootEntry.getBuilder(tag)))));
				//registerDropping(, )
			});
		}
		
		@Override
		protected Iterable<Block> getKnownBlocks() {
			return ModBlocks.TEA_ITEM_TO_BLOCK.values().stream().map(BlockItem::getBlock)::iterator;
		}
	}
	
	public static class LootProvider extends LootTableProvider {
		private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> lootTables = ImmutableList.of(Pair.of(BlockTables::new, LootParameterSets.BLOCK));
		
		public LootProvider(DataGenerator dataGeneratorIn) {
			super(dataGeneratorIn);
		}
		
		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
			return lootTables;
		}
		
		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {}
	}
}
