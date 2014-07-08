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
    private static Freebuild PluginInstance;
    
    @Override
    public void onEnable(){
        PluginInstance = this;
        getCommand("Theme").setExecutor(new Create());
    }
    
    @Override
    public void onDisable(){
        DBmanager.save();
    }
}
