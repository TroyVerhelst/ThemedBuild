/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;
                
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan
 */
public class Plot {
    private Player p;
    private int y;
    private int Boundz[] = {0,0};
    private int Boundx[] = {0,0};
    private int rotation;
    private World w;
    
    public Plot(Location corner, int rot){
        this.y = corner.getBlockY();
        corner.add(0, 2, 0).getBlock().setType(Material.BRICK);
        if(rot == 1){
            this.Boundz[0]=corner.getBlockZ();
            this.Boundz[1]=corner.getBlockZ()+50;
            this.Boundx[0]=corner.getBlockX();
            this.Boundx[1]=corner.getBlockX()+50;
        }else if(rot == 2){
            this.Boundz[0]=corner.getBlockZ();
            this.Boundz[1]=corner.getBlockZ()+50;
            this.Boundx[0]=corner.getBlockX()-50;
            this.Boundx[1]=corner.getBlockX();
        }else if(rot == 3){
            this.Boundz[0]=corner.getBlockZ()-50;
            this.Boundz[1]=corner.getBlockZ();
            this.Boundx[0]=corner.getBlockX()-50;
            this.Boundx[1]=corner.getBlockX();
        }else if(rot == 4){
            this.Boundz[0]=corner.getBlockZ()-50;
            this.Boundz[1]=corner.getBlockZ();
            this.Boundx[0]=corner.getBlockX();
            this.Boundx[1]=corner.getBlockX()+50;
        }
        this.w = corner.getWorld();
        this.rotation = rot;
        Generate();
    }
    private void Generate(){
            for(int x = Boundx[0]; x < Boundx[1]; x++){
                Location lc = new Location(w, x, this.y, Boundz[0]);
                lc.getBlock().setType(Material.DIAMOND_BLOCK);
            }
            for(int x = Boundx[0]; x < Boundx[1]; x++){
                Location lc = new Location(w, x, this.y, Boundz[1]);
                lc.getBlock().setType(Material.DIAMOND_BLOCK);
            }
            for(int z = Boundz[0]; z < Boundz[1]; z++){
                Location lc = new Location(w, Boundx[0], this.y, z);
                lc.getBlock().setType(Material.DIAMOND_BLOCK);
            }
            for(int z = Boundz[0]; z < Boundz[1]; z++){
                Location lc = new Location(w, Boundx[1], this.y, z);
                lc.getBlock().setType(Material.DIAMOND_BLOCK);
            }
    }
    public void assign(Player p){
        this. p = p;
        switch(rotation){
            case 1:
                Location lc = new Location(w, Boundx[0]+1, this.y+1, Boundz[0]);
                lc.getBlock().setType(Material.DIAMOND_BLOCK);
                lc.add(0 , 0, 1).getBlock().setType(Material.SIGN);
        }
        
    }
    
}
