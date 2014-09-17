/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
                if(!DBmanager.InfinitePlotsPerPlayer && DBmanager.plots.containsKey(p.getName())
                        && DBmanager.MaxPlotsPerPlayer <= DBmanager.plots.get(p.getName()).size()){
                    p.sendMessage(Freebuild.prefix + "You reached maximum number of plots");
                    return true;
                }
                //move the player and gen new plot
                for(Plot plot : DBmanager.curr.getCurrplots()){
                    if(!plot.isAssigned()){
//                        p.sendMessage(p.getLocation().toString());
                        plot.assign(p);
                        p.sendMessage(Freebuild.prefix + "Welcome to a new plot, the current theme is " + DBmanager.curr.getTheme());
                        return true;
                    }
                }
                DBmanager.curr.genPlots(false);
                for(Plot plot : DBmanager.curr.getCurrplots()){
                    if(!plot.isAssigned()){
                        plot.assign(p);
                        p.teleport(plot.getCorner());
                        p.sendMessage(Freebuild.prefix + "Welcome to a new plot, the current theme is " + DBmanager.curr.getTheme());
                        return true;
                    }
                }
            }else if(args[0].equalsIgnoreCase("new")&&p.hasPermission("plotmanager.create")){
                //create new theme
                p.sendMessage("Generating...");
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
            }
            else if(args[0].equalsIgnoreCase("set")&&p.hasPermission("plotmanager.create")){
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
                DBmanager.currModel = DBmanager.loadPlotModel(modelname);
                Theme theme = new Theme(tname, " ", p.getLocation(), modelname);
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
                return true;
            }
            else if(args[0].equalsIgnoreCase("createmodel") && p.hasPermission("plotmanager.create")){
                if(args.length >= 2){
                    DBmanager.IncompleteModel = new PlotModel(args[1]);
                    p.sendMessage(Freebuild.prefix + "Empty model created");
                    ItemStack tool = new ItemStack(DBmanager.ModelTool);
                    ItemMeta tmet = tool.getItemMeta();
                    tmet.setDisplayName("Model Selector");
                    tmet.setLore(Arrays.asList(new String[] {"Use this tool to set plot corners", "Right Click - set point 1", "Left click - set point 2"}));
                    tool.setItemMeta(tmet);
                    p.getInventory().setItem(0, tool);
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
                if(args[1].equalsIgnoreCase("set")){
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
                else{
                    p.sendMessage("Specified subcommand does not exist");
                    p.sendMessage("Use " + ChatColor.DARK_GREEN + "/theme help" + ChatColor.WHITE + " to see available subcommands");
                }
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
