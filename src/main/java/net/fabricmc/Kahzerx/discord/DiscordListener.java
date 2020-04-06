package net.fabricmc.Kahzerx.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordListener extends ListenerAdapter {
    private static Pattern url_patt = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    private static JDA process = null;
    public static String channelId = "";
    public static String token = "";
    public static boolean chatBridge = false;

    MinecraftServer server;

    public DiscordListener (MinecraftServer s){
        this.server = s;
    }

    public static void connect(MinecraftServer server, String t, String c, String playerName){
        token = t;
        channelId = c;
        try{
            chatBridge = false;
            DiscordFileManager.updateFile(false);
            process = new JDABuilder(token).addEventListeners(new DiscordListener(server)).build();
            process.awaitReady();
            chatBridge = true;
            DiscordFileManager.updateFile(true);
        }
        catch (Exception e){
            System.out.println(e);
            if (!playerName.equals("")){
                server.getPlayerManager().getPlayer(playerName).sendChatMessage(new LiteralText("Unable to start the process, is the token correct?"), MessageType.CHAT);
            }
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (chatBridge){
            if (event.getChannel().getId().equals(channelId)){
                if (event.getMessage().getContentDisplay().equals("")) return;
                if (event.getAuthor().isBot()) return;
                String msg = "[Discord] <" + event.getAuthor().getName() + "> " + event.getMessage().getContentDisplay();
                if (msg.length() >= 256) msg = msg.substring(0, 253) + "...";

                Matcher m = url_patt.matcher(msg);
                Text finalMsg = new LiteralText("");
                boolean hasUrl = false;
                int prev = 0;

                while (m.find()){
                    hasUrl = true;
                    Text text = new LiteralText(m.group(0)).styled((style -> {
                        style.setColor(Formatting.GRAY)
                                .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, m.group(0)))
                                .setHoverEvent(new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, new LiteralText("Open URL")));
                    }));
                    finalMsg = finalMsg.append(new LiteralText(msg.substring(prev, m.start()))).append(text);
                    prev = m.end();
                }
                if (hasUrl) server.getPlayerManager().sendToAll(finalMsg.append(msg.substring(prev)));
                else server.getPlayerManager().sendToAll(new LiteralText(msg));
            }
        }
    }

    public static void sendMessage(String msg){
        if (chatBridge){
            try {
                TextChannel ch = process.getTextChannelById(channelId);
                if (ch != null) ch.sendMessage(msg).queue();
            }
            catch (Exception e){
                System.out.println("wrong channelId :(");
            }
        }
    }

    public static void stop(){
        process.shutdownNow();
        chatBridge = false;
    }
}
