package soulspark.tea_kettle.core.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

public class TeaKettleTags {
	public static final ITag<Block> HOT = BlockTags.makeWrapperTag("tea_kettle:hot");
	public static final ITag<Block> FURNACES = BlockTags.makeWrapperTag("tea_kettle:furnaces");
	public static final ITag<Item> KETTLES = ItemTags.makeWrapperTag("tea_kettle:kettles");
	public static final ITag<Item> BOILING_KETTLES = ItemTags.makeWrapperTag("tea_kettle:boiling_kettles");
	public static final ITag<Item> FROTHING_KETTLES = ItemTags.makeWrapperTag("tea_kettle:frothing_kettles");
	public static final ITag<Item> WEAK_SWEENETERS = ItemTags.makeWrapperTag("tea_kettle:weak_sweeteners");
	public static final ITag<Item> STRONG_SWEENETERS = ItemTags.makeWrapperTag("tea_kettle:strong_sweeteners");
	public static final ITag<Item> LAVENDER_TEA = ItemTags.makeWrapperTag("tea_kettle:abundance_lavender_tea");
}
