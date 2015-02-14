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

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Ivan1pl
 */
public final class Tool implements Listener{
    public static Material ModelTool;
    public static Material liquidTool;
    public static Material fireTool;
    
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
    
    private boolean interactionAllowed(Player pl, Location l) {
        boolean allowed = false;
        if(DBmanager.plots.containsKey(pl.getUniqueId().toString())) {
            ArrayList<Plot> plots = DBmanager.plots.get(pl.getUniqueId().toString());
            for(Plot p : plots) {
                if(p.isIn(l)) {
                    allowed = true;
                    break;
                }
            }
        }
        if(DBmanager.BuildPastPlots && DBmanager.pastPlots.containsKey(pl.getUniqueId().toString())) {
            ArrayList<Plot> plots = DBmanager.pastPlots.get(pl.getUniqueId().toString());
            for(Plot p : plots) {
                if(p.isIn(l)) {
                    allowed = true;
                    break;
                }
            }
        }
        return allowed;
    }
    
    @EventHandler
    public void onPlayerUseLiquidTool(PlayerInteractEvent e) {
        if(!e.isCancelled() && e.getPlayer().getItemInHand().getType() == liquidTool && e.hasBlock()) {
            Location l = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();
            if(interactionAllowed(e.getPlayer(),l)) {
                if(l.getBlock().isEmpty() || l.getBlock().isLiquid()) {
                    if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        l.getBlock().setType(Material.AIR);
                        l.getBlock().setType(Material.LAVA);
                    }
                    else if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        l.getBlock().setType(Material.AIR);
                        l.getBlock().setType(Material.WATER);
                    }
                    BlockState state = l.getBlock().getState();
                    ItemStack is = state.getData().toItemStack();
                    is.setDurability((short) 0);
                    state.setData(is.getData());
                    state.update(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerUseFireTool(PlayerInteractEvent e) {
        if(!e.isCancelled() && e.getPlayer().getItemInHand().getType() == fireTool && e.hasBlock()) {
            Location l = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();
            if(interactionAllowed(e.getPlayer(),l)) {
                if(l.getBlock().getType() == Material.AIR) {
                    if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        l.getBlock().setType(Material.FIRE);
                    }
                }
            }
        }
    }
}
