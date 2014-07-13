/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

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
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        List<Plot> pPlots = DBmanager.plots.get(p.getName());
        Location ploc=p.getLocation();
        boolean canBuild = true;
        for(Plot plot : pPlots){
            if(!(ploc.getBlockX()<plot.Boundx[1] && ploc.getBlockX()>plot.Boundx[0])&&(ploc.getBlockZ()<plot.Boundz[1] && ploc.getBlockZ()>plot.Boundz[0])){
                canBuild = false;
            }
        }
        if(!canBuild){
            //stop the building here
            //you can add the above to any event to stop it
        }
    }
}
