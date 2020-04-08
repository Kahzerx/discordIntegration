package net.fabricmc.Kahzerx.mixin;

import net.fabricmc.Kahzerx.discord.DiscordListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class DiscordJoinServerMixin {
    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    public void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        if (DiscordListener.chatBridge){
            DiscordListener.sendMessage("**" + player.getName().getString() + " joined the game!**");
        }
    }
}
