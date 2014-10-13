/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.freebuild;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Ivan1pl
 */
public final class Tool implements Listener{
    public static Material ModelTool;
    
    @EventHandler
    public void onPlayerUseModelTool(PlayerInteractEvent e){
        if(!e.isCancelled() && e.getPlayer().hasPermission("plotmanager.create") && DBmanager.IncompleteModel != null
                && e.getPlayer().getItemInHand().getType() == ModelTool && e.hasBlock()){
            Action action = e.getAction();
            if(action == Action.LEFT_CLICK_BLOCK){
                DBmanager.IncompleteModel.setPoint1(e.getClickedBlock().getLocation());
                e.getPlayer().sendMessage(Freebuild.prefix + "First point set");
                e.setCancelled(true);
            }
            else if(action == Action.RIGHT_CLICK_BLOCK){
                DBmanager.IncompleteModel.setPoint2(e.getClickedBlock().getLocation());
                e.getPlayer().sendMessage(Freebuild.prefix + "Second point set");
                e.setCancelled(true);
            }
        }
    }
}
