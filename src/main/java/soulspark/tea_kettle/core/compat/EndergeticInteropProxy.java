package soulspark.tea_kettle.core.compat;

import com.minecraftabnormals.endergetic.core.registry.EEBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.blocks.CampfireKettleBlock;
import soulspark.tea_kettle.core.init.ModBlocks;

public class EndergeticInteropProxy implements InteropProxy {
	private CampfireKettleBlock ENDER_CAMPFIRE;
	
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		ENDER_CAMPFIRE = new CampfireKettleBlock(ModBlocks::getCampfireAndKettleItem, EEBlocks.ENDER_CAMPFIRE, AbstractBlock.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD).setLightLevel(state -> 11).notSolid());
		event.getRegistry().register(ENDER_CAMPFIRE.setRegistryName(TeaKettle.MODID, "ender_campfire_and_kettle"));
	}
	
	@Override
	public void registerItems(RegistryEvent.Register<Item> event) {
		ModBlocks.CAMPFIRE_KETTLES.put(EEBlocks.ENDER_CAMPFIRE.get(), ENDER_CAMPFIRE);
	}
}
