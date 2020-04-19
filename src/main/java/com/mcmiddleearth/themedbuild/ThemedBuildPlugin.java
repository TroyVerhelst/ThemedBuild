/*
 * This file is part of ThemedBuild.
 *
 * ThemedBuild is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ThemedBuild is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ThemedBuild.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.mcmiddleearth.themedbuild;

import com.mcmiddleearth.themedbuild.domain.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The type Themed build plugin.
 */
public class ThemedBuildPlugin extends JavaPlugin {
    public static String prefix = ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "PlotManager" + ChatColor.DARK_AQUA + "] " + ChatColor.AQUA;

    private static ThemedBuildPlugin PluginInstance;

    private static LoadScheduler scheduler;

    @Override
    public void onEnable() {
        PluginInstance = this;
        this.saveDefaultConfig();
        getCommand("Theme").setExecutor(new Create());
        getServer().getPluginManager().registerEvents(new Tool(), this);
        getServer().getPluginManager().registerEvents(new Protection(), this);
        getServer().getPluginManager().registerEvents(new DBmanager(), this);
        scheduler = new LoadScheduler();
        scheduler.runTaskTimer(this, 0, 100);
        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        DBmanager.save();
    }

    public static boolean denyBuild(Player p, Location location) {
        return Protection.denyBuild(p, location);
    }

    public static ThemedBuildPlugin getPluginInstance() {
        return PluginInstance;
    }
}
