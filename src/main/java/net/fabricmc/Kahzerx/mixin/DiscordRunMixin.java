package net.fabricmc.Kahzerx.mixin;

import net.fabricmc.Kahzerx.discord.DiscordFileManager;
import net.fabricmc.Kahzerx.discord.DiscordListener;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class DiscordRunMixin {
    @Inject(at = @At("HEAD"), method = "run")
    public void run (CallbackInfo ci){
        try {
            String[] result = DiscordFileManager.readFile();
            if (!result[0].equals("") && !result[1].equals("") && !result[2].equals("")) {
                if (result[2].equals("true")) {
                    try {
                        DiscordListener.connect((MinecraftServer) (Object) this, result[0], result[1], "");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("config file not created");
        }
    }

    @Inject(at = @At("RETURN"), method = "run")
    public void stop (CallbackInfo ci){
        if (DiscordListener.chatBridge) DiscordListener.stop();
    }

}
