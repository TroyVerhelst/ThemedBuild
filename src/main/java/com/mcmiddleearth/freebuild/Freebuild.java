/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
