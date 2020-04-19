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

import com.mcmiddleearth.pluginutil.LegacyMaterialUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ivan1pl
 */
public class PlotModel implements IPlotModel {

    private final String name;
    private BlockData[][][] model;

    private int sizex;
    private int sizey;
    private int sizez;

    private Location point1 = null;
    private Location point2 = null;

    public PlotModel(String modelname) {
        name = modelname;
        sizex = sizey = sizez = 0;
    }

    public PlotModel(String modelname, File in) {
        name = modelname;
        try {
            Scanner stream = new Scanner(in);
            sizex = Integer.parseInt(stream.nextLine());
            sizey = Integer.parseInt(stream.nextLine());
            sizez = Integer.parseInt(stream.nextLine());
            if (sizex == 0 || sizey == 0 || sizez == 0) {
                model = null;
            } else {
                model = new BlockData[sizex][sizey][sizez];
            }
            for (int x = 0; x < sizex; ++x) {
                for (int y = 0; y < sizey; ++y) {
                    for (int z = 0; z < sizez; ++z) {
                        Material mat = Material.getMaterial("LEGACY_" + stream.nextLine());
                        model[x][y][z] = LegacyMaterialUtil.getBlockData(mat, Byte.parseByte(stream.nextLine()));
                    }
                }
            }
            stream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setPoint1(Location l) {
        point1 = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    @Override
    public void setPoint2(Location l) {
        point2 = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    @Override
    public void saveModel(File out, Player p) {
        throw new UnsupportedOperationException("Saving PlotModels is no longer supported in 1.13."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void setOpposite(Block block) {
        BlockState state = block.getState();
        BlockData data = state.getBlockData();
        if (data instanceof Directional) {
            BlockFace facing = ((Directional) data).getFacing();
            if (facing != BlockFace.DOWN && facing != BlockFace.UP) {
                ((Directional) data).setFacing(facing);
            }
            state.setBlockData(data);
            state.update(true, false);
        }
    }

    @Override
    public void generate(Location l, BlockFace direction) {
        Location corner = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
        Location iter;
        for (int x = 0; x < sizex; ++x) {
            for (int y = 0; y < sizey; ++y) {
                for (int z = 0; z < sizez; ++z) {
                    if (direction == BlockFace.SOUTH) {
                        iter = new Location(corner.getWorld(), corner.getBlockX() + x, corner.getBlockY() + y, corner.getBlockZ() + z);
                        iter.getBlock().setBlockData(model[x][y][z], false);
                    } else if (direction == BlockFace.NORTH) {
                        iter = new Location(corner.getWorld(), corner.getBlockX() + x, corner.getBlockY() + y, corner.getBlockZ() + z);
                        iter.getBlock().setBlockData(model[sizex - x - 1][y][sizez - z - 1], false);
                        setOpposite(iter.getBlock());
                    }
                }
            }
        }
    }

    public static void generateDefaultModel(File out) {
        try {
            FileWriter fos = new FileWriter(out);
            PrintWriter stream = new PrintWriter(fos);
            stream.println(48);
            stream.println(0);
            stream.println(48);
            stream.close();
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSizex() {
        return sizex;
    }

    @Override
    public int getSizez() {
        return sizez;
    }
}
