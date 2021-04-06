package soulspark.tea_kettle.core.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import soulspark.tea_kettle.TeaKettle;

public class CupItemHandler extends ItemStackHandler {
	private final Notify onDirtyCallback;
	
	public CupItemHandler(int size, Notify onDirty) {
		super(size);
		onDirtyCallback = onDirty;
	}
	/*
	so.
			add a "getTopStack()" method for easily accessing the most recent stack
			then use that for getGrabStack
	and that kinda stuff
			make big size
	and then improve the JEI Addon thingy
	gn*/
	
	public ItemStack getLastStack() {
		for (int i = stacks.size() - 1; i >= 0; i--) {
			ItemStack stack = getStackInSlot(i);
			if (!stack.isEmpty()) return stack;
		}
		return ItemStack.EMPTY;
	}
	
	public void setLastStack(ItemStack stack) {
		TeaKettle.LOGGER.info(stack);
		for (int i = stacks.size() - 1; i >= 0; i--) {
			if (!getStackInSlot(i).isEmpty()) {
				setStackInSlot(i, stack);
				break;
			}
		}
	}
	
	public boolean addStack(ItemStack stack) {
		for (int i = 0; i < stacks.size(); i++) {
			if (getStackInSlot(i).isEmpty()) {
				setStackInSlot(i, stack);
				return true;
			}
		}
		return false;
	}
	
	public boolean isEmpty() {
		return getStackInSlot(0).isEmpty();
	}
	
	public boolean isFull() {
		return !getStackInSlot(stacks.size() - 1).isEmpty();
	}
	
	public NonNullList<ItemStack> getStacks() {
		return stacks;
	}
	
	@Override
	protected void onContentsChanged(int slot) {
		// when the contents changed, notify this instance's creator about it [used to mark the creator as dirty]
		// Humanity stained God
		super.onContentsChanged(slot);
		onDirtyCallback.invoke();
	}
	
	@FunctionalInterface
	public interface Notify {   // Some folks use Runnable, but I prefer not to use it for non-thread-related tasks
		void invoke();
	}
}