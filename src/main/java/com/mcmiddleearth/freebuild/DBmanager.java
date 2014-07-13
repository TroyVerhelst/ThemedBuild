/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;

/**
 *
 * @author Donovan
 */
public class DBmanager {
    public static HashMap<String, List<Plot>> plots = new HashMap<String, List<Plot>>();
    
    public static HashMap<String, Theme> Themes = new HashMap<String, Theme>();
    
    public static Theme curr;
    
    private static File Theme_dat = new File(Freebuild.getPluginInstance().getDataFolder() + System.getProperty("file.separator") + "Themes");
    
    private static File Plot_dat = new File(Freebuild.getPluginInstance().getDataFolder() + System.getProperty("file.separator") + "Plots");
    
    public static void save(){
        if(!Theme_dat.exists()){
            Theme_dat.mkdirs();
        }
        File start = new File(Theme_dat + System.getProperty("file.separator") + curr.getTheme().replace(" ", "-") + ".MCtheme");
        
        try {
            FileWriter fwr = new FileWriter(start.toString());
            PrintWriter writer = new PrintWriter(fwr);
            writer.println(curr.getCent().getWorld().getName() + " , " + curr.getCent().getBlockX() + " , " + curr.getCent().getBlockY() + " , " + curr.getCent().getBlockZ());
            //------
            writer.println(curr.getX_left());
            writer.println(curr.getX_right());
            for(Plot p : curr.getPlots().values()){
                if(!curr.getCurrplots().contains(p)){
                    String s = curr.getCent().getWorld().getName() + " , " + 
                               curr.getCent().getBlockX() + " , " + 
                               curr.getCent().getBlockY() + " , " + 
                               curr.getCent().getBlockZ() + " , " + 
                               p.getRotation() + " , " +
                               p.getP().getName();
                    writer.println(s);
                }
            }
            for(Plot p : curr.getCurrplots()){
                String s = curr.getCent().getWorld().getName() + " , " + 
                           curr.getCent().getBlockX() + " , " + 
                           curr.getCent().getBlockY() + " , " + 
                           curr.getCent().getBlockZ() + " , " + 
                           p.getRotation() + " , ";
                if(curr.getPlots().containsValue(p)){
                    s+=p.getP().getName();
                }
                s+=" , #curr#";
            }
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
