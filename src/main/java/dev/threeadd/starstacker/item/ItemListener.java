package dev.threeadd.starstacker.item;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class ItemListener implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof InventoryHolder holder))
            return;

        event.setCancelled(true);

        StackedItem item = getItem(event.getItem());

        item.give(holder.getInventory());
    }

    @EventHandler
    public void onMerge(ItemMergeEvent event) {
        StackedItem from = getItem(event.getEntity());
        StackedItem to = getItem(event.getTarget());

        if (!from.isSimilar(to))
            return;

        event.setCancelled(true);

        to.merge(from);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        StackedItem item = getItem(event.getEntity());
        getNearbyItems(item).forEach(item::merge);
    }

    @EventHandler
    public void onDespawn(ItemDespawnEvent event) {
        event.setCancelled(true);
    }

    private List<StackedItem> getNearbyItems(StackedItem item) {
        return item.getBukkitItem().getLocation()
                .getNearbyLivingEntities(5).stream()
                .filter(entity -> entity instanceof Item) // Remove non item entities
                .map(entity -> getItem((Item) entity)) // Map to StackedItem
                .filter(stackedItem -> stackedItem.isSimilar(item)) // Only allow similar items
                .toList();
    }

    private StackedItem getItem(Item item) {
        return new StackedItem(item);
    }
}
