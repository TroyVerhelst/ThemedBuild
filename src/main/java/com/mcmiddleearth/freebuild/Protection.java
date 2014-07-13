/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Donovan
 */
public class Protection implements Listener{
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
