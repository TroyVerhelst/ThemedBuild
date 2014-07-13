/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;
                
import java.util.Arrays;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan
 */
public class Plot {
    @Getter
    private Player p;
    @Getter
    private boolean assigned;
    @Getter
    private Location corner;
    public int Boundz[] = {0,0};
    public int Boundx[] = {0,0};
    @Getter
    private int rotation;
    private World w;
    
    public Plot(Location corner, int rot){
        assigned = false;
        this.corner = new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ());
//        corner.add(0, 2, 0).getBlock().setType(Material.BRICK);
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
                Location lc = new Location(w, x, corner.getBlockY(), Boundz[0]);
                lc.getBlock().setType(Material.DIAMOND_BLOCK);
            }
            for(int x = Boundx[0]; x < Boundx[1]; x++){
                Location lc = new Location(w, x, corner.getBlockY(), Boundz[1]);
                lc.getBlock().setType(Material.DIAMOND_BLOCK);
            }
            for(int z = Boundz[0]; z < Boundz[1]; z++){
                Location lc = new Location(w, Boundx[0], corner.getBlockY(), z);
                lc.getBlock().setType(Material.DIAMOND_BLOCK);
            }
            for(int z = Boundz[0]; z < Boundz[1]; z++){
                Location lc = new Location(w, Boundx[1], corner.getBlockY(), z);
                lc.getBlock().setType(Material.DIAMOND_BLOCK);
            }
            new Location(w, Boundx[1], corner.getBlockY(), Boundz[1]).getBlock().setType(Material.DIAMOND_BLOCK);
    }
    public void assign(Player p){
        this. p = p;
        assigned = true;
        DBmanager.curr.getPlots().put(p.getName(), this);
        if(DBmanager.plots.containsKey(p.getName()))
            DBmanager.plots.get(p.getName()).add(this);
        else
            DBmanager.plots.put(p.getName(), Arrays.asList(new Plot[] {this}));
        if(rotation == 1 || rotation == 4){
            new Location(w, corner.getBlockX()+1, corner.getBlockY()+1, corner.getBlockZ()).getBlock().setType(Material.DIAMOND_BLOCK);
            new Location(w, corner.getBlockX()+1, corner.getBlockY()+2, corner.getBlockZ()).getBlock().setType(Material.SIGN_POST);
        }else if(rotation == 2 || rotation == 3){
            new Location(w, corner.getBlockX()-1, corner.getBlockY()+1, corner.getBlockZ()).getBlock().setType(Material.DIAMOND_BLOCK);
            new Location(w, corner.getBlockX()-1, corner.getBlockY()+2, corner.getBlockZ()).getBlock().setType(Material.SIGN_POST);
        }
        
    }
    
}
