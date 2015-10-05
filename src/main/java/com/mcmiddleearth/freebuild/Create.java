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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Donovan, Ivan1pl
 */
public class Create implements CommandExecutor, ConversationAbandonedListener{
    private final ConversationFactory conversationFactory;
    
    public String tname;
    public String type;
    private Location ploc;
    
    public Create(){
        conversationFactory = new ConversationFactory(Freebuild.getPluginInstance())
                .withLocalEcho(false)
                .withModality(true)
                .withFirstPrompt(new setTitle())
                .withTimeout(600)
                .thatExcludesNonPlayersWithMessage(Freebuild.prefix + "You must be a player to send this command");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player p = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("theme")){
            if(args.length == 0){
                if(!DBmanager.InfinitePlotsPerPlayer && DBmanager.plots.containsKey(p.getUniqueId().toString())
                        && DBmanager.MaxPlotsPerPlayer <= DBmanager.plots.get(p.getUniqueId().toString()).size()){
                    p.sendMessage(Freebuild.prefix + "You reached maximum number of plots");
                    return true;
                }
                //move the player and gen new plot
                for(Plot plot : DBmanager.curr.getCurrplots()){
                    if(!plot.isAssigned()){
                        plot.assign(p);
                        p.sendMessage(Freebuild.prefix + "Welcome to a new plot, the current theme is " + DBmanager.curr.getTheme());
                        if(!DBmanager.curr.getURL().equals("null"))
                        {
                            p.sendMessage(Freebuild.prefix + "More information about this Themedbuild:");
                            p.sendMessage(ChatColor.GRAY + DBmanager.curr.getURL());
                        }
                        p.sendMessage(ChatColor.WHITE + "To place lava, left click with " + ChatColor.GREEN + Tool.liquidTool);
                        p.sendMessage(ChatColor.WHITE + "To place water, right click with " + ChatColor.GREEN + Tool.liquidTool);
                        p.sendMessage(ChatColor.WHITE + "To place fire, right click with " + ChatColor.GREEN + Tool.fireTool);
                        return true;
                    }
                }
                DBmanager.curr.genPlots(false);
                for(Plot plot : DBmanager.curr.getCurrplots()){
                    if(!plot.isAssigned()){
                        plot.assign(p);
                        p.teleport(plot.getCorner());
                        p.sendMessage(Freebuild.prefix + "Welcome to a new plot, the current theme is " + DBmanager.curr.getTheme());
                        if(!DBmanager.curr.getURL().equals("null"))
                        {
                            p.sendMessage(Freebuild.prefix + "More information about this Themedbuild:");
                            p.sendMessage(ChatColor.GRAY + DBmanager.curr.getURL());
                        }
                        p.sendMessage(ChatColor.WHITE + "To place lava, left click with " + ChatColor.GREEN + Tool.liquidTool);
                        p.sendMessage(ChatColor.WHITE + "To place water, right click with " + ChatColor.GREEN + Tool.liquidTool);
                        p.sendMessage(ChatColor.WHITE + "To place fire, right click with " + ChatColor.GREEN + Tool.fireTool);
                        return true;
                    }
                }
            }
            else if(args[0].equalsIgnoreCase("toplot")){
                if(!DBmanager.plots.containsKey(p.getUniqueId().toString())) {
                    p.sendMessage(Freebuild.prefix + "You don't have claimed plots in current theme");
                    return true;
                }
                ArrayList<Plot> plots = DBmanager.plots.get(p.getUniqueId().toString());
                int nplots = plots.size();
                if(nplots > 0) {
                    p.teleport(plots.get(nplots-1).getPlotSignLocation());
                }
                else {
                    p.sendMessage(Freebuild.prefix + "You don't have claimed plots in current theme");
                }
                return true;
            }
            else if(args[0].equalsIgnoreCase("warp")){
                if(DBmanager.curr != null){
                    Location cent = DBmanager.curr.getCent();
                    p.teleport(new Location(cent.getWorld(), cent.getBlockX(), cent.getBlockY()+1, cent.getBlockZ()));
                    p.sendMessage("Teleported to current theme!");
                }else{
                    p.sendMessage("There is no current theme.");
                }
            }
            else if(args[0].equalsIgnoreCase("resetplot")){
                if(!DBmanager.plots.containsKey(p.getUniqueId().toString())) {
                    p.sendMessage(Freebuild.prefix + "You don't have claimed plots in current theme");
                    return true;
                }
                ArrayList<Plot> plots = DBmanager.plots.get(p.getUniqueId().toString());
                int nplots = plots.size();
                if(nplots > 0) {
                    for(Plot pl : plots) {
                        if(pl.isIn(p.getLocation())) {
                            pl.reset();
                            p.sendMessage(Freebuild.prefix + "Plot restored to original state");
                            return true;
                        }
                    }
                }
                p.sendMessage(Freebuild.prefix + "You can only reset your own plot");
                return true;
            }
            else if(args[0].equalsIgnoreCase("unclaim")&&p.hasPermission("plotmanager.create")){
                Set<String> keys = DBmanager.plots.keySet();
                List<Plot> pPlots;
                for(String k : keys){
                    pPlots = DBmanager.plots.get(k);
                    for(Plot plot : pPlots){
                        if(plot.isIn(p.getLocation())) {
                            plot.unclaim();
                            pPlots.remove(plot);
                            p.sendMessage(Freebuild.prefix + "Plot unclaimed");
                            return true;
                        }
                    }
                }
                p.sendMessage(Freebuild.prefix + "This plot isn't claimed");
                return true;
            }
            else if(args[0].equalsIgnoreCase("new")&&p.hasPermission("plotmanager.create")){
                if(Freebuild.getPluginInstance().getConfig().getStringList("ValidWorlds").contains(p.getWorld().getName())){
                    //create new theme
                    p.sendMessage(Freebuild.prefix + "Generating...");
                    String tname = "";
                    String modelname = "default";
                    int namebegin = 1;
                    if(args.length == 1){
                        return false;
                    }
                    if(args[1].equals("-m") && args.length > 3){
                        modelname = args[2];
                        namebegin = 3;
                        if(!DBmanager.modelExists(modelname)){
                            p.sendMessage(Freebuild.prefix + "Model '"+modelname+"' doesn't exist");
                            p.sendMessage(Freebuild.prefix + "To see available models, use /theme listmodels");
                            return true;
                        }
                    }
                    for(String s : Arrays.asList(args).subList(namebegin, args.length)){
                        tname += s + " ";
                    }
                    tname = tname.trim();
                    if(tname.length() > 30) {
                        p.sendMessage(Freebuild.prefix + "Provided name is too long");
                        return true;
                    }
                    DBmanager.currModel = DBmanager.loadPlotModel(modelname);
                    Theme theme = new Theme(tname, " ", modelname);
                    DBmanager.Themes.put(tname, theme);
                    DBmanager.curr.close();
                    DBmanager.curr = theme;
                    Set<String> owners = DBmanager.plots.keySet();
                    for(String s: owners){
                        ArrayList<Plot> pl = DBmanager.plots.get(s);
                        if(DBmanager.pastPlots.containsKey(s)){
                            ArrayList<Plot> ppl = DBmanager.pastPlots.get(s);
                            ppl.addAll(pl);
                        }else{
                            DBmanager.pastPlots.put(s, pl);
                        }
                    }
                    DBmanager.plots = new HashMap<>();
                    p.teleport(new Location(theme.getCent().getWorld(), theme.getCent().getX(), theme.getCent().getY()+1, theme.getCent().getZ()));
    //                type = args[0];
    //                ploc = p.getLocation();
    //                conversationFactory.buildConversation((Conversable) sender).begin();
    ////                p.teleport(DBmanager.curr.getCent());
                    return true;
                }else{
                    p.sendMessage(Freebuild.prefix + "You crazy... you cant make plots here!!!!");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("set")&&p.hasPermission("plotmanager.create") && DBmanager.curr == null){
                if(Freebuild.getPluginInstance().getConfig().getStringList("ValidWorlds").contains(p.getWorld().getName())){
                    //set and generate a theme with player at center
                    p.sendMessage(Freebuild.prefix + "Generating...");
                    String tname = "";
                    String modelname = "default";
                    int namebegin = 1;
                    if(args.length == 1){
                        return false;
                    }
                    if(args[1].equals("-m") && args.length > 3){
                        modelname = args[2];
                        namebegin = 3;
                        if(!DBmanager.modelExists(modelname)){
                            p.sendMessage(Freebuild.prefix + "Model '"+modelname+"' doesn't exist");
                            p.sendMessage(Freebuild.prefix + "To see available models, use /theme listmodels");
                            return true;
                        }
                    }
                    for(String s : Arrays.asList(args).subList(namebegin, args.length)){
                        tname += s + " ";
                    }
                    tname = tname.trim();
                    if(tname.length() > 30) {
                        p.sendMessage(Freebuild.prefix + "Provided name is too long");
                        return true;
                    }
                    DBmanager.currModel = DBmanager.loadPlotModel(modelname);
                    Theme theme = new Theme(tname, " ", p.getLocation(), modelname);
                    DBmanager.Themes.put(tname, theme);
                    if(DBmanager.curr != null) {
                        DBmanager.curr.close();
                    }
                    DBmanager.curr = theme;
                    Set<String> owners = DBmanager.plots.keySet();
                    for(String s: owners){
                        ArrayList<Plot> pl = DBmanager.plots.get(s);
                        if(DBmanager.pastPlots.containsKey(s)){
                            ArrayList<Plot> ppl = DBmanager.pastPlots.get(s);
                            ppl.addAll(pl);
                        }else{
                            DBmanager.pastPlots.put(s, pl);
                        }
                    }
                    DBmanager.plots = new HashMap<>();
                    return true;
                }else{
                    p.sendMessage(Freebuild.prefix + "You crazy... you cant make plots here!!!!");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("createmodel") && p.hasPermission("plotmanager.create")){
                if(args.length >= 2){
                    DBmanager.IncompleteModel = new PlotModel(args[1]);
                    p.sendMessage(Freebuild.prefix + "Empty model created");
                    ItemStack tool = new ItemStack(Tool.ModelTool);
                    ItemMeta tmet = tool.getItemMeta();
                    tmet.setDisplayName("Model Selector");
                    tmet.setLore(Arrays.asList(new String[] {"Use this tool to set plot corners", "Right Click - set point 1", "Left click - set point 2"}));
                    tool.setItemMeta(tmet);
                    p.getInventory().setItem(0, tool);
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("deletemodel") && p.hasPermission("plotmanager.create")){
                if(args.length >= 2){
                    if(!DBmanager.modelExists(args[1])){
                        p.sendMessage(Freebuild.prefix + "Model '"+args[1]+"' doesn't exist");
                        return true;
                    }
                    if(args[1].equals("default")){
                        p.sendMessage(Freebuild.prefix + "Default model can't be deleted");
                        return true;
                    }
                    if(DBmanager.currModel != null && args[1].equals(DBmanager.currModel.getName())){
                        p.sendMessage(Freebuild.prefix + "Model currently in use, can't be deleted");
                        return true;
                    }
                    DBmanager.deletePlotModel(args[1]);
                    p.sendMessage(Freebuild.prefix + "Model '"+args[1]+"' deleted");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("modelpos") && p.hasPermission("plotmanager.create")){
                if(args.length >= 2){
                    if(DBmanager.IncompleteModel == null){
                        p.sendMessage(Freebuild.prefix + "You have to create model first");
                        return true;
                    }
                    Location l = p.getLocation();
                    if(!p.isFlying()) {
                        l.subtract(0, 1, 0);
                    }
                    if(args[1].equals("1")){
                        DBmanager.IncompleteModel.setPoint1(l);
                    }
                    else if(args[1].equals("2")){
                        DBmanager.IncompleteModel.setPoint2(l);
                    }
                    else {
                        p.sendMessage(Freebuild.prefix + "Invalid argument: " + args[1]);
                        return true;
                    }
                    p.sendMessage(Freebuild.prefix + "Point set");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("savemodel") && args.length >= 1 && p.hasPermission("plotmanager.create")){
                if(DBmanager.IncompleteModel != null){
                    p.sendMessage(Freebuild.prefix + "Saving model");
                    p.sendMessage(Freebuild.prefix + "Please wait...");
                    DBmanager.savePlotModel(DBmanager.IncompleteModel,p);
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("listmodels") && args.length >= 1 && p.hasPermission("plotmanager.create")){
                p.sendMessage(Freebuild.prefix + "Existing models:");
                String models = "";
                for(String s: DBmanager.Models){
                    models += s + " ";
                }
                p.sendMessage(models);
                return true;
            }
            else if(args[0].equalsIgnoreCase("help")){
                if(args.length == 1){
                    return false;
                }
                p.sendMessage(Freebuild.prefix + "Displaying help for " + ChatColor.DARK_GREEN + "/theme " + args[1]);
                if(args[1].equalsIgnoreCase("toplot")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme toplot" + ChatColor.WHITE + " -- teleports you to your plot in current theme");
                }
                else if(args[1].equalsIgnoreCase("resetplot")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme resetplot" + ChatColor.WHITE + " -- restores plot to original state");
                    p.sendMessage("You have to be inside the plot to reset it");
                    p.sendMessage("You can only reset your own plots in current theme");
                }
                else if(args[1].equalsIgnoreCase("unclaim")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme unclaim" + ChatColor.WHITE + " -- unclaims a plot");
                    p.sendMessage("You have to be inside the plot to unclaim it");
                    p.sendMessage("Plot will be cleared");
                }
                else if(args[1].equalsIgnoreCase("set")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme set [-m <model>] <name>" + ChatColor.WHITE + " -- start new chain of Themed Builds");
                    p.sendMessage("If " + ChatColor.DARK_GREEN + "-m <model>" + ChatColor.WHITE + " is not specified, default model is used");
                    p.sendMessage("To see available models, " + ChatColor.DARK_GREEN + "/theme listmodels");
                }
                else if(args[1].equalsIgnoreCase("new")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme new [-m <model>] <name>" + ChatColor.WHITE + " -- create new Themed Build in current chain");
                    p.sendMessage("If " + ChatColor.DARK_GREEN + "-m <model>" + ChatColor.WHITE + " is not specified, default model is used");
                    p.sendMessage("To see available models, " + ChatColor.DARK_GREEN + "/theme listmodels");
                }
                else if(args[1].equalsIgnoreCase("createmodel")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme createmodel <name>" + ChatColor.WHITE + " -- create empty plot model");
                    p.sendMessage("Created model is empty, and can't be saved");
                    p.sendMessage("Use model tool (default is wooden sword, can be changed in config.yml) to set points");
                    p.sendMessage("While holding model tool, left click on block to set first point, right click to set second point");
                    p.sendMessage("After both points are set, use " + ChatColor.DARK_GREEN + "/theme savemodel" + ChatColor.WHITE + " to save your model");
                }
                else if(args[1].equalsIgnoreCase("modelpos")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme modelpos <1|2>" + ChatColor.WHITE + " -- set point at feet coordinates");
                    p.sendMessage("If player is not flying, point is set to the block player is standing on");
                    p.sendMessage("Otherwise, it's set to the block player feet are in");
                    p.sendMessage("This command exists as an alternative to model tool, see " + ChatColor.DARK_GREEN + "/theme help createmodel");
                }
                else if(args[1].equalsIgnoreCase("deletemodel")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme deletemodel <name>" + ChatColor.WHITE + " -- delete plot model");
                    p.sendMessage("Only saved models can be deleted");
                    p.sendMessage("Default model can't be deleted (model name: default)");
                    p.sendMessage("If model is currently in use, it will not be deleted");
                }
                else if(args[1].equalsIgnoreCase("savemodel")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme savemodel" + ChatColor.WHITE + " -- save current model");
                    p.sendMessage("Model can't be used before it's saved");
                }
                else if(args[1].equalsIgnoreCase("listmodels")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme listmodels" + ChatColor.WHITE + " -- list available models");
                    p.sendMessage("Only saved models are listed");
                }
                else if(args[1].equalsIgnoreCase("help")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme help [subcommand]" + ChatColor.WHITE + " -- view more informations about subcommand");
                    p.sendMessage("If subcommand is not specified, this command displays general help");
                }
                else if(args[1].equalsIgnoreCase("setURL")){
                    p.sendMessage(ChatColor.DARK_GREEN + "/theme setURL <url>" + ChatColor.WHITE + " -- set the URL for the themedbuild");
                    p.sendMessage("URL is displayed when player claims a plot");
                }
                else{
                    p.sendMessage("Specified subcommand does not exist");
                    p.sendMessage("Use " + ChatColor.DARK_GREEN + "/theme help" + ChatColor.WHITE + " to see available subcommands");
                }
                return true;
            } else if(args[0].equalsIgnoreCase("setURL")&&p.hasPermission("plotmanager.create") && args.length >= 2){
                DBmanager.curr.setURL(args[1]);
                p.sendMessage(Freebuild.prefix + "Theme URL set to: " + ChatColor.GRAY + args[1]);
                return true;
            }
        }
        return false;
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent cae) {
        throw new UnsupportedOperationException(Freebuild.prefix + "Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
class setTitle extends StringPrompt {
    @Override
    public String getPromptText(ConversationContext context) {
        return "Enter the Theme this week:";
    }
    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        context.setSessionData("title", input);
        return new setThread();
    }
 }

class setThread extends StringPrompt {

    @Override
    public String getPromptText(ConversationContext cc) {
        return "enter the thread url for this theme:";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        context.setSessionData("url", input);
        return new finished();
    }
    
}
class finished extends MessagePrompt {

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
        return Prompt.END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        Theme theme = null;
        if(type.equalsIgnoreCase("set")){
            theme = new Theme((String) context.getSessionData("title"), (String) context.getSessionData("url"), ploc, "default");
        }else{
            theme = new Theme((String) context.getSessionData("title"), (String) context.getSessionData("url"), "default");
        }
        
        DBmanager.Themes.put((String) context.getSessionData("title"), theme);
        DBmanager.curr = theme;
        DBmanager.currModel = DBmanager.loadPlotModel("default");
        return "Finishing...";
    }
    
}
}
