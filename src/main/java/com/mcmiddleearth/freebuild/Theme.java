/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Donovan
 */
public class Theme {
    @Getter
    private String theme;
    @Getter
    private Location cent;
    @Getter
    private HashMap<Integer, ArrayList<Plot>> plots = new HashMap<Integer, ArrayList<Plot>>();
    
    public Theme(String name){
        this.theme = name;
        Location ocent = DBmanager.curr.getCent();
        ocent.add(0, -1, 109);
        this.cent=ocent;
        this.Generate();
    }
    public Theme(String name, Location loc){
        this.theme = name;
        this.cent = loc.add(0 , -1, 0);
        this.Generate();
    }
    private void Generate(){
        Location loc = cent;
        for(int x = loc.getBlockX()-1; x<loc.getBlockX() + 2; x++){
            for(int z = loc.getBlockZ(); z<loc.getBlockZ()+109; z++){
                Location lc = new Location(loc.getWorld(), x, loc.getBlockY(), z);
                lc.getBlock().setType(Material.SANDSTONE);
            }
        }
        this.genPlots(true);
    
    }
    private void genPlots(boolean first){
        if(first){
            Plot p1 = new Plot(new Location(cent.getWorld(), cent.getBlockX()+3, cent.getBlockY(), cent.getBlockZ()+3), 1);
            Plot p2 = new Plot(new Location(cent.getWorld(), cent.getBlockX()-3, cent.getBlockY(), cent.getBlockZ()+3), 2);
            Plot p3 = new Plot(new Location(cent.getWorld(), cent.getBlockX()+3, cent.getBlockY(), cent.getBlockZ()-3), 4);
            Plot p4 = new Plot(new Location(cent.getWorld(), cent.getBlockX()-3, cent.getBlockY(), cent.getBlockZ()-3), 3);
        }
    }
}
