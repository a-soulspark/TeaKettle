package soulspark.tea_kettle.core.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

public class TeaKettleTags {
	public static ITag<Block> HOT = BlockTags.makeWrapperTag("tea_kettle:hot");
	public static ITag<Block> FURNACES = BlockTags.makeWrapperTag("tea_kettle:furnaces");
	public static ITag<Item> BOILING_KETTLES = ItemTags.makeWrapperTag("tea_kettle:boiling_kettles");
	public static ITag<Item> FROTHING_KETTLES = ItemTags.makeWrapperTag("tea_kettle:frothing_kettles");
	public static ITag<Item> WEAK_SWEENETERS = ItemTags.makeWrapperTag("tea_kettle:weak_sweeteners");
	public static ITag<Item> STRONG_SWEENETERS = ItemTags.makeWrapperTag("tea_kettle:strong_sweeteners");
}
