/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Stairs;

/**
 *
 * @author Donovan, Ivan1pl
 */
public class Theme {
    @Getter
    private String theme;
    @Getter
    private Location cent;
    @Getter
    private ArrayList<Plot> plots = new ArrayList<Plot>();
    @Getter
    private ArrayList<Plot> currplots = new ArrayList<Plot>();
    @Getter
    private int x_left;
    @Getter
    private int x_right;
    
    public Theme(String name, String url){
        this.theme = name;
        Location ocent = DBmanager.curr.getCent();
        ocent.add(0, 0, 113);
        this.cent=ocent;
        this.Generate();
    }
    public Theme(String name, String url, Location loc){
        this.theme = name;
        this.cent = loc.add(0 , -1, 0);
        this.Generate();
    }
    public Theme(String name, Location loc, ArrayList<Plot> plotz, ArrayList<Plot> currs, int xl, int xr){
        this.theme = name;
        this.cent = loc.add(0 , -1, 0);
        this.plots.addAll(plotz);
        this.currplots.addAll(currs);
        this.x_left = xl;
        this.x_right = xr;
    }
    private void Generate(){
        Location loc = cent;
        BlockState state;
        Stairs stairs;
        for(int z = loc.getBlockZ()-56; z<loc.getBlockZ()+57; z++){
            for(int x = loc.getBlockX()-1; x<loc.getBlockX() + 2; x++){
                Location lc = new Location(loc.getWorld(), x, loc.getBlockY(), z);
                lc.getBlock().setType(Material.SANDSTONE);
            }
            Location lc = new Location(loc.getWorld(), loc.getBlockX()-2, loc.getBlockY(), z);
            lc.getBlock().setType(Material.BRICK_STAIRS);
            state = lc.getBlock().getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.WEST);
            state.setData(stairs);
            state.update(true);
            lc = new Location(loc.getWorld(), loc.getBlockX()+2, loc.getBlockY(), z);
            lc.getBlock().setType(Material.BRICK_STAIRS);
            state = lc.getBlock().getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.EAST);
            state.setData(stairs);
            state.update(true);
        }
        this.genPlots(true);
    }
    public void close(){
        
    }
    public void genPlots(boolean first){
        Bukkit.getServer().broadcastMessage(Freebuild.prefix + "Generating new plots, Lag incoming");
        Plot p1;
        Plot p2;
        Plot p3;
        Plot p4;
        Block b;
        BlockState state;
        Stairs stairs;
        if(first){
//            Bukkit.getServer().broadcastMessage("1");
            p1 = new Plot(new Location(cent.getWorld(), cent.getBlockX()+3, cent.getBlockY()+1, cent.getBlockZ()+3), 1);
            p2 = new Plot(new Location(cent.getWorld(), cent.getBlockX()-3, cent.getBlockY()+1, cent.getBlockZ()+3), 2);
            p3 = new Plot(new Location(cent.getWorld(), cent.getBlockX()+3, cent.getBlockY()+1, cent.getBlockZ()-3), 4);
            p4 = new Plot(new Location(cent.getWorld(), cent.getBlockX()-3, cent.getBlockY()+1, cent.getBlockZ()-3), 3);
            this.x_left = cent.getBlockX()+3;
            this.x_right = cent.getBlockX()-3;
        }else{
            this.x_left += 55;
            this.x_right -= 55;
            p1 = new Plot(new Location(cent.getWorld(), this.x_left, cent.getBlockY()+1, cent.getBlockZ()+3), 1);
            p2 = new Plot(new Location(cent.getWorld(), this.x_right, cent.getBlockY()+1, cent.getBlockZ()+3), 2);
            p3 = new Plot(new Location(cent.getWorld(), this.x_left, cent.getBlockY()+1, cent.getBlockZ()-3), 4);
            p4 = new Plot(new Location(cent.getWorld(), this.x_right, cent.getBlockY()+1, cent.getBlockZ()-3), 3);
        }
        currplots.clear();
        currplots.add(p1);
        currplots.add(p2);
        currplots.add(p3);
        currplots.add(p4);
        plots.add(p1);
        plots.add(p2);
        plots.add(p3);
        plots.add(p4);
        for(int z = cent.getBlockZ()-1; z<cent.getBlockZ()+2; z++){
            for(int x = p1.getCorner().getBlockX()-1; x<p1.getCorner().getBlockX()+54; x++){
                new Location(cent.getWorld(), x, cent.getBlockY(), z).getBlock().setType(Material.SANDSTONE);
            }
            for(int x = p2.getCorner().getBlockX()+1; x>p2.getCorner().getBlockX()-54; x--){
                new Location(cent.getWorld(), x, cent.getBlockY(), z).getBlock().setType(Material.SANDSTONE);
            }
        }
        for(int x = p1.getCorner().getBlockX()-1; x<p1.getCorner().getBlockX()+54; x++){
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ()-2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.NORTH);
            state.setData(stairs);
            state.update(true);
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ()+2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.SOUTH);
            state.setData(stairs);
            state.update(true);
        }
        for(int x = p2.getCorner().getBlockX()+1; x>p2.getCorner().getBlockX()-54; x--){
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ()-2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.NORTH);
            state.setData(stairs);
            state.update(true);
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ()+2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.SOUTH);
            state.setData(stairs);
            state.update(true);
        }
    }
}
