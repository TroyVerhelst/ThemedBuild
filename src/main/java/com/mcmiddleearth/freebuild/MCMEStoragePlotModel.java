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

import com.mcmiddleearth.pluginutil.plotStoring.IStoragePlot;
import com.mcmiddleearth.pluginutil.plotStoring.InvalidRestoreDataException;
import com.mcmiddleearth.pluginutil.plotStoring.MCMEPlotFormat;
import com.mcmiddleearth.pluginutil.plotStoring.StoragePlotSnapshot;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class MCMEStoragePlotModel implements IStoragePlot, IPlotModel {
    
    private byte[] nbtData;
    
    @Getter
    private final String name;

    @Getter
    private int sizex;
    private int sizey;
    @Getter
    private int sizez;
    
    private Location point1 = null;
    private Location point2 = null;
    
    public MCMEStoragePlotModel(String modelname){
        name = modelname;
        sizex = sizey = sizez = 0;
    }
    public MCMEStoragePlotModel(String modelname, File inFile){
        name = modelname;
        try(DataInputStream in = new DataInputStream(
                                 new BufferedInputStream(
                                 new GZIPInputStream(
                                 new FileInputStream(inFile))))) {
            Vector size = new MCMEPlotFormat().readSize(in);
            in.close();
            sizex = size.getBlockX();
            sizey = size.getBlockY();
            sizez = size.getBlockZ();
Logger.getGlobal().log(Level.INFO, "Found Size: {0} {1} {2}", new Object[]{sizex,sizey,sizez});
        } catch (IOException ex) {
            Logger.getLogger(MCMEStoragePlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try(BufferedInputStream in = new BufferedInputStream(
                                 new GZIPInputStream(
                                 new FileInputStream(inFile)));
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int readBytes = 0;
            do {
                readBytes = in.read(buffer,0,buffer.length);
                out.write(buffer, 0, readBytes);
            } while(readBytes == buffer.length);
            nbtData = out.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(MCMEStoragePlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*try {
            Scanner stream = new Scanner(in);
            sizex = Integer.parseInt(stream.nextLine());
            sizey = Integer.parseInt(stream.nextLine());
            sizez = Integer.parseInt(stream.nextLine());
            if(sizex == 0 || sizey == 0 || sizez == 0){
                model = null;
            }
            else{
                model = new BlockData[sizex][sizey][sizez];
            }
            for(int x = 0; x < sizex; ++x){
                for(int y = 0; y < sizey; ++y){
                    for(int z = 0; z < sizez; ++z){
                        Material mat = Material.getMaterial("LEGACY_"+stream.nextLine());
                        model[x][y][z] = LegacyMaterialUtil.getBlockData(mat,Byte.parseByte(stream.nextLine()));
                                //new ItemStack(mat,1,Short.parseShort(stream.nextLine()));
                    }
                }
            }
            stream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MCMEStoragePlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    @Override
    public void setPoint1(Location l){
        point1 = new Location(l.getWorld(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
    }
    @Override
    public void setPoint2(Location l){
        point2 = new Location(l.getWorld(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
    }
    
    @Override
    public void saveModel(final File out, final Player p){
        if(point1 != null && point2 != null && point1.getWorld().getName().equals(point2.getWorld().getName())){
            sizex = Math.abs(point1.getBlockX()-point2.getBlockX())+1;
            sizey = Math.abs(point1.getBlockY()-point2.getBlockY())+1;
            sizez = Math.abs(point1.getBlockZ()-point2.getBlockZ())+1;
Logger.getGlobal().log(Level.INFO, "Size: {0} {1} {2}", new Object[]{sizex,sizey,sizez});
            final IStoragePlot plot = this;
Logger.getGlobal().log(Level.INFO, "Low: {0} {1} {2}", new Object[]{plot.getLowCorner().getBlock(),
                                                                    plot.getLowCorner().getBlockY(),
                                                                    plot.getLowCorner().getBlockZ()});
Logger.getGlobal().log(Level.INFO, "High: {0} {1} {2}", new Object[]{plot.getHighCorner().getBlock(),
                                                                    plot.getHighCorner().getBlockY(),
                                                                    plot.getHighCorner().getBlockZ()});
            p.sendMessage(Freebuild.prefix + "Getting blocks...");
            final StoragePlotSnapshot snapshot = new StoragePlotSnapshot(plot);
            p.sendMessage(Freebuild.prefix + "Done");
            p.sendMessage(Freebuild.prefix + "Saving model...");
            try(ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream outStream = new DataOutputStream(
                                       new BufferedOutputStream(
                                       new GZIPOutputStream(
                                       byteOut)))) {
                new MCMEPlotFormat().save(plot, outStream, snapshot);
                outStream.flush();
                outStream.close();
                nbtData = byteOut.toByteArray();
Logger.getGlobal().log(Level.INFO,"Bytes: "+nbtData.length);
                try(ByteArrayInputStream byteIn = new ByteArrayInputStream(nbtData);
                    FileOutputStream fileOut = new FileOutputStream(out)) {
                    byte[] buffer = new byte[1024];
                    int readBytes = 0;
                    int sum = 0;
                    do {
                        readBytes = byteIn.read(buffer,0,buffer.length);
                        sum = sum + readBytes;
                        fileOut.write(buffer, 0, readBytes);
                    } while(readBytes == buffer.length);
                    fileOut.close();
                    byteIn.close();
Logger.getGlobal().info("Written bytes: "+sum);                    
                }
                p.sendMessage(Freebuild.prefix + "Done");
            } catch (IOException ex) {
                Logger.getLogger(Plot.class.getName()).log(Level.SEVERE, null, ex);
                p.sendMessage(Freebuild.prefix+ChatColor.RED+"Error while storing plot data: "+ex.getMessage());
            }
        } else{
            if(p != null){
                p.sendMessage(Freebuild.prefix + "You have to set both points");
            }
        }
    }
    
    /*private static void setOpposite(Block block){
        BlockState state = block.getState();
        BlockData data=state.getBlockData();
        if (data != null && data instanceof Directional) {
            BlockFace facing = ((Directional)data).getFacing();
            if(facing != BlockFace.DOWN && facing != BlockFace.UP) {
                ((Directional)data).setFacing(facing);
            }
            state.setBlockData(data);
            state.update(true,false);
        }
    }*/
    
    @Override
    public void generate(Location l, BlockFace direction){
        final Location corner = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
        final int rotations = (direction.equals(BlockFace.NORTH)?0:2);
        new BukkitRunnable() {
            @Override
            public void run() {
                try(DataInputStream in = new DataInputStream(new ByteArrayInputStream(nbtData))) {
                    new MCMEPlotFormat().load(corner, rotations, null, in);
                } catch (IOException | InvalidRestoreDataException ex) {
                    Logger.getLogger(MCMEStoragePlotModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.runTaskLater(Freebuild.getPluginInstance(),15);
        /*Location iter;
        for(int x = 0; x < sizex; ++x){
        for(int y = 0; y < sizey; ++y){
        for(int z = 0; z < sizez; ++z){
        if(direction == BlockFace.SOUTH) {
        iter = new Location(corner.getWorld(),corner.getBlockX()+x,corner.getBlockY()+y,corner.getBlockZ()+z);
        iter.getBlock().setBlockData(model[x][y][z],false);
        /*BlockState state = iter.getBlock().getState();
        state.setData(model[x][y][z].getData());
        state.update(true); ///*
        }
        else if(direction == BlockFace.NORTH) {
        iter = new Location(corner.getWorld(),corner.getBlockX()+x,corner.getBlockY()+y,corner.getBlockZ()+z);
        iter.getBlock().setBlockData(model[sizex-x-1][y][sizez-z-1],false);
        /*BlockState state = iter.getBlock().getState();
        state.setData(model[sizex-x-1][y][sizez-z-1].getData());
        state.update(true);//*
        setOpposite(iter.getBlock());
        }
        }
        }
        }*/ 
        /*Location iter;
        for(int x = 0; x < sizex; ++x){
            for(int y = 0; y < sizey; ++y){
                for(int z = 0; z < sizez; ++z){
                    if(direction == BlockFace.SOUTH) {
                        iter = new Location(corner.getWorld(),corner.getBlockX()+x,corner.getBlockY()+y,corner.getBlockZ()+z);
                        iter.getBlock().setBlockData(model[x][y][z],false);
                        /*BlockState state = iter.getBlock().getState();
                        state.setData(model[x][y][z].getData());
                        state.update(true); ///*
                    }
                    else if(direction == BlockFace.NORTH) {
                        iter = new Location(corner.getWorld(),corner.getBlockX()+x,corner.getBlockY()+y,corner.getBlockZ()+z);
                        iter.getBlock().setBlockData(model[sizex-x-1][y][sizez-z-1],false);
                        /*BlockState state = iter.getBlock().getState();
                        state.setData(model[sizex-x-1][y][sizez-z-1].getData());
                        state.update(true);//*
                        setOpposite(iter.getBlock());
                    }
                }
            }
        }*/
    }
    
    public static void generateDefaultModel(File out){
        try {
            FileWriter fos = new FileWriter(out);
            PrintWriter stream = new PrintWriter(fos);
            stream.println(48);
            stream.println(0);
            stream.println(48);
            stream.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MCMEStoragePlotModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MCMEStoragePlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public World getWorld() {
        return point1.getWorld();
    }

    @Override
    public Location getLowCorner() {
        return new Location(point1.getWorld(),Math.min(point1.getBlockX(),point2.getBlockX()),
                                              Math.min(point1.getBlockY(),point2.getBlockY()),
                                              Math.min(point1.getBlockZ(),point2.getBlockZ()));
    }

    @Override
    public Location getHighCorner() {
        return new Location(point1.getWorld(),Math.max(point1.getBlockX(),point2.getBlockX()),
                                              Math.max(point1.getBlockY(),point2.getBlockY()),
                                              Math.max(point1.getBlockZ(),point2.getBlockZ()));
    }

    
    @Override
    public boolean isInside(Location lctn) {
        return point1!=null && point2!=null
                && lctn.getWorld().equals(point1.getWorld())
                && lctn.getBlockX()>=getLowCorner().getBlockX() && lctn.getBlockX()<=getHighCorner().getBlockX()
                && lctn.getBlockY()>=getLowCorner().getBlockY() && lctn.getBlockY()<=getHighCorner().getBlockY()
                && lctn.getBlockZ()>=getLowCorner().getBlockZ() && lctn.getBlockZ()<=getHighCorner().getBlockZ();
    }
}
