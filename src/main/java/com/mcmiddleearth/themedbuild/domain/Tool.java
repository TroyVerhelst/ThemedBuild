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

import com.mcmiddleearth.themedbuild.ThemedBuildPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 *
 * @author Ivan1pl
 */
public final class Tool implements Listener{
    
    private static Material modelTool;
    private static Material liquidTool;
    private static Material fireTool;
    
    @EventHandler
    public void onPlayerUseModelTool(PlayerInteractEvent e){
        if(!e.isCancelled() && e.getPlayer().hasPermission("plotmanager.create") && DBmanager.IncompleteModel != null
                && e.getPlayer().getItemInHand().getType() == getModelTool() && e.hasBlock()){
            Action action = e.getAction();
            if(action == Action.LEFT_CLICK_BLOCK){
                DBmanager.IncompleteModel.setPoint1(e.getClickedBlock().getLocation());
                e.getPlayer().sendMessage(ThemedBuildPlugin.prefix + "First point set");
                e.setCancelled(true);
            }
            else if(action == Action.RIGHT_CLICK_BLOCK){
                DBmanager.IncompleteModel.setPoint2(e.getClickedBlock().getLocation());
                e.getPlayer().sendMessage(ThemedBuildPlugin.prefix + "Second point set");
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
        if(!e.isCancelled() && e.getPlayer().getItemInHand().getType() == getLiquidTool() && e.hasBlock()) {
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
        if(!e.isCancelled() && e.getPlayer().getItemInHand().getType() == getFireTool() && e.hasBlock()) {
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
    
    public static Material getFireTool() {
        return (fireTool!=null?fireTool:Material.IRON_SWORD);
    }
    public static Material getLiquidTool() {
        return (liquidTool!=null?liquidTool:Material.STONE_SWORD);
    }
    public static Material getModelTool() {
        return (modelTool!=null?modelTool:Material.WOODEN_SWORD);
    }

    public static void setModelTool(Material modelTool) {
        Tool.modelTool = modelTool;
    }

    public static void setLiquidTool(Material liquidTool) {
        Tool.liquidTool = liquidTool;
    }

    public static void setFireTool(Material fireTool) {
        Tool.fireTool = fireTool;
    }
}
