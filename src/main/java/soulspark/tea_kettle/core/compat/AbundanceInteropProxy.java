/*
package soulspark.tea_kettle.core.compat;

import com.teamaurora.abundance.common.item.LavenderTeaItem;
import com.teamaurora.abundance.core.registry.AbundanceItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.blocks.ExternalTeaBlock;
import soulspark.tea_kettle.core.init.ModBlocks;

import static soulspark.tea_kettle.core.init.ModBlocks.CUP_PROPERTIES;

public class AbundanceInteropProxy implements InteropProxy {
	@Override
	public void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new LavenderTeaItem((new Item.Properties()).food(AbundanceItems.Foods.EMPTY).maxStackSize(16).group(ItemGroup.FOOD)).setRegistryName(TeaKettle.MODID, "abundance_lavender_tea"));
	}
	
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		ModBlocks.registerExternalBlock(name -> new ExternalTeaBlock(name, CUP_PROPERTIES), new ResourceLocation(TeaKettle.MODID, "abundance_lavender_tea"), event.getRegistry());
	}
}
*/
