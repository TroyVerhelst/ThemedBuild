/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Donovan
 */
public class Freebuild extends JavaPlugin{
    @Getter
    private Freebuild PluginInstance;
    
    @Override
    public void onEnable(){
        PluginInstance = this;
        getCommand("Theme").setExecutor(new Create());
        this.getLogger().info("Running");
    }
    
    @Override
    public void onDisable(){
        this.getLogger().info("Ending");
    }
}
