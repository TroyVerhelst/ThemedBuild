/*
 * This file is part of Freebuild.
 * 
 * Freebuild is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Freebuild is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Freebuild.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package com.mcmiddleearth.freebuild;
                
import java.util.ArrayList;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan, aaldim
 */
public class Plot {
    @Getter
    private String p;
    @Getter
    private boolean assigned;
    @Getter
    private Location corner;
    public int Boundz[] = {0,0};
    public int Boundx[] = {0,0};
    @Getter
    private int rotation;
    @Getter
    private World w;
    @Getter
    private int sizeX = 50;
    @Getter
    private int sizeZ = 50;
    Block plotsign;
    
    public Plot(Location corner, int rot, int sx, int sz){
        sizeX = sx;
        sizeZ = sz;
        assigned = false;
        this.corner = new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ());
//        corner.add(0, 2, 0).getBlock().setType(Material.BRICK);
        if(rot == 1){
            this.Boundz[0]=corner.getBlockZ();
            this.Boundz[1]=corner.getBlockZ()+sizeZ;
            this.Boundx[0]=corner.getBlockX();
            this.Boundx[1]=corner.getBlockX()+sizeX;
        }else if(rot == 2){
            this.Boundz[0]=corner.getBlockZ();
            this.Boundz[1]=corner.getBlockZ()+sizeZ;
            this.Boundx[0]=corner.getBlockX()-sizeX;
            this.Boundx[1]=corner.getBlockX();
        }else if(rot == 3){
            this.Boundz[0]=corner.getBlockZ()-sizeZ;
            this.Boundz[1]=corner.getBlockZ();
            this.Boundx[0]=corner.getBlockX()-sizeX;
            this.Boundx[1]=corner.getBlockX();
        }else if(rot == 4){
            this.Boundz[0]=corner.getBlockZ()-sizeZ;
            this.Boundz[1]=corner.getBlockZ();
            this.Boundx[0]=corner.getBlockX();
            this.Boundx[1]=corner.getBlockX()+sizeX;
        }
        this.w = corner.getWorld();
        this.rotation = rot;
        Generate();
    }
    public Plot(Location corner, int rot, boolean assigned, String owner, int sx, int sz){
        sizeX = sx;
        sizeZ = sz;
        this.assigned = false;
        this.corner = new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ());
//        corner.add(0, 2, 0).getBlock().setType(Material.BRICK);
        if(rot == 1){
            this.Boundz[0]=corner.getBlockZ();
            this.Boundz[1]=corner.getBlockZ()+sizeZ;
            this.Boundx[0]=corner.getBlockX();
            this.Boundx[1]=corner.getBlockX()+sizeX;
        }else if(rot == 2){
            this.Boundz[0]=corner.getBlockZ();
            this.Boundz[1]=corner.getBlockZ()+sizeZ;
            this.Boundx[0]=corner.getBlockX()-sizeX;
            this.Boundx[1]=corner.getBlockX();
        }else if(rot == 3){
            this.Boundz[0]=corner.getBlockZ()-sizeZ;
            this.Boundz[1]=corner.getBlockZ();
            this.Boundx[0]=corner.getBlockX()-sizeX;
            this.Boundx[1]=corner.getBlockX();
        }else if(rot == 4){
            this.Boundz[0]=corner.getBlockZ()-sizeZ;
            this.Boundz[1]=corner.getBlockZ();
            this.Boundx[0]=corner.getBlockX();
            this.Boundx[1]=corner.getBlockX()+sizeX;
        }
        this.w = corner.getWorld();
        this.rotation = rot;
        this.assigned = assigned;
        if(assigned){
            this.p = owner;
        }
        if(rotation == 1){
            plotsign = new Location(w, corner.getBlockX()+1, corner.getBlockY()+1, corner.getBlockZ()-1).getBlock();
        }else if(rotation == 4){
            plotsign = new Location(w, corner.getBlockX()+1, corner.getBlockY()+1, corner.getBlockZ()+1).getBlock();
        }else if(rotation == 2){
            plotsign = new Location(w, corner.getBlockX()-1, corner.getBlockY()+1, corner.getBlockZ()-1).getBlock();
        }else if(rotation == 3){
            plotsign = new Location(w, corner.getBlockX()-1, corner.getBlockY()+1, corner.getBlockZ()+1).getBlock();
        }
    }
    private void Generate(){
            for(int x = Boundx[0]; x < Boundx[1]; x++){
                Location lc = new Location(w, x, corner.getBlockY(), Boundz[0]);
                lc.getBlock().setType(Material.DOUBLE_STEP);
            }
            for(int x = Boundx[0]; x < Boundx[1]; x++){
                Location lc = new Location(w, x, corner.getBlockY(), Boundz[1]);
                lc.getBlock().setType(Material.DOUBLE_STEP);
            }
            for(int z = Boundz[0]; z < Boundz[1]; z++){
                Location lc = new Location(w, Boundx[0], corner.getBlockY(), z);
                lc.getBlock().setType(Material.DOUBLE_STEP);
            }
            for(int z = Boundz[0]; z < Boundz[1]; z++){
                Location lc = new Location(w, Boundx[1], corner.getBlockY(), z);
                lc.getBlock().setType(Material.DOUBLE_STEP);
            }
            new Location(w, Boundx[1], corner.getBlockY(), Boundz[1]).getBlock().setType(Material.DOUBLE_STEP);
    }
    public void assign(Player p){
        DBmanager.curr.getCurrplots().remove(this);
        this.p = p.getUniqueId().toString();
        assigned = true;
        BlockFace modelDirection;
        if(rotation == 1 || rotation == 2) {
            modelDirection = BlockFace.SOUTH;
        }
        else {
            modelDirection = BlockFace.NORTH;
        }
        DBmanager.currModel.generate(new Location(w, Boundx[0]+1, corner.getBlockY()-1, Boundz[0]+1),modelDirection);
//        DBmanager.curr.getPlots().put(p.getName(), this);
        if(DBmanager.plots.containsKey(p.getUniqueId().toString())){
            ArrayList<Plot> ps = DBmanager.plots.get(p.getUniqueId().toString());
            ps.add(this);
            DBmanager.plots.put(p.getUniqueId().toString(), ps);
        }else{
            ArrayList<Plot> ps = new ArrayList<Plot>();
            ps.add(this);
            DBmanager.plots.put(p.getUniqueId().toString(), ps);
        }
        if(rotation == 1 || rotation == 4){
            new Location(w, corner.getBlockX()+1, corner.getBlockY()+1, corner.getBlockZ()).getBlock().setType(Material.DIAMOND_BLOCK);
            if(rotation == 1){
                plotsign = new Location(w, corner.getBlockX()+1, corner.getBlockY()+1, corner.getBlockZ()-1).getBlock();
                plotsign.setType(Material.WALL_SIGN);
            }else if(rotation == 4){
                plotsign = new Location(w, corner.getBlockX()+1, corner.getBlockY()+1, corner.getBlockZ()+1).getBlock();
                plotsign.setType(Material.WALL_SIGN);
            }
    
        }else if(rotation == 2 || rotation == 3){
            new Location(w, corner.getBlockX()-1, corner.getBlockY()+1, corner.getBlockZ()).getBlock().setType(Material.DIAMOND_BLOCK);
            if(rotation == 2){
                plotsign = new Location(w, corner.getBlockX()-1, corner.getBlockY()+1, corner.getBlockZ()-1).getBlock();
                plotsign.setType(Material.WALL_SIGN);
            }else if(rotation == 3){
                plotsign = new Location(w, corner.getBlockX()-1, corner.getBlockY()+1, corner.getBlockZ()+1).getBlock();
                plotsign.setType(Material.WALL_SIGN);
            }
        }
        Sign plotSign = (Sign)plotsign.getState();
        org.bukkit.material.Sign s = new org.bukkit.material.Sign(Material.WALL_SIGN);
        if(rotation == 1 || rotation == 2){
            s.setFacingDirection(BlockFace.NORTH);
        }else{
            s.setFacingDirection(BlockFace.SOUTH);
        }
        plotSign.setData(s);
        plotSign.setLine(0, ChatColor.RED + "[ThemedBuild]");
        plotSign.setLine(1, ChatColor.AQUA + DBmanager.curr.getTheme());
        plotSign.setLine(2, ChatColor.GREEN + p.getName());
        plotSign.update();
        p.teleport(getPlotSignLocation());
    }
    public Location getPlotSignLocation() {
        Location l = new Location(plotsign.getWorld(), plotsign.getX()+0.5, plotsign.getY()-1, plotsign.getZ()+0.5);
        if(rotation == 1 || rotation == 2){
            l.setYaw(0);
        }else{
            l.setYaw(180);
        }
        l.setPitch(0);
        return l;
    }
    private void clear() {
        for(int x=Boundx[0]+1; x<Boundx[1]; ++x) {
            for(int z=Boundz[0]+1; z<Boundz[1]; ++z) {
                new Location(w,x,0,z).getBlock().setType(Material.BEDROCK);
                for(int y=1; y<corner.getBlockY()-1; ++y) {
                    new Location(w,x,y,z).getBlock().setType(Material.AIR);
                    new Location(w,x,y,z).getBlock().setType(Material.DIRT);
                }
                new Location(w,x,corner.getBlockY()-1,z).getBlock().setType(Material.AIR);
                new Location(w,x,corner.getBlockY()-1,z).getBlock().setType(Material.GRASS);
                for(int y=corner.getBlockY(); y<256; ++y) {
                    new Location(w,x,y,z).getBlock().setType(Material.AIR);
                }
            }
        }
    }
    public void reset() {
        clear();
        BlockFace modelDirection;
        if(rotation == 1 || rotation == 2) {
            modelDirection = BlockFace.SOUTH;
        }
        else {
            modelDirection = BlockFace.NORTH;
        }
        DBmanager.currModel.generate(new Location(w, Boundx[0]+1, corner.getBlockY()-1, Boundz[0]+1),modelDirection);
    }
    public void unclaim() {
        plotsign.setType(Material.AIR);
        if(rotation == 1 || rotation == 4){
            new Location(w, corner.getBlockX()+1, corner.getBlockY()+1, corner.getBlockZ()).getBlock().setType(Material.AIR);
        }else if(rotation == 2 || rotation == 3){
            new Location(w, corner.getBlockX()-1, corner.getBlockY()+1, corner.getBlockZ()).getBlock().setType(Material.AIR);
        }
        clear();
        DBmanager.curr.getCurrplots().add(this);
        this.p = "";
        assigned = false;
    }
    public boolean isIn(Location l) {
        int x = l.getBlockX();
        int z = l.getBlockZ();
        return (x > Boundx[0] && x < Boundx[1] && z > Boundz[0] && z < Boundz[1]);
    }
}
