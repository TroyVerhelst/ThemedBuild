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

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * @author Donovan, aaldim
 */
public class Plot {
    private String p;

    private boolean assigned;

    private Location corner;

    public int[] boundZ = {0, 0};
    public int[] boundX = {0, 0};

    private int rotation;
    private World w;

    private int sizeX = 50;
    private int sizeZ = 50;

    Block plotsign;

    public Plot(Location corner, int rot, int sx, int sz) {
        sizeX = sx;
        sizeZ = sz;
        assigned = false;
        this.corner = new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ());
        if (rot == 1) {
            this.boundZ[0] = corner.getBlockZ();
            this.boundZ[1] = corner.getBlockZ() + sizeZ;
            this.boundX[0] = corner.getBlockX();
            this.boundX[1] = corner.getBlockX() + sizeX;
        } else if (rot == 2) {
            this.boundZ[0] = corner.getBlockZ();
            this.boundZ[1] = corner.getBlockZ() + sizeZ;
            this.boundX[0] = corner.getBlockX() - sizeX;
            this.boundX[1] = corner.getBlockX();
        } else if (rot == 3) {
            this.boundZ[0] = corner.getBlockZ() - sizeZ;
            this.boundZ[1] = corner.getBlockZ();
            this.boundX[0] = corner.getBlockX() - sizeX;
            this.boundX[1] = corner.getBlockX();
        } else if (rot == 4) {
            this.boundZ[0] = corner.getBlockZ() - sizeZ;
            this.boundZ[1] = corner.getBlockZ();
            this.boundX[0] = corner.getBlockX();
            this.boundX[1] = corner.getBlockX() + sizeX;
        }
        this.w = corner.getWorld();
        this.rotation = rot;
        Generate();
    }

    public Plot(Location corner, int rot, boolean assigned, String owner, int sx, int sz) {
        sizeX = sx;
        sizeZ = sz;
        this.assigned = false;
        this.corner = new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ());
        if (rot == 1) {
            this.boundZ[0] = corner.getBlockZ();
            this.boundZ[1] = corner.getBlockZ() + sizeZ;
            this.boundX[0] = corner.getBlockX();
            this.boundX[1] = corner.getBlockX() + sizeX;
        } else if (rot == 2) {
            this.boundZ[0] = corner.getBlockZ();
            this.boundZ[1] = corner.getBlockZ() + sizeZ;
            this.boundX[0] = corner.getBlockX() - sizeX;
            this.boundX[1] = corner.getBlockX();
        } else if (rot == 3) {
            this.boundZ[0] = corner.getBlockZ() - sizeZ;
            this.boundZ[1] = corner.getBlockZ();
            this.boundX[0] = corner.getBlockX() - sizeX;
            this.boundX[1] = corner.getBlockX();
        } else if (rot == 4) {
            this.boundZ[0] = corner.getBlockZ() - sizeZ;
            this.boundZ[1] = corner.getBlockZ();
            this.boundX[0] = corner.getBlockX();
            this.boundX[1] = corner.getBlockX() + sizeX;
        }
        this.w = corner.getWorld();
        this.rotation = rot;
        this.assigned = assigned;
        if (assigned) {
            this.p = owner;
        }
        if (rotation == 1) {
            plotsign = new Location(w, corner.getBlockX() + 1, corner.getBlockY() + 1, corner.getBlockZ() - 1).getBlock();
        } else if (rotation == 4) {
            plotsign = new Location(w, corner.getBlockX() + 1, corner.getBlockY() + 1, corner.getBlockZ() + 1).getBlock();
        } else if (rotation == 2) {
            plotsign = new Location(w, corner.getBlockX() - 1, corner.getBlockY() + 1, corner.getBlockZ() - 1).getBlock();
        } else if (rotation == 3) {
            plotsign = new Location(w, corner.getBlockX() - 1, corner.getBlockY() + 1, corner.getBlockZ() + 1).getBlock();
        }
    }

    private void Generate() {
        BlockData borderBlockData = Bukkit.createBlockData(Material.STONE_SLAB);
        ((Slab) borderBlockData).setType(Slab.Type.DOUBLE);
        for (int x = boundX[0]; x < boundX[1]; x++) {
            Location lc = new Location(w, x, corner.getBlockY(), boundZ[0]);
            lc.getBlock().setBlockData(borderBlockData);
        }
        for (int x = boundX[0]; x < boundX[1]; x++) {
            Location lc = new Location(w, x, corner.getBlockY(), boundZ[1]);
            lc.getBlock().setBlockData(borderBlockData);
        }
        for (int z = boundZ[0]; z < boundZ[1]; z++) {
            Location lc = new Location(w, boundX[0], corner.getBlockY(), z);
            lc.getBlock().setBlockData(borderBlockData);
        }
        for (int z = boundZ[0]; z < boundZ[1]; z++) {
            Location lc = new Location(w, boundX[1], corner.getBlockY(), z);
            lc.getBlock().setBlockData(borderBlockData);
        }
        new Location(w, boundX[1], corner.getBlockY(), boundZ[1]).getBlock().setBlockData(borderBlockData);
    }

    public void assign(Player p) {
        DBmanager.curr.getCurrplots().remove(this);
        this.p = p.getUniqueId().toString();
        assigned = true;
        BlockFace modelDirection;
        if (rotation == 1 || rotation == 2) {
            modelDirection = BlockFace.SOUTH;
        } else {
            modelDirection = BlockFace.NORTH;
        }
        DBmanager.currModel.generate(new Location(w, boundX[0] + 1, corner.getBlockY() - 1, boundZ[0] + 1), modelDirection);
        if (DBmanager.plots.containsKey(p.getUniqueId().toString())) {
            ArrayList<Plot> ps = DBmanager.plots.get(p.getUniqueId().toString());
            ps.add(this);
            DBmanager.plots.put(p.getUniqueId().toString(), ps);
        } else {
            ArrayList<Plot> ps = new ArrayList<>();
            ps.add(this);
            DBmanager.plots.put(p.getUniqueId().toString(), ps);
        }
        if (rotation == 1 || rotation == 4) {
            new Location(w, corner.getBlockX() + 1, corner.getBlockY() + 1, corner.getBlockZ()).getBlock().setType(Material.GOLD_ORE);
            if (rotation == 1) {
                plotsign = new Location(w, corner.getBlockX() + 1, corner.getBlockY() + 1, corner.getBlockZ() - 1).getBlock();
                plotsign.setType(Material.OAK_WALL_SIGN);
            } else if (rotation == 4) {
                plotsign = new Location(w, corner.getBlockX() + 1, corner.getBlockY() + 1, corner.getBlockZ() + 1).getBlock();
                plotsign.setType(Material.OAK_WALL_SIGN);
            }
        } else if (rotation == 2 || rotation == 3) {
            new Location(w, corner.getBlockX() - 1, corner.getBlockY() + 1, corner.getBlockZ()).getBlock().setType(Material.GOLD_ORE);
            if (rotation == 2) {
                plotsign = new Location(w, corner.getBlockX() - 1, corner.getBlockY() + 1, corner.getBlockZ() - 1).getBlock();
                plotsign.setType(Material.OAK_WALL_SIGN);
            } else if (rotation == 3) {
                plotsign = new Location(w, corner.getBlockX() - 1, corner.getBlockY() + 1, corner.getBlockZ() + 1).getBlock();
                plotsign.setType(Material.OAK_WALL_SIGN);
            }
        }
        WallSign plotSign = (WallSign) Bukkit.createBlockData(Material.OAK_WALL_SIGN);
        if (rotation == 1 || rotation == 2) {
            plotSign.setFacing(BlockFace.NORTH);
        } else {
            plotSign.setFacing(BlockFace.SOUTH);
        }
        plotsign.setBlockData(plotSign, false);
        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) plotsign.getState();
        sign.setLine(0, ChatColor.RED + "[ThemedBuild]");
        sign.setLine(1, ChatColor.AQUA + DBmanager.curr.getTheme());
        sign.setLine(2, ChatColor.GREEN + p.getName());
        sign.update(true, false);
        p.teleport(getPlotSignLocation());
    }

    public Location getPlotSignLocation() {
        Location l = new Location(plotsign.getWorld(), plotsign.getX() + 0.5, plotsign.getY() - 1, plotsign.getZ() + 0.5);
        if (rotation == 1 || rotation == 2) {
            l.setYaw(0);
        } else {
            l.setYaw(180);
        }
        l.setPitch(0);
        return l;
    }

    private void clear() {
        for (int x = boundX[0] + 1; x < boundX[1]; ++x) {
            for (int z = boundZ[0] + 1; z < boundZ[1]; ++z) {
                new Location(w, x, 0, z).getBlock().setType(Material.BEDROCK);
                for (int y = 1; y < corner.getBlockY() - 1; ++y) {
                    new Location(w, x, y, z).getBlock().setType(Material.AIR);
                    new Location(w, x, y, z).getBlock().setType(Material.DIRT);
                }
                new Location(w, x, corner.getBlockY() - 1, z).getBlock().setType(Material.AIR);
                new Location(w, x, corner.getBlockY() - 1, z).getBlock().setType(Material.GRASS_BLOCK);
                for (int y = corner.getBlockY(); y < 256; ++y) {
                    new Location(w, x, y, z).getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public void reset() {
        clear();
        BlockFace modelDirection;
        if (rotation == 1 || rotation == 2) {
            modelDirection = BlockFace.SOUTH;
        } else {
            modelDirection = BlockFace.NORTH;
        }
        DBmanager.currModel.generate(new Location(w, boundX[0] + 1, corner.getBlockY() - 1, boundZ[0] + 1), modelDirection);
    }

    public void unclaim() {
        plotsign.setType(Material.AIR);
        if (rotation == 1 || rotation == 4) {
            new Location(w, corner.getBlockX() + 1, corner.getBlockY() + 1, corner.getBlockZ()).getBlock().setType(Material.AIR);
        } else if (rotation == 2 || rotation == 3) {
            new Location(w, corner.getBlockX() - 1, corner.getBlockY() + 1, corner.getBlockZ()).getBlock().setType(Material.AIR);
        }
        clear();
        if (DBmanager.plots.get(p) != null && DBmanager.plots.get(p).contains(this)) {
            DBmanager.curr.getCurrplots().add(this);
        }
        this.p = "";
        assigned = false;
    }

    public boolean isIn(Location l) {
        int x = l.getBlockX();
        int z = l.getBlockZ();
        return (x > boundX[0] && x < boundX[1] && z > boundZ[0] && z < boundZ[1]);
    }

    public String getP() {
        return p;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public Location getCorner() {
        return corner;
    }

    public int getRotation() {
        return rotation;
    }

    public World getW() {
        return w;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeZ() {
        return sizeZ;
    }
}
