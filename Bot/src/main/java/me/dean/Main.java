package me.dean;


import com.github.aabssmc.minehutapi.MinehutAPI;
import com.github.aabssmc.minehutapi.players.MinehutPlayer;
import com.github.aabssmc.minehutapi.players.Players;
import com.github.aabssmc.minehutapi.players.Rank;
import com.github.aabssmc.minehutapi.server.Server;
import com.github.aabssmc.minehutapi.*;
import com.github.aabssmc.minehutapi.server.Servers;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;

import org.javacord.api.interaction.*;
import org.javacord.api.util.logging.ExceptionLogger;


public class Main {

    public static void main(String[] args) {
        String token = "MTIxMDk3Mjc4OTc0NjQzODE2NA.GHz8ij.U-hePLibtFk8zJiNVhsiMH7Ci5FJmb2JHC3SK4";
        DiscordApi api = new DiscordApiBuilder().setToken(token).addIntents(Intent.MESSAGE_CONTENT).login().join();

        // slash commands
        SlashCommand checkrank = SlashCommand.with("checkrank", "checks the rank of a player", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "user", "Provide the user or uuid, if not it will error"))).createGlobal(api).join();
        SlashCommand serverinfo = SlashCommand.with("serverinfo", "info about a server", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "server", "Provide a valid name, if not it will error"))).createGlobal(api).join();

        // minehut info
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("#info")) {
                String AllPlayers = String.valueOf(Players.getOnlinePlayerCount());
                String BedrockPlayers = String.valueOf(Players.getBedrockTotal());
                String JavaPlayers = String.valueOf(Players.getJavaTotal());
                String UserCount = String.valueOf(MinehutAPI.getUserCount());
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Minehut player count")
                        .addField("All registered users (on website)", UserCount)
                        .addField("Total Players", AllPlayers)
                        .addField("Java Players", JavaPlayers)
                        .addField("Total Servers", String.valueOf(MinehutAPI.getOnlineServerCount()))
                        .addField("Bedrock Players", BedrockPlayers)
                        .setColor(Color.BLUE);
                event.getChannel().sendMessage(embed);
            }
        });
        // server info
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getCommandName().equals("serverinfo")) {
                slashCommandInteraction.getArgumentStringValueByName("server").ifPresent(str -> {
                    Server srv = Servers.getServer(str);
                    String name = srv.getName();
                    boolean suspended = srv.isSuspended();
                    int playercount = srv.getPlayerCount();
                    boolean isonline = srv.isOnline();
                    double Credits = srv.getCreditsPerDay();
                    slashCommandInteraction.createImmediateResponder().setContent("name: " + name + ", Online: " + isonline + ", Player count:" + playercount + ", Banned: " + suspended + ", Credits per day: "+ Credits).setFlags(MessageFlag.EPHEMERAL).respond().exceptionally(ExceptionLogger.get());
                });
            }
        });
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getCommandName().equals("checkrank")) {
                slashCommandInteraction.getArgumentStringValueByName("user").ifPresent(str -> {
                    Rank rank = Rank.getRank(str);
                    slashCommandInteraction.createImmediateResponder().setContent(str + "'s rank is " + rank).setFlags(MessageFlag.EPHEMERAL).respond().exceptionally(ExceptionLogger.get());
                });
            }
        });
    }
}