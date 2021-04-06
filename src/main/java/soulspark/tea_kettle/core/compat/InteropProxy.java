package soulspark.tea_kettle.core.compat;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface InteropProxy {
	default void registerItems(RegistryEvent.Register<Item> event) {}
	default void registerBlocks(RegistryEvent.Register<Block> event) {}
	default ActionResultType onRightClickBlock(PlayerInteractEvent.RightClickBlock event) { return ActionResultType.PASS; }
}