/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
/**
 *
 * @author Donovan, aaldim
 */
public final class Protection implements Listener{
  
@EventHandler
    public void signProtection(BlockBreakEvent event) {
        
        Player p = event.getPlayer();
        Block b = event.getBlock();
        Material sign = b.getType();
        Sign s = (Sign) b.getState();
        
            if (s.getLine(0).equalsIgnoreCase(ChatColor.RED + "[ThemedBuild]")) {
                
                if (sign == Material.SIGN_POST && p.hasPermission("plotmanager.breakPlotSign")){
                    p.sendMessage(ChatColor.RED + "[PlotManager] removed plotsign!");
                }
                else if (sign == Material.SIGN_POST && p.hasPermission("plotmanager.breakPlotSign") == false) {
                    p.sendMessage(ChatColor.RED + "[PlotManager] no permission for that! ask staff to un-claim your plot!");
                    event.setCancelled(true);
                }
            }
                    
        }
    
    }
