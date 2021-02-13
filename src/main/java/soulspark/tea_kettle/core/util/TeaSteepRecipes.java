package soulspark.tea_kettle.core.util;

import com.mojang.datafixers.util.Pair;
import soulspark.tea_kettle.common.blocks.TeaBlock;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModItems;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class TeaSteepRecipes {
	public static final Map<Item, TeaBlock> RECIPES = new HashMap<>();
	
	public static void registerRecipes() {
		RECIPES.put(ModItems.TEA_LEAF.get(), ModBlocks.GREEN_TEA.get());
		RECIPES.put(ModItems.WHITE_TEA_LEAF.get(), ModBlocks.WHITE_TEA.get());
		RECIPES.put(ModItems.OOLONG_TEA_LEAF.get(), ModBlocks.OOLONG_TEA.get());
		RECIPES.put(ModItems.BLACK_TEA_LEAF.get(), ModBlocks.BLACK_TEA.get());
		RECIPES.put(ModItems.BAMBOO_LEAF.get(), ModBlocks.BAMBOO_TEA.get());
		RECIPES.put(ModItems.ROSE_PETAL.get(), ModBlocks.ROSE_TEA.get());
	}
	
	public static Pair<Item, TeaBlock> getRecipe(Item ingredient) {
		return new Pair<>(ingredient, RECIPES.get(ingredient));
	}
}
