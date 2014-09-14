/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author Donovan
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
                        p.teleport(new Location(plot.getCorner().getWorld(), plot.getCorner().getBlockX(), plot.getCorner().getBlockY()+2, plot.getCorner().getBlockZ()));
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
                if(args.length == 1){
                    return false;
                }
                if(args[1].equals("-m") && args.length > 3){
                    modelname = args[2];
                }
                for(String s : Arrays.asList(args).subList(1, args.length)){
                    tname += s + " ";
                }
                DBmanager.currModel = DBmanager.loadPlotModel(modelname);
                Theme theme = new Theme(tname, " ", modelname);
                DBmanager.Themes.put(tname, theme);
                DBmanager.curr.close();
                DBmanager.curr = theme;
                if(!DBmanager.BuildPastPlots){
                    DBmanager.plots = new HashMap<String, ArrayList<Plot>>();
                }
                p.teleport(theme.getCent());
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
                if(args.length == 1){
                    return false;
                }
                if(args[1].equals("-m") && args.length > 3){
                    modelname = args[2];
                }
                for(String s : Arrays.asList(args).subList(1, args.length)){
                    tname += s + " ";
                }
                DBmanager.currModel = DBmanager.loadPlotModel(modelname);
                Theme theme = new Theme(tname, " ", p.getLocation(), modelname);
                DBmanager.Themes.put(tname, theme);
                DBmanager.curr = theme;
                if(!DBmanager.BuildPastPlots){
                    DBmanager.plots = new HashMap<String, ArrayList<Plot>>();
                }
                return true;
            }
            else if(args[0].equalsIgnoreCase("createmodel")){
                if(args.length == 2){
                    PlotModel model = new PlotModel(args[1]);
                    DBmanager.IncompleteModels.put(args[1], model);
                    p.sendMessage(Freebuild.prefix + "Empty model created");
                    return true;
                }
                else if(args.length == 3){
                    if(DBmanager.IncompleteModels.containsKey(args[1])){
                        PlotModel model = DBmanager.IncompleteModels.get(args[1]);
                        if(args[2].equalsIgnoreCase("point1")){
                            model.setPoint1(p.getLocation());
                            p.sendMessage(Freebuild.prefix + "First point set");
                            return true;
                        }
                        else if(args[2].equalsIgnoreCase("point2")){
                            model.setPoint2(p.getLocation());
                            p.sendMessage(Freebuild.prefix + "Second point set");
                            return true;
                        }
                    }
                }
                else if(args.length == 4){
                    if(DBmanager.IncompleteModels.containsKey(args[1])){
                        PlotModel model = DBmanager.IncompleteModels.get(args[1]);
                        if(args[2].equalsIgnoreCase("height")){
                            try{
                                model.setHeight(Integer.parseInt(args[3]));
                                p.sendMessage(Freebuild.prefix + "Height set");
                                return true;
                            }
                            catch(NumberFormatException ex){
                                return false;
                            }
                        }
                    }
                }
            }
            else if(args[0].equalsIgnoreCase("savemodel") && args.length == 2){
                if(DBmanager.IncompleteModels.containsKey(args[1])){
                    p.sendMessage(Freebuild.prefix + "Saving model");
                    p.sendMessage(Freebuild.prefix + "Please wait...");
                    PlotModel model = DBmanager.IncompleteModels.get(args[1]);
                    DBmanager.savePlotModel(model);
                    DBmanager.IncompleteModels.remove(args[1]);
                    p.sendMessage(Freebuild.prefix + "Model saved");
                    return true;
                }
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
