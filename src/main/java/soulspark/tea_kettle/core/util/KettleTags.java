package soulspark.tea_kettle.core.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class KettleTags {
	public static ITag<Block> HOT = BlockTags.makeWrapperTag("tea_kettle:hot");
	
	public static void registerTags() {
		HOT = BlockTags.getCollection().getTagByID(new ResourceLocation("tea_kettle:hot"));
	}
}
