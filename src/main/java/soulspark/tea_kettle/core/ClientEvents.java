package soulspark.tea_kettle.core;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.util.TeaKettleUtils;

public class ClientEvents {
	@SubscribeEvent
	public static void onItemTooltips(ItemTooltipEvent event) {
		if (ModBlocks.TEA_ITEM_TO_BLOCK.containsKey(event.getItemStack().getItem().getRegistryName())) {
			switch ((int) (TeaKettleUtils.getSweetness(event.getItemStack()) * 2)) {
				case 1:
					event.getToolTip().add(new TranslationTextComponent("tea_kettle.tea_sweetened_weak").modifyStyle(style -> style.forceFormatting(TextFormatting.GRAY)));
					break;
				case 2:
					event.getToolTip().add(new TranslationTextComponent("tea_kettle.tea_sweetened_strong").modifyStyle(style -> style.forceFormatting(TextFormatting.GRAY)));
			}
		}
	}
}
