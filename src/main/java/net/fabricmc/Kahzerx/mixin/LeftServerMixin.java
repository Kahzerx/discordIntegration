package net.fabricmc.Kahzerx.mixin;

import net.fabricmc.Kahzerx.discord.DiscordListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class LeftServerMixin {
    @Shadow ServerPlayerEntity player;
    @Inject(at = @At("RETURN"), method = "onDisconnected")
    public void onPlayerLeft(Text reason, CallbackInfo ci){
        if (DiscordListener.chatBridge){
            DiscordListener.sendMessage("**" + player.getName().getString() + " left the game!**");
        }
    }
}
