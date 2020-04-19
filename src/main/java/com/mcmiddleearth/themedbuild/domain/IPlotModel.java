/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.themedbuild.domain;

import java.io.File;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public interface IPlotModel {
    
    String getName();
    void saveModel(File file, Player player);
    void generate(Location l, BlockFace direction);
    int getSizex();
    int getSizez();
    void setPoint1(Location loc);
    void setPoint2(Location loc);
}
