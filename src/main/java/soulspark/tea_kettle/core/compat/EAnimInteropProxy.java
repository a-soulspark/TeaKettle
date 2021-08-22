/*
package soulspark.tea_kettle.core.compat;

import mokiyoki.enhancedanimals.entity.EnhancedAnimalAbstract;
import mokiyoki.enhancedanimals.entity.EnhancedCow;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import soulspark.tea_kettle.core.init.ModItems;

public class EAnimInteropProxy implements InteropProxy {
    private static DataParameter<Float> BAG_SIZE;

    @Override
    public void registerItems(RegistryEvent.Register<Item> event) {
        try {
            Object bagSize = EnhancedAnimalAbstract.class.getDeclaredField("BAG_SIZE").get(null);
            if (bagSize instanceof DataParameter<?>) BAG_SIZE = (DataParameter<Float>) bagSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ActionResultType onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getItemStack().getItem() == ModItems.EMPTY_KETTLE.get() && event.getTarget() instanceof EnhancedCow) {
            EnhancedCow cow = (EnhancedCow) event.getTarget();
            if (cow.decreaseMilk(6)) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.func_233537_a_(event.getWorld().isRemote));
//              float milkBagSize = (float)cow.getMilkAmount() / (30.0F * (cow.getAnimalSize() / 1.5F) * (cow.getBagSize() / 1.5F));
//              this.setBagSize(1.1F * milkBagSize * (this.maxBagSize - 1.0F) + 1.0F);
//              cow.setMilkAmount(0);
//              cow.setBagSize(0);
            }
        }

        return ActionResultType.PASS;
    }
}
*/
