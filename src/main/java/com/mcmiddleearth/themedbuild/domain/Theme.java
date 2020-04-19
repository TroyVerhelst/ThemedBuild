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

import com.mcmiddleearth.themedbuild.ThemedBuildPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fence;

import java.util.ArrayList;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.Stairs;

/**
 * @author Donovan, Ivan1pl
 */
public class Theme {
    private String theme;
    private Location cent;

    private ArrayList<Plot> plots = new ArrayList<>();
    private ArrayList<Plot> currplots = new ArrayList<>();

    private int x_left;
    private int x_right;

    private String model;

    private String URL = "null";

    public Theme(String name, String url, String model, World w) {
        int sizez = DBmanager.currModel.getSizez();
        int sizez2 = (DBmanager.curr != null ? DBmanager.curr.plots.get(0).getSizeZ() : 0);
        this.theme = name;
        this.model = model;
        Location ocent = (DBmanager.curr != null ? DBmanager.curr.getCent().clone() : w.getSpawnLocation().clone());
        ocent.add(0, 0, sizez + sizez2 + 13);
        this.cent = ocent;
        this.Generate();
    }

    public Theme(String name, String url, Location loc, String model) {
        this.theme = name;
        this.model = model;
        this.cent = loc.add(0, -1, 0);
        this.Generate();
    }

    public Theme(String name, Location loc, ArrayList<Plot> plotz, ArrayList<Plot> currs, int xl, int xr, String model) {
        this.theme = name;
        this.model = model;
        this.cent = loc.add(0, -1, 0);
        this.plots.addAll(plotz);
        this.currplots.addAll(currs);
        this.x_left = xl;
        this.x_right = xr;
    }

    private void genSign(BlockFace face) {
        Location l = getCent().clone();
        if (face == BlockFace.EAST) {
            l.subtract(3, -1, 0);
        } else if (face == BlockFace.WEST) {
            l.add(3, 1, 0);
        }
        Block sign = l.getBlock();
        sign.setType(Material.OAK_SIGN);
        Sign s = (Sign) sign.getBlockData();
        s.setRotation(face);
        sign.setType(Material.OAK_SIGN);
        org.bukkit.block.Sign plotSign = (org.bukkit.block.Sign) sign.getState();
        plotSign.setBlockData(s);
        plotSign.setLine(0, ChatColor.BOLD + "Main");
        plotSign.setLine(1, ChatColor.BOLD + "ThemedBuild");
        plotSign.setLine(2, theme);
        if (theme.length() > 15) {
            plotSign.setLine(3, theme.substring(15));
        }
        plotSign.update(true,false);
    }

    private void Generate() {
        int sizez = DBmanager.currModel.getSizez() + 1;
        Location loc = cent;
        BlockState state;
        Stairs stairs;
        for (int z = loc.getBlockZ() - sizez - 6; z < loc.getBlockZ() + sizez + 7; z++) {
            for (int x = loc.getBlockX() - 1; x < loc.getBlockX() + 2; x++) {
                Location lc = new Location(loc.getWorld(), x, loc.getBlockY(), z);
                lc.getBlock().setType(Material.SANDSTONE);
            }
            Location lc = new Location(loc.getWorld(), loc.getBlockX() - 2, loc.getBlockY(), z);
            lc.getBlock().setType(Material.BRICK_STAIRS);
            state = lc.getBlock().getState();
            stairs = (Stairs) state.getBlockData();
            stairs.setFacing(BlockFace.WEST);
            state.setBlockData(stairs);
            state.update(true,false);
            lc = new Location(loc.getWorld(), loc.getBlockX() + 2, loc.getBlockY(), z);
            lc.getBlock().setType(Material.BRICK_STAIRS);
            state = lc.getBlock().getState();
            stairs = (Stairs) state.getBlockData();
            stairs.setFacing(BlockFace.EAST);
            state.setBlockData(stairs);
            state.update(true,false);
        }
        genSign(BlockFace.EAST);
        genSign(BlockFace.WEST);
        this.genPlots(true);
    }

    private void genGate(World w, int x, int y, int z, BlockFace direction) {
        Fence both = (Fence) Bukkit.createBlockData(Material.OAK_FENCE);
        both.setFace(BlockFace.NORTH, true);
        both.setFace(BlockFace.SOUTH, true);
        both.setFace(direction, true);
        Fence north = (Fence) Bukkit.createBlockData(Material.OAK_FENCE);
        north.setFace(BlockFace.NORTH, true);
        Fence south = (Fence) Bukkit.createBlockData(Material.OAK_FENCE);
        south.setFace(BlockFace.SOUTH, true);
        new Location(w, x, y, z + 1).getBlock().setBlockData(south, false);
        new Location(w, x, y, z + 2).getBlock().setBlockData(north, false);
        new Location(w, x, y + 1, z + 1).getBlock().setBlockData(south, false);
        new Location(w, x, y + 1, z + 2).getBlock().setBlockData(north, false);
        new Location(w, x, y, z - 1).getBlock().setBlockData(north, false);
        new Location(w, x, y, z - 2).getBlock().setBlockData(south, false);
        new Location(w, x, y + 1, z - 1).getBlock().setBlockData(north, false);
        new Location(w, x, y + 1, z - 2).getBlock().setBlockData(south, false);
        new Location(w, x, y + 2, z + 1).getBlock().setBlockData(north, false);
        new Location(w, x, y + 2, z).getBlock().setBlockData(both, false);
        new Location(w, x, y + 2, z - 1).getBlock().setBlockData(south, false);
        BlockData data = Bukkit.createBlockData(Material.OAK_WALL_SIGN);
        Block sign;
        org.bukkit.block.data.type.WallSign s = (org.bukkit.block.data.type.WallSign) data;
        s.setFacing(direction);
        switch (direction) {
            case EAST:
                sign = new Location(w, x + 1, y + 2, z).getBlock();
                break;
            case WEST:
                sign = new Location(w, x - 1, y + 2, z).getBlock();
                break;
            default:
                return;
        }
        sign.setType(Material.OAK_WALL_SIGN);
        org.bukkit.block.Sign plotSign = (org.bukkit.block.Sign) sign.getState();
        plotSign.setBlockData(s);
        //plotSign.setData(s);
        plotSign.setLine(0, "");
        plotSign.setLine(1, "" + ChatColor.RED + ChatColor.BOLD + "Closed");
        plotSign.update(true, false);
    }

    public void close() {
        genGate(getCent().getWorld(), getCent().getBlockX() - 3, getCent().getBlockY() + 1, getCent().getBlockZ(), BlockFace.EAST);
        genGate(getCent().getWorld(), getCent().getBlockX() + 3, getCent().getBlockY() + 1, getCent().getBlockZ(), BlockFace.WEST);
        DBmanager.save();
    }

    public void genPlots(boolean first) {
        Bukkit.getServer().broadcastMessage(ThemedBuildPlugin.prefix + "Generating new plots, Lag incoming");
        Plot p1;
        Plot p2;
        Plot p3;
        Plot p4;
        Block b;
        BlockState state;
        Stairs stairs;
        int sizex = DBmanager.currModel.getSizex() + 1;
        int sizez = DBmanager.currModel.getSizez() + 1;
        if (first) {
            p1 = new Plot(new Location(cent.getWorld(), cent.getBlockX() + 3, cent.getBlockY() + 1, cent.getBlockZ() + 3), 1, sizex, sizez);
            p2 = new Plot(new Location(cent.getWorld(), cent.getBlockX() - 3, cent.getBlockY() + 1, cent.getBlockZ() + 3), 2, sizex, sizez);
            p3 = new Plot(new Location(cent.getWorld(), cent.getBlockX() + 3, cent.getBlockY() + 1, cent.getBlockZ() - 3), 4, sizex, sizez);
            p4 = new Plot(new Location(cent.getWorld(), cent.getBlockX() - 3, cent.getBlockY() + 1, cent.getBlockZ() - 3), 3, sizex, sizez);
            this.x_left = cent.getBlockX() + 3;
            this.x_right = cent.getBlockX() - 3;
        } else {
            this.x_left += (sizex + 5);
            this.x_right -= (sizex + 5);
            p1 = new Plot(new Location(cent.getWorld(), this.x_left, cent.getBlockY() + 1, cent.getBlockZ() + 3), 1, sizex, sizez);
            p2 = new Plot(new Location(cent.getWorld(), this.x_right, cent.getBlockY() + 1, cent.getBlockZ() + 3), 2, sizex, sizez);
            p3 = new Plot(new Location(cent.getWorld(), this.x_left, cent.getBlockY() + 1, cent.getBlockZ() - 3), 4, sizex, sizez);
            p4 = new Plot(new Location(cent.getWorld(), this.x_right, cent.getBlockY() + 1, cent.getBlockZ() - 3), 3, sizex, sizez);
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
        for (int z = cent.getBlockZ() - 1; z < cent.getBlockZ() + 2; z++) {
            for (int x = p1.getCorner().getBlockX() - 1; x < p1.getCorner().getBlockX() + sizex + 4; x++) {
                Location tmp = new Location(cent.getWorld(), x, cent.getBlockY(), z);
                tmp.getBlock().setType(Material.SANDSTONE);
            }
            for (int x = p2.getCorner().getBlockX() + 1; x > p2.getCorner().getBlockX() - sizex - 4; x--) {
                Location tmp = new Location(cent.getWorld(), x, cent.getBlockY(), z);
                tmp.getBlock().setType(Material.SANDSTONE);
            }
        }
        for (int x = p1.getCorner().getBlockX() - 1; x < p1.getCorner().getBlockX() + sizex + 4; x++) {
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ() - 2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getBlockData();
            stairs.setFacing(BlockFace.NORTH);
            state.setBlockData(stairs);
            state.update(true,false);
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ() + 2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getBlockData();
            stairs.setFacing(BlockFace.SOUTH);
            state.setBlockData(stairs);
            state.update(true,false);
        }
        for (int x = p2.getCorner().getBlockX() + 1; x > p2.getCorner().getBlockX() - sizex - 4; x--) {
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ() - 2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getBlockData();
            stairs.setFacing(BlockFace.NORTH);
            state.setBlockData(stairs);
            state.update(true,false);
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ() + 2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getBlockData();
            stairs.setFacing(BlockFace.SOUTH);
            state.setBlockData(stairs);
            state.update(true,false);
        }
    }

    public String getTheme() {
        return theme;
    }

    public Location getCent() {
        return cent;
    }

    public ArrayList<Plot> getPlots() {
        return plots;
    }

    public ArrayList<Plot> getCurrplots() {
        return currplots;
    }

    public int getX_left() {
        return x_left;
    }

    public int getX_right() {
        return x_right;
    }

    public String getModel() {
        return model;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
