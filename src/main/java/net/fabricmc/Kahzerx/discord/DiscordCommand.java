package net.fabricmc.Kahzerx.discord;

import static net.minecraft.server.command.CommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.io.*;
import java.util.Properties;

public class DiscordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("discord")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.literal("setBot")
                        .then(CommandManager.argument("token", StringArgumentType.string())
                                .then(CommandManager.argument("channelId", StringArgumentType.string())
                                        .executes(context -> f0(context.getSource(), StringArgumentType.getString(context, "token"), StringArgumentType.getString(context, "channelId"))))))
                .then(CommandManager.literal("stop")
                        .executes(context -> f1(context.getSource())))
                .then(CommandManager.literal("start")
                        .executes(context -> f2(context.getSource())))
                .then(CommandManager.literal("restart")
                        .executes(context -> f3(context.getSource()))));
    }

    private static int f0(ServerCommandSource src, String token, String channelId){
        if (DiscordListener.chatBridge){
            src.sendFeedback(new LiteralText("Please stop the mod before you make any changes"), false);
        }
        else{
            DiscordFileManager.writeFile(token, channelId);
            src.sendFeedback(new LiteralText("Done!"), false);
        }
        return 1;
    }

    private static int f1(ServerCommandSource src){
        if (DiscordListener.chatBridge){
            DiscordListener.stop();
            src.sendFeedback(new LiteralText("Discord integration has stopped"), false);
        }
        else{
            src.sendFeedback(new LiteralText("Discord integration is already off"), false);
        }
        return 1;
    }

    private static int f2(ServerCommandSource src){
        String[] result = DiscordFileManager.readFile();
        if (!DiscordListener.chatBridge){
            if (!result[0].equals("") && !result[1].equals("")) {
                try {
                    DiscordListener.connect(src.getMinecraftServer(), result[0], result[1], src.getPlayer().getName().toString());
                    src.sendFeedback(new LiteralText("Discord integration is running"), false);
                } catch (Exception e) {
                    System.out.println(e);
                    src.sendFeedback(new LiteralText("Unable to start the process, is the token correct?"), false);
                }
            }
            else{
                src.sendFeedback(new LiteralText("set up a bot first please"), false);
            }
        }
        else{
            src.sendFeedback(new LiteralText("Discord integration is already on"), false);
        }
        return 1;
    }

    private static int f3(ServerCommandSource src){
        src.sendFeedback(new LiteralText("Restarting bot..."), false);
        if (DiscordListener.chatBridge){
            DiscordListener.stop();
        }
        try{
            String[] result = DiscordFileManager.readFile();
            if (!result[0].equals("") && !result[1].equals("")) {
                DiscordListener.connect(src.getMinecraftServer(), result[0], result[1], src.getPlayer().getName().toString());
                src.sendFeedback(new LiteralText("Discord integration is running now"), false);
            }
            else{
                src.sendFeedback(new LiteralText("set up a bot first please"), false);
            }
        }
        catch (Exception e){
            System.out.println(e);
            src.sendFeedback(new LiteralText("Unable to start the process, is the token correct?"), false);
        }
        return 1;
    }
}
