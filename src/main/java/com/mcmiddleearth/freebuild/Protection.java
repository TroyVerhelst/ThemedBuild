/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Donovan, aaldim
 */
public final class Protection implements Listener{
    //on relog cant build
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled())
            return;
        Player p = event.getPlayer();
        Block b = event.getBlock();
        Material mat = b.getType();
        if(mat.equals(Material.SIGN_POST)){
            Sign s = (Sign) b.getState();
            if (s.getLine(0).equalsIgnoreCase(ChatColor.RED + "[ThemedBuild]")) {
                if (mat == Material.SIGN_POST && p.hasPermission("plotmanager.create")){
                    p.sendMessage(ChatColor.RED + "[PlotManager] removed plotsign!");
                }
                else if (mat == Material.SIGN_POST) {
                    p.sendMessage(ChatColor.RED + "[PlotManager] no permission for that! ask staff to un-claim your plot!");
                    event.setCancelled(true);
                }
            }

            }

        Location ploc=b.getLocation();
        boolean canBuild = false;
        if(DBmanager.plots.containsKey(p.getName())){
            List<Plot> pPlots = DBmanager.plots.get(p.getName());
            for(Plot plot : pPlots){
                canBuild = ploc.getWorld().equals(plot.getW())&&((ploc.getBlockX()<plot.Boundx[1] && ploc.getBlockX()>plot.Boundx[0])&&(ploc.getBlockZ()<plot.Boundz[1] && ploc.getBlockZ()>plot.Boundz[0]));
            }
        }
        if(!canBuild){
//            if(!p.hasPermission("plotmanager.create"))
                event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if(e.isCancelled())
            return;
        Player p = e.getPlayer();
        Block b = e.getBlock();
        Location ploc=b.getLocation();
        boolean canBuild = false;
        if(DBmanager.plots.containsKey(p.getName())){
            List<Plot> pPlots = DBmanager.plots.get(p.getName());
            for(Plot plot : pPlots){
                canBuild = ploc.getWorld().equals(plot.getW())&&((ploc.getBlockX()<plot.Boundx[1] && ploc.getBlockX()>plot.Boundx[0])&&(ploc.getBlockZ()<plot.Boundz[1] && ploc.getBlockZ()>plot.Boundz[0]));
            }
        }
        if(!canBuild){
//            if(!p.hasPermission("plotmanager.create"))
                e.setCancelled(true);
        }
    }
}
