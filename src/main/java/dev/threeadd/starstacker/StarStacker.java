package dev.threeadd.starstacker;

import dev.threeadd.starstacker.item.ItemListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class StarStacker extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Enabled Star Stacker by 3add");

        getServer().getPluginManager().registerEvents(new ItemListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled Star Stacker by 3add");
    }
}
