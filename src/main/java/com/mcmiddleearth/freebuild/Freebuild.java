/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Donovan, aaldim
 */
public class Freebuild extends JavaPlugin{
    @Getter
    private static Freebuild PluginInstance;
    
    @Override
    public void onEnable(){
        PluginInstance = this;
        getCommand("Theme").setExecutor(new Create());
        getServer().getPluginManager().registerEvents(new Protection(), this);
    }
    
    @Override
    public void onDisable(){
        DBmanager.save();
    }
}
