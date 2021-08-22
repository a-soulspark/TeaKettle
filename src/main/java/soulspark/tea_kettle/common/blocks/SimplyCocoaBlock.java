package soulspark.tea_kettle.common.blocks;

import net.minecraft.util.ResourceLocation;

public class SimplyCocoaBlock extends SimplyTeaBlock {
	public SimplyCocoaBlock(ResourceLocation itemName, Properties properties) {
		super(itemName, properties);
	}
	
	@Override
	protected String getExtraTag() {
		return "with_cinnamon";
	}
}
