/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

/**
 *
 * @author Donovan
 */
public class DBmanager implements Listener{
    public static HashMap<String, ArrayList<Plot>> plots = new HashMap<String, ArrayList<Plot>>();
    
    public static HashMap<String, Theme> Themes = new HashMap<String, Theme>();
    
    public static Theme curr;
    
    private static File Theme_dat = new File(Freebuild.getPluginInstance().getDataFolder() + System.getProperty("file.separator") + "Themes");
    
    private static File Plot_dat = new File(Freebuild.getPluginInstance().getDataFolder() + System.getProperty("file.separator") + "Plots");
    
    public static void save(){
        if(!Theme_dat.exists()){
            Theme_dat.mkdirs();
        }
        File start = new File(Theme_dat + System.getProperty("file.separator") + curr.getTheme().replace(" ", "_") + ".MCtheme");
        try {
            FileWriter fr = new FileWriter(start.toString());
            try (PrintWriter writer = new PrintWriter(fr)) {
                writer.println(curr.getCent().getWorld().getName() + " , " + curr.getCent().getBlockX() + " , " + curr.getCent().getBlockY() + " , " + curr.getCent().getBlockZ());
                //------
                writer.println(curr.getX_left());
                writer.println(curr.getX_right());
                writer.println("Plots:");
                System.out.println(curr.getPlots().toString());
                for(Plot p : curr.getPlots()){
                        String s = p.getCorner().getWorld().getName() + " , " + 
                                   p.getCorner().getBlockX() + " , " + 
                                   p.getCorner().getBlockY() + " , " + 
                                   p.getCorner().getBlockZ() + " , " + 
                                   p.getRotation();
                                   if(curr.getCurrplots().contains(p)){
                                       s += " , #curr#";
                                   } else {
                                       s += " , " + p.getP();
                    }
                        writer.println(s);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        Freebuild.getPluginInstance().getConfig().set("currTheme", curr.getTheme());
        Freebuild.getPluginInstance().saveConfig();
    }
    public static void loadAll(){
        for(File f : Theme_dat.listFiles()){
            String name = f.getName().replace("_", " ");
            name = name.replace(".MCtheme", "");
            try {
                Scanner s;
                s = new Scanner(f);
                String line = s.nextLine();
                List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
                Location cent = new Location(Bukkit.getWorld(items.get(0)), Integer.parseInt(items.get(1)), Integer.parseInt(items.get(2))+1, Integer.parseInt(items.get(3)));
                int xl = Integer.parseInt(s.nextLine());
                int xr = Integer.parseInt(s.nextLine());
                s.nextLine();
                ArrayList<Plot> plotz = new ArrayList<Plot>();
                ArrayList<Plot> currplotz = new ArrayList<Plot>();
                while(s.hasNext()){
                    line = s.nextLine();
                    items = Arrays.asList(line.split("\\s*,\\s*"));
                    Location corner = new Location(Bukkit.getWorld(items.get(0)), Integer.parseInt(items.get(1)), Integer.parseInt(items.get(2)), Integer.parseInt(items.get(3)));
                    int rotation = Integer.parseInt(items.get(4));
                    String type = items.get(5);
                    boolean assigned;
                    String owner = null;
//                    System.out.print(type);
                    if(type.equalsIgnoreCase("#curr#")){
                        assigned = false;
                    }else{
                        assigned = true;
                        owner = type;
                    }
                    Plot p =  new Plot(corner, rotation, assigned, owner);
                    plotz.add(p);
                    if(!assigned){
                        currplotz.add(p);
                    }else{
                        if(DBmanager.plots.containsKey(owner)){
                            ArrayList<Plot> ps = DBmanager.plots.get(owner);
                            ps.add(p);
                            DBmanager.plots.put(owner, ps);
                        }else{
                            ArrayList<Plot> ps = new ArrayList<Plot>();
                            ps.add(p);
                            DBmanager.plots.put(owner, ps);
                        }
                    }
                }
                Theme t = new Theme(name, cent, plotz, currplotz, xl, xr);
                String curr1 = Freebuild.getPluginInstance().getConfig().getString("currTheme");
                if(t.getTheme().equalsIgnoreCase(curr1)){
                    DBmanager.curr = t;
                }
                DBmanager.Themes.put(t.getTheme(), t);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    @EventHandler
    public void onWorldSave(WorldSaveEvent e){
        DBmanager.save();
    }
}
