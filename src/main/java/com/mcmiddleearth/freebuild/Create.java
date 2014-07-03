/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan
 */
public class Create implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player p = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("theme")){
            if(args.length == 0){
                //move the player and gen new plot
            }else if(args[0].equalsIgnoreCase("new")&&args.length>1){
                //create new theme location afer old one
                String tname = "";
                for(String s : args){
                    if(!s.equalsIgnoreCase("new")){
                        tname += s;
                    }
                }
                Theme theme = new Theme(tname);
                DBmanager.Themes.put(tname, theme);
                return true;
            }else if(args[0].equalsIgnoreCase("set")){
                //set and generate a theme with player at center
                String tname = "";
                for(String s : args){
                    if(!s.equalsIgnoreCase("set")){
                        tname += s;
                    }
                }
                Theme theme = new Theme(tname, p.getLocation());
                DBmanager.Themes.put(tname, theme);
                return true;
            }
        }
        return false;
    }
}
