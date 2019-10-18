/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.freebuild;

import java.io.File;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public interface IPlotModel {
    
    public String getName();
    public void saveModel(File file, Player player);
    public void generate(Location l, BlockFace direction);
    public int getSizex();
    public int getSizez();
    public void setPoint1(Location loc);
    public void setPoint2(Location loc);
}
