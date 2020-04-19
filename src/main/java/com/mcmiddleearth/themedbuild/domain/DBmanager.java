/*
 * This file is part of ThemedBuild.
 * 
 * ThemedBuild is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThemedBuild is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ThemedBuild.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package com.mcmiddleearth.themedbuild.domain;

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

import com.mcmiddleearth.themedbuild.ThemedBuildPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

/**
 *
 * @author Donovan
 */
public class DBmanager implements Listener{
    public static HashMap<String, ArrayList<Plot>> plots = new HashMap<>();
    
    public static HashMap<String, ArrayList<Plot>> pastPlots = new HashMap<>();
    
    public static HashMap<String, Theme> Themes = new HashMap<>();
    
    public static IPlotModel IncompleteModel = null;
    
    public static Theme curr;
    
    public static IPlotModel currModel;
    
    private static final File Theme_dat = new File(ThemedBuildPlugin.getPluginInstance().getDataFolder() + System.getProperty("file.separator") + "Themes");
    
    private static final File Plot_dat = new File(ThemedBuildPlugin.getPluginInstance().getDataFolder() + System.getProperty("file.separator") + "Plots");
    
    public static int MaxPlotsPerPlayer;
    
    public static boolean InfinitePlotsPerPlayer;
    
    public static boolean BuildPastPlots;
    
    public static ArrayList<String> Models;
    
    public static boolean loaded;
    
    private static String oldPlotFileExtension = "MCplot";
    private static String newPlotFileExtension = "MCMEplot";
    
    static{
        if(!Theme_dat.exists()){
            Theme_dat.mkdirs();
        }
        if(!Plot_dat.exists()){
            Plot_dat.mkdirs();
            File out = new File(Plot_dat + System.getProperty("file.separator") + "default."+oldPlotFileExtension);
            PlotModel.generateDefaultModel(out);
        }
        loaded = false;
    }
    
    public static void save(){
        if(curr != null){
            File start = new File(Theme_dat + System.getProperty("file.separator") + curr.getTheme().replace(" ", "_") + ".MCtheme");
            try {
                FileWriter fr = new FileWriter(start.toString());
                try (PrintWriter writer = new PrintWriter(fr)) {
                    writer.println(curr.getCent().getWorld().getName() + " , " 
                                 + curr.getCent().getBlockX() + " , " 
                                 + curr.getCent().getBlockY() + " , " 
                                 + curr.getCent().getBlockZ());
                    //------
                    writer.println(curr.getX_left());
                    writer.println(curr.getX_right());
                    writer.println(curr.getModel());
                    writer.println(curr.getURL());
                    writer.println("Plots:");
                    //System.out.println(curr.getPlots().toString());
                    for(Plot p : curr.getPlots()){
                        String s = p.getCorner().getWorld().getName() + " , " + 
                                   p.getCorner().getBlockX() + " , " + 
                                   p.getCorner().getBlockY() + " , " + 
                                   p.getCorner().getBlockZ() + " , " + 
                                   p.getRotation() + " , " +
                                   p.getSizeX() + " , " +
                                   p.getSizeZ();
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
            ThemedBuildPlugin.getPluginInstance().getConfig().set("currTheme", curr.getTheme());
        }
        ThemedBuildPlugin.getPluginInstance().saveConfig();
    }
    public static void savePlotModel(IPlotModel model, Player p){
        File out = new File(Plot_dat + System.getProperty("file.separator") 
                           + model.getName().replace(" ", "_") + "."+newPlotFileExtension);
        model.saveModel(out,p);
        updateModelsList();
    }
    public static void deletePlotModel(String name){
        File f = new File(Plot_dat + System.getProperty("file.separator") + name.replace(" ", "_") 
                                                                          + "."+oldPlotFileExtension);
        if(f.exists()){
            f.delete();
        }
        f = new File(Plot_dat + System.getProperty("file.separator") + name.replace(" ", "_") 
                                                                          + "."+newPlotFileExtension);
        if(f.exists()){
            f.delete();
        }
    }
    public static IPlotModel loadPlotModel(String name){
        File in = new File(Plot_dat + System.getProperty("file.separator") + name.replace(" ", "_") 
                                                                           + "."+newPlotFileExtension);
        IPlotModel model;
        if(in.exists()) {
            model = new MCMEStoragePlotModel(name,in);
        } else {
            in = new File(Plot_dat + System.getProperty("file.separator") + name.replace(" ", "_") 
                                                                           + "."+oldPlotFileExtension);
            model = new PlotModel(name,in);
        }
        return model;
    }
    public static void updateModelsList(){
        Models = new ArrayList<>();
        for(File f: Plot_dat.listFiles()){
            String name;
            if(f.getName().endsWith("."+oldPlotFileExtension)) {
                name = f.getName().replace("."+oldPlotFileExtension, "");
            } else {
                name = f.getName().replace("."+newPlotFileExtension, "");
            }
            if(!Models.contains(name)) {
                Models.add(name);   
            }
        }
    }
    public static boolean modelExists(String model){
        for(String s: Models){
            if(s.equals(model)){
                return true;
            }
        }
        return false;
    }
    public static void loadAll(){
        MaxPlotsPerPlayer = ThemedBuildPlugin.getPluginInstance().getConfig().getInt("maxPlotsPerPlayer");
        InfinitePlotsPerPlayer = (MaxPlotsPerPlayer < 0);
        BuildPastPlots = ThemedBuildPlugin.getPluginInstance().getConfig().getBoolean("buildPastPlots");
        Tool.setModelTool(Material.getMaterial(ThemedBuildPlugin.getPluginInstance().getConfig().getString("modelTool")));
        Tool.setLiquidTool(Material.getMaterial(ThemedBuildPlugin.getPluginInstance().getConfig().getString("liquidTool")));
        Tool.setFireTool(Material.getMaterial(ThemedBuildPlugin.getPluginInstance().getConfig().getString("fireTool")));
        updateModelsList();
        for(File f : Theme_dat.listFiles()){
            String name = f.getName().replace("_", " ");
            name = name.replace(".MCtheme", "");
            try {
                String curr1 = ThemedBuildPlugin.getPluginInstance().getConfig().getString("currTheme");
                boolean isCurrent = name.equalsIgnoreCase(curr1);
                Scanner s;
                s = new Scanner(f);
                String line = s.nextLine();
                List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
                Location cent = new Location(Bukkit.getWorld(items.get(0)), Integer.parseInt(items.get(1)), Integer.parseInt(items.get(2))+1, Integer.parseInt(items.get(3)));
                if(Bukkit.getWorld(items.get(0)) == null) {
                    return;
                }
                int xl = Integer.parseInt(s.nextLine());
                int xr = Integer.parseInt(s.nextLine());
                String model = s.nextLine();
                String url = s.nextLine();
                s.nextLine();
                ArrayList<Plot> plotz = new ArrayList<>();
                ArrayList<Plot> currplotz = new ArrayList<>();
                while(s.hasNext()){
                    line = s.nextLine();
                    items = Arrays.asList(line.split("\\s*,\\s*"));
                    Location corner = new Location(Bukkit.getWorld(items.get(0)), Integer.parseInt(items.get(1)), Integer.parseInt(items.get(2)), Integer.parseInt(items.get(3)));
                    int rotation = Integer.parseInt(items.get(4));
                    int sx = Integer.parseInt(items.get(5));
                    int sz = Integer.parseInt(items.get(6));
                    String type = items.get(7);
                    boolean assigned;
                    String owner = null;
//                    System.out.print(type);
                    if(type.equalsIgnoreCase("#curr#")){
                        assigned = false;
                    }else{
                        assigned = true;
                        owner = type;
                    }
                    Plot p =  new Plot(corner, rotation, assigned, owner, sx, sz);
                    plotz.add(p);
                    if(!assigned){
                        currplotz.add(p);
                    }else if(isCurrent){
                        if(DBmanager.plots.containsKey(owner)){
                            ArrayList<Plot> ps = DBmanager.plots.get(owner);
                            ps.add(p);
                            DBmanager.plots.put(owner, ps);
                        }else{
                            ArrayList<Plot> ps = new ArrayList<>();
                            ps.add(p);
                            DBmanager.plots.put(owner, ps);
                        }
                    }else{
                        if(DBmanager.pastPlots.containsKey(owner)){
                            ArrayList<Plot> ps = DBmanager.pastPlots.get(owner);
                            ps.add(p);
                            DBmanager.pastPlots.put(owner, ps);
                        }else{
                            ArrayList<Plot> ps = new ArrayList<>();
                            ps.add(p);
                            DBmanager.pastPlots.put(owner, ps);
                        }
                    }
                }
                Theme t = new Theme(name, cent, plotz, currplotz, xl, xr, model);
                t.setURL(url);
                if(t.getTheme().equalsIgnoreCase(curr1)){
                    DBmanager.curr = t;
                    currModel = loadPlotModel(model);
                }
                DBmanager.Themes.put(t.getTheme(), t);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        loaded = true;
    }
    
    @EventHandler
    public void onWorldSave(WorldSaveEvent e){
        DBmanager.save();
    }
}
