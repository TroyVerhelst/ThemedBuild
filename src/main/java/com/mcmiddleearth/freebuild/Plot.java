/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;
                
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan
 */
public class Plot {
    private Player p;
    private int Boundz[];
    private int Boundx[];
    
    public Plot(Player p, Location center){
        this.p = p;
    }
    
}
