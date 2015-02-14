/*
 * This file is part of Freebuild.
 * 
 * Freebuild is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Freebuild is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Freebuild.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package com.mcmiddleearth.freebuild;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Donovan, aaldim
 */
public class Freebuild extends JavaPlugin{
    
    public static String prefix = ChatColor.DARK_AQUA +"["+ ChatColor.AQUA +"PlotManager"+ ChatColor.DARK_AQUA +"] "+ ChatColor.AQUA;
    
    @Getter
    private static Freebuild PluginInstance;
    
    private static LoadScheduler scheduler;
    
    @Override
    public void onEnable(){
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
    public void onDisable(){
        DBmanager.save();

    }
}

class LoadScheduler extends BukkitRunnable {

    @Override
    public void run() {
        Freebuild.getPluginInstance().getLogger().info("Trying load data...");
        DBmanager.loadAll();
        if(!DBmanager.loaded) {
            Freebuild.getPluginInstance().getLogger().info("Load data failed, retrying in 100 ticks");
        }
        else {
            Freebuild.getPluginInstance().getLogger().info("Plugin data loaded");
            this.cancel();
        }
    }
    
}
