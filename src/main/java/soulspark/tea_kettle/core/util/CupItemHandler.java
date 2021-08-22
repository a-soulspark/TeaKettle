package soulspark.tea_kettle.core.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import soulspark.tea_kettle.TeaKettle;

public class CupItemHandler extends ItemStackHandler {
	private final Notify onDirtyCallback;
	private final int capacity;
	private int filledSlots = 0;
	
	public CupItemHandler(int size, Notify onDirty) {
		super(size);
		capacity = size;
		onDirtyCallback = onDirty;
	}
	
	public ItemStack getLastStack() {
		if (filledSlots == 0) return ItemStack.EMPTY;
		return getStackInSlot(filledSlots - 1);
	}
	
	public ItemStack pop() {
		if (filledSlots == 0) return ItemStack.EMPTY;
		
		ItemStack lastStack = getLastStack().copy();
		setStackInSlot(--filledSlots, ItemStack.EMPTY);
		
		return lastStack;
	}
	
	public boolean addStack(ItemStack stack) {
		if (filledSlots >= capacity) return false;
		setStackInSlot(filledSlots++, stack);
		return true;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		filledSlots = nbt.getInt("filledSlots");
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = super.serializeNBT();
		nbt.putInt("filledSlots", filledSlots);
		return nbt;
	}
	
	public NonNullList<ItemStack> getStacks() {
		return stacks;
	}
	
	@Override
	protected void onContentsChanged(int slot) {
		// when the contents changed, notify this instance's creator about it [used to mark the creator as dirty]
		// Humanity stained God
		onDirtyCallback.invoke();
	}
	
	@FunctionalInterface
	public interface Notify {   // Some folks use Runnable, but I prefer not to use it for non-thread-related tasks
		void invoke();
	}
}