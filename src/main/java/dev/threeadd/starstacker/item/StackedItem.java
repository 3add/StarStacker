package dev.threeadd.starstacker.item;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class StackedItem {

    private static final NamespacedKey key = new NamespacedKey("star_stacker", "amount");

    private final Item bukkitItem;

    // Accessing PDC is relatively resource intensive, thus we have a separate synced int
    private int amount;

    public StackedItem(Item bukkitItem) {
        this.bukkitItem = bukkitItem;

        int amount = bukkitItem.getPersistentDataContainer()
                .getOrDefault(key, PersistentDataType.INTEGER, bukkitItem.getItemStack().getAmount());

        setAmount(amount); // Save to PDC and local int

        this.bukkitItem.getItemStack().setAmount(1);
    }

    public Item getBukkitItem() {
        return bukkitItem;
    }

    public int getAmount() {
        return amount;
    }

    public void add(int amount) {
        setAmount(getAmount() + amount);
    }

    public void remove(int amount) {
        setAmount(getAmount() - amount);
    }

    public void setAmount(int amount) {
        this.amount = amount;
        bukkitItem.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, amount);
    }

    public void give(Inventory inv) {
        ItemStack original = bukkitItem.getItemStack();
        int total = getAmount();
        int max = original.getMaxStackSize();

        while (total > 0) {
            int giveAmount = Math.min(total, max);

            ItemStack stack = original.clone();
            stack.setAmount(giveAmount);

            Map<Integer, ItemStack> leftover = inv.addItem(stack);

            int leftoverAmount = leftover.values().stream()
                    .mapToInt(ItemStack::getAmount)
                    .sum();

            total -= giveAmount;
            total += leftoverAmount;

            if (leftoverAmount > 0) {
                setAmount(total);
                return;
            }
        }

        bukkitItem.remove(); // No left over, remove
    }

    public void merge(StackedItem other) {
        if (!isSimilar(other))
            throw new IllegalStateException("Attempted to merge not similar items");

        add(other.getAmount());
        other.getBukkitItem().remove();
    }

    public boolean isSimilar(StackedItem other) {
        return bukkitItem.getItemStack().isSimilar(
                other.getBukkitItem().getItemStack());
    }

    @Override
    public String toString() {
        return "StackedItem{" +
                ", bukkitItem=" + bukkitItem +
                "}";
    }
}
