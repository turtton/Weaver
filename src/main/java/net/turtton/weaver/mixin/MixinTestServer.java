package net.turtton.weaver.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.TestServer;
import net.minecraft.util.ApiServices;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.storage.LevelStorage;
import net.turtton.weaver.TestServerVariables;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.Collection;
import java.util.Objects;

@Mixin(TestServer.class)
public abstract class MixinTestServer extends MinecraftServer {

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private static ApiServices field_39441;

    public MixinTestServer(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(serverThread, session, dataPackManager, saveLoader, proxy, dataFixer, ApiServices.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), new File(".")), worldGenerationProgressListenerFactory);
    }


    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void insertServices(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Collection<GameTestBatch> batches, BlockPos pos, CallbackInfo ci) {
        SkullBlockEntity.setServices(apiServices, this);
    }

    @Inject(method = "setupServer", at = @At("HEAD"), cancellable = true)
    private void enableLocalPlayerConnecting(CallbackInfoReturnable<Boolean> cir) throws IOException {
        var server = (TestServer) (Object) this;
        server.setOnlineMode(false);
        server.setServerIp("127.0.0.1");
        server.setPvpEnabled(true);
        server.setFlightEnabled(true);
        //        server.setResourcePack();
        server.setMotd("TestSever");
        server.setPlayerIdleTimeout(0);
        var gamemode = GameMode.DEFAULT;
        server.getSaveProperties().setGameMode(gamemode);
        LOGGER.info("Default game type: {}", gamemode);

        var inetAddress = InetAddress.getByName(server.getServerIp());
        server.setServerPort(TestServerVariables.getPort());
        server.generateKeyPair();
        LOGGER.info(
                "Starting Minecraft server on {}:{}",
                server.getServerIp().isEmpty() ? "*" : server.getServerIp(),
                server.getServerPort());

        try {
            Objects.requireNonNull(server.getNetworkIo()).bind(inetAddress, server.getServerPort());
        } catch (Exception exception) {
            LOGGER.warn("**** FAILED TO BIND TO PORT!");
            LOGGER.warn("The exception was: {}", exception.toString());
            LOGGER.warn("Perhaps a server is already running on that port?");
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Redirect(
            method = "setupServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/test/TestServer;setPlayerManager(Lnet/minecraft/server/PlayerManager;)V"
            )
    )
    private void changePlayerLimit(TestServer instance, PlayerManager playerManager) {
        instance.setPlayerManager(new PlayerManager(instance, getRegistryManager(), saveHandler, 1) {
            @Override
            public boolean canBypassPlayerLimit(GameProfile profile) {
                return true;
            }
        });
    }

    @Override
    public int getNetworkCompressionThreshold() {
        return TestServerVariables.getNetworkCompressionThreshold();
    }
}
