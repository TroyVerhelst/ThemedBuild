/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Donovan, aaldim
 */
public class Freebuild extends JavaPlugin{
    
    public static String prefix = ChatColor.DARK_AQUA +"["+ ChatColor.AQUA +"PlotManager"+ ChatColor.DARK_AQUA +"] "+ ChatColor.AQUA;
    
    @Getter
    private static Freebuild PluginInstance;
    
    @Override
    public void onEnable(){
        PluginInstance = this;
        this.saveDefaultConfig();
        getCommand("Theme").setExecutor(new Create());
        getServer().getPluginManager().registerEvents(new Tool(), this);
        getServer().getPluginManager().registerEvents(new Protection(), this);
        getServer().getPluginManager().registerEvents(new DBmanager(), this);
        DBmanager.loadAll();
    }
    
    @Override
    public void onDisable(){
        DBmanager.save();

    }
}
