package soulspark.tea_kettle.common.blocks;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ExternalTeaBlock extends TeaBlock {
	public final ResourceLocation itemName;
	public Item item;
	
	public ExternalTeaBlock(ResourceLocation itemName, Properties properties) {
		super(properties);
		this.itemName = itemName;
	}
	
	@Override
	public Item asItem() {
		if (item == null) item = ForgeRegistries.ITEMS.getValue(itemName);
		return item;
	}
}
