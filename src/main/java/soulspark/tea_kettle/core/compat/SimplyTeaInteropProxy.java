package soulspark.tea_kettle.core.compat;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModList;
import soulspark.tea_kettle.common.blocks.SimplyCocoaBlock;
import soulspark.tea_kettle.common.blocks.SimplyCupBlock;

import static soulspark.tea_kettle.core.init.ModBlocks.*;

public class SimplyTeaInteropProxy implements InteropProxy {
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		registerSimplyTea(new ResourceLocation("simplytea", "cup_tea_green"), event.getRegistry());
		registerSimplyTea(new ResourceLocation("simplytea", "cup_tea_black"), event.getRegistry());
		registerSimplyTea(new ResourceLocation("simplytea", "cup_tea_chorus"), event.getRegistry());
		registerSimplyTea(new ResourceLocation("simplytea", "cup_tea_floral"), event.getRegistry());
		registerSimplyTea(new ResourceLocation("simplytea", "cup_tea_chai"), event.getRegistry());
		registerSimplyTea(new ResourceLocation("simplytea", "cup_tea_iced"), event.getRegistry());
//		registerSimplyTea(new ResourceLocation("simplytea", "cup_cocoa"), event.getRegistry());
		registerExternalBlock(nameIn -> new SimplyCocoaBlock(nameIn, CUP_PROPERTIES), new ResourceLocation("simplytea", "cup_cocoa"), event.getRegistry());
		
		if (ModList.get().isLoaded("terraincognita")) registerSimplyTea(new ResourceLocation("terraincognita", "fireweed_tea_cup"), event.getRegistry());
		
		registerExternalBlock(name -> new SimplyCupBlock(name, CUP_PROPERTIES), new ResourceLocation("simplytea", "cup"), event.getRegistry());
	}
}
