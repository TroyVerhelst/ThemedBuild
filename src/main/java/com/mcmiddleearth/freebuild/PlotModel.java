/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

/**
 *
 * @author Ivan1pl
 */
public class PlotModel {
    private class SaveScheduler extends BukkitRunnable {
        private final PlotModel model;
        private final Player player;
        private final File file;
        
        public SaveScheduler(PlotModel model, Player player, File file){
            this.model = model;
            this.player = player;
            this.file = file;
        }
        
        @Override
        public void run() {
            model.save(file, player);
        }
    }
    @Getter
    private final String name;
    private ItemStack[][][] model;
    @Getter
    private int sizex;
    private int sizey;
    @Getter
    private int sizez;
    private Location point1 = null;
    private Location point2 = null;
    public PlotModel(String modelname){
        name = modelname;
        sizex = sizey = sizez = 0;
    }
    public PlotModel(String modelname, File in){
        name = modelname;
        try {
            FileInputStream fis = new FileInputStream(in);
            BukkitObjectInputStream stream = new BukkitObjectInputStream(fis);
            sizex = stream.readInt();
            sizey = stream.readInt();
            sizez = stream.readInt();
            if(sizex == 0 || sizey == 0 || sizez == 0){
                model = null;
            }
            else{
                model = new ItemStack[sizex][sizey][sizez];
            }
            for(int x = 0; x < sizex; ++x){
                for(int y = 0; y < sizey; ++y){
                    for(int z = 0; z < sizez; ++z){
                        model[x][y][z] = (ItemStack) stream.readObject();
                    }
                }
            }
            stream.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setPoint1(Location l){
        point1 = new Location(l.getWorld(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
    }
    public void setPoint2(Location l){
        point2 = new Location(l.getWorld(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
    }
    public void saveModel(File out, Player p){
        new SaveScheduler(this,p,out).runTask(Freebuild.getPluginInstance());
    }
    private void save(File out, Player p){
        if(point1 != null && point2 != null && point1.getWorld().getName().equals(point2.getWorld().getName())){
            sizex = Math.abs(point1.getBlockX()-point2.getBlockX())+1;
            sizey = Math.abs(point1.getBlockY()-point2.getBlockY())+1;
            sizez = Math.abs(point1.getBlockZ()-point2.getBlockZ())+1;
            model = new ItemStack[sizex][sizey][sizez];
            Location corner = new Location(point1.getWorld(), Math.min(point1.getBlockX(), point2.getBlockX()),
                                                              Math.min(point1.getBlockY(), point2.getBlockY()),
                                                              Math.min(point1.getBlockZ(), point2.getBlockZ()));
            Location iter;
            if(p != null){
                p.sendMessage(Freebuild.prefix + "Getting blocks...");
            }
            for(int x = 0; x < sizex; ++x){
                for(int y = 0; y < sizey; ++y){
                    for(int z = 0; z < sizez; ++z){
                        iter = new Location(corner.getWorld(),corner.getBlockX()+x,corner.getBlockY()+y,corner.getBlockZ()+z);
                        model[x][y][z] = iter.getBlock().getState().getData().toItemStack();
                    }
                }
            }
            if(p != null){
                p.sendMessage(Freebuild.prefix + "Done");
            }
            if(p != null){
                p.sendMessage(Freebuild.prefix + "Saving model...");
            }
            try {
                FileOutputStream fos = new FileOutputStream(out);
                BukkitObjectOutputStream stream = new BukkitObjectOutputStream(fos);
                stream.writeInt(sizex);
                stream.writeInt(sizey);
                stream.writeInt(sizez);
                for(int x = 0; x < sizex; ++x){
                    for(int y = 0; y < sizey; ++y){
                        for(int z = 0; z < sizez; ++z){
                            stream.writeObject(model[x][y][z]);
                        }
                    }
                }
                stream.close();
                fos.close();
                if(p != null){
                    p.sendMessage(Freebuild.prefix + "Done");
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            if(p != null){
                p.sendMessage(Freebuild.prefix + "You have to set both points first");
            }
        }
    }
    public void generate(Location l){
        Location corner = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
        Location iter;
        for(int x = 0; x < sizex; ++x){
            for(int y = 0; y < sizey; ++y){
                for(int z = 0; z < sizez; ++z){
                    iter = new Location(corner.getWorld(),corner.getBlockX()+x,corner.getBlockY()+y,corner.getBlockZ()+z);
                    iter.getBlock().setType(model[x][y][z].getType());
                    BlockState state = iter.getBlock().getState();
                    state.setData(model[x][y][z].getData());
                    state.update(true);
                }
            }
        }
    }
    public static void generateDefaultModel(File out){
        try {
            FileOutputStream fos = new FileOutputStream(out);
            BukkitObjectOutputStream stream = new BukkitObjectOutputStream(fos);
            stream.writeInt(48);
            stream.writeInt(0);
            stream.writeInt(48);
            /*for(int x = 0; x < 48; ++x){
                for(int y = 0; y < 1; ++y){
                    for(int z = 0; z < 48; ++z){
                        stream.writeObject(new ItemStack(Material.GRASS));
                    }
                }
            }*/
            stream.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
