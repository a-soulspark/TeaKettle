package soulspark.tea_kettle.core.compat;

import com.teamaurora.abundance.common.item.LavenderTeaItem;
import com.teamaurora.abundance.core.registry.AbundanceEffects;
import com.teamaurora.abundance.core.registry.AbundanceItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.blocks.ExternalTeaBlock;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModItems;

import static soulspark.tea_kettle.core.init.ModBlocks.CUP_PROPERTIES;

public class AbundanceInteropProxy implements InteropProxy {
	@Override
	public void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ExternalLavenderTeaItem((new Item.Properties()).food(AbundanceItems.Foods.EMPTY).maxStackSize(1).group(ItemGroup.FOOD)).setRegistryName(TeaKettle.MODID, "abundance_lavender_tea"));
	}
	
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		ModBlocks.registerExternalBlock(name -> new ExternalTeaBlock(name, CUP_PROPERTIES), new ResourceLocation(TeaKettle.MODID, "abundance_lavender_tea"), event.getRegistry());
	}
	
	public static class ExternalLavenderTeaItem extends LavenderTeaItem {
		public ExternalLavenderTeaItem(Properties properties) {
			super(properties);
		}
		
		public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity livingEntity) {
			super.onItemUseFinish(stack, world, livingEntity);
			if (livingEntity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingEntity;
				CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
				serverplayerentity.addStat(Stats.ITEM_USED.get(this));
			}
			
			if (livingEntity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity)livingEntity;
				player.addPotionEffect(new EffectInstance(AbundanceEffects.SUPPORTIVE.get(), 200, 0, false, true, true));
			}
			
			if (stack.isEmpty()) {
				return new ItemStack(ModItems.CUP.get());
			} else {
				if (livingEntity instanceof PlayerEntity && !((PlayerEntity)livingEntity).abilities.isCreativeMode) {
					ItemStack itemstack = new ItemStack(ModItems.CUP.get());
					PlayerEntity playerentity = (PlayerEntity)livingEntity;
					if (!playerentity.inventory.addItemStackToInventory(itemstack)) {
						playerentity.dropItem(itemstack, false);
					}
				}
				
				return stack;
			}
		}
	}
}
