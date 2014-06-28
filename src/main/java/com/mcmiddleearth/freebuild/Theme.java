/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Donovan
 */
public class Theme {
    private String theme;
    @Getter
    private Location cent;
    
    public Theme(String name){
        this.theme = name;
        Location ocent = DBmanager.curr.getCent();
        ocent.add(0, 0, 109);
        this.cent=ocent;
        Generate();
    }
    public Theme(String name, Location loc){
        this.theme = name;
        this.cent = loc;
    }
    private void Generate(){
        Location loc = cent;
        
        cent.getBlock().setType(Material.SANDSTONE);
    }
    
}
