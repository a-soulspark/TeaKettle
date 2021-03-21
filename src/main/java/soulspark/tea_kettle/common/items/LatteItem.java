package soulspark.tea_kettle.common.items;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class LatteItem extends TeaItem {
	public LatteItem(Block blockIn, Properties builder) {
		super(blockIn, builder);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if (!worldIn.isRemote) entityLiving.curePotionEffects(new ItemStack(Items.MILK_BUCKET)); // FORGE - move up so stack.shrink does not turn stack into air
		
		return super.onItemUseFinish(stack, worldIn, entityLiving);
	}
}
