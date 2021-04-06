package soulspark.tea_kettle.core.compat;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import soulspark.tea_kettle.TeaKettle;

import java.util.HashSet;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = TeaKettle.MODID)
public class CompatHandler {
	public static HashSet<InteropProxy> PROXIES = new HashSet<>();
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		PROXIES.forEach(proxy -> proxy.registerItems(event));
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		PROXIES.forEach(proxy -> proxy.registerBlocks(event));
	}
	
	public static ActionResultType onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		for (InteropProxy proxy : PROXIES) {
			ActionResultType result = proxy.onRightClickBlock(event);
			if (result != ActionResultType.PASS) return result;
		}
		return ActionResultType.PASS;
	}
}
