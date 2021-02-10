package com.soulspark.kettle.core.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class CupItemHandler extends ItemStackHandler {
	private final Notify onDirtyCallback;
	
	public CupItemHandler(int size, Notify onDirty) {
		super(size);
		onDirtyCallback = onDirty;
	}
	
	public ItemStack getStack() {
		return getStackInSlot(0);
	}
	
	public boolean isEmpty() {
		return getStack().isEmpty();
	}
	
	public void setStack(ItemStack stack) {
		setStackInSlot(0, stack);
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