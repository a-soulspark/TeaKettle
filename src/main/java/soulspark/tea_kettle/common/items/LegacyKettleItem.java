package soulspark.tea_kettle.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import soulspark.tea_kettle.core.init.ModItems;

public class LegacyKettleItem extends Item {
	public LegacyKettleItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		ItemStack convertedStack = new ItemStack(ModItems.EMPTY_KETTLE.get());
		
		if (stack.hasTag())
		{
			String content = stack.getOrCreateChildTag("BlockStateTag").getString("content");
			if (content.equals("water")) convertedStack = new ItemStack(ModItems.WATER_KETTLE.get());
			else if (content.equals("hot_water")) {
				convertedStack = new ItemStack(ModItems.BOILING_KETTLE.get());
				
				CompoundNBT tag = convertedStack.getOrCreateTag();
				CompoundNBT blockStateTag = new CompoundNBT();
				blockStateTag.putInt("fullness", Math.max(0, stack.getOrCreateChildTag("BlockStateTag").getInt("fullness")));
				tag.put("BlockStateTag", blockStateTag);
				convertedStack.setTag(tag);
			}
		}
		
		entityIn.replaceItemInInventory(itemSlot, convertedStack);
	}
}
