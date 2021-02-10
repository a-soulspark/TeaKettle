package com.soulspark.kettle.core.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class KettleTags {
	public static ITag<Block> HOT = BlockTags.makeWrapperTag("kettle:hot");
	public static ITag<Item> TEA_INGREDIENTS = ItemTags.makeWrapperTag("kettle:tea_ingredients");
	
	public static void registerTags() {
		HOT = BlockTags.getCollection().getTagByID(new ResourceLocation("kettle:hot"));
		TEA_INGREDIENTS = ItemTags.getCollection().getTagByID(new ResourceLocation("kettle:tea_ingredients"));
	}
}
