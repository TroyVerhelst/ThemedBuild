/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
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
                .thatExcludesNonPlayersWithMessage("You must be a player to send this command");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player p = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("theme")){
            if(args.length == 0){
                //move the player and gen new plot
                for(Plot plot : DBmanager.curr.getCurrplots()){
                    if(!plot.isAssigned()){
                        p.sendMessage(p.getLocation().toString());
                        plot.assign(p);
                        p.teleport(new Location(plot.getCorner().getWorld(), plot.getCorner().getBlockX(), plot.getCorner().getBlockY()+2, plot.getCorner().getBlockZ()));
                        p.sendMessage("Welcome to a new plot, the current theme is " + DBmanager.curr.getTheme());
                        return true;
                    }
                }
                DBmanager.curr.genPlots(false);
                for(Plot plot : DBmanager.curr.getCurrplots()){
                    if(!plot.isAssigned()){
                        plot.assign(p);
                        p.teleport(plot.getCorner());
                        p.sendMessage("Welcome to a new plot, the current theme is " + DBmanager.curr.getTheme());
                        return true;
                    }
                }
            }else if(args[0].equalsIgnoreCase("new")&&p.hasPermission("plotmanager.create")){
                //create new theme
                p.sendMessage("Generating...");
                String tname = "";
                for(String s : args){
                    if(!s.equalsIgnoreCase("new")){
                        tname += s + " ";
                    }
                }
                Theme theme = new Theme(tname, " ");
                DBmanager.Themes.put(tname, theme);
                DBmanager.curr.close();
                DBmanager.curr = theme;
                p.teleport(theme.getCent());
//                type = args[0];
//                ploc = p.getLocation();
//                conversationFactory.buildConversation((Conversable) sender).begin();
////                p.teleport(DBmanager.curr.getCent());
                return true;
            }
            else if(args[0].equalsIgnoreCase("set")&&p.hasPermission("plotmanager.create")){
                //set and generate a theme with player at center
                p.sendMessage("Generating...");
                String tname = "";
                for(String s : args){
                    if(!s.equalsIgnoreCase("set")){
                        tname += s + " ";
                    }
                }
                Theme theme = new Theme(tname, " ", p.getLocation());
                DBmanager.Themes.put(tname, theme);
                DBmanager.curr = theme;
                return true;
            }
        }
        return false;
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent cae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            theme = new Theme((String) context.getSessionData("title"), (String) context.getSessionData("url"), ploc);
        }else{
            theme = new Theme((String) context.getSessionData("title"), (String) context.getSessionData("url"));
        }
        
        DBmanager.Themes.put((String) context.getSessionData("title"), theme);
        DBmanager.curr = theme;
        return "Finishing...";
    }
    
}
}
