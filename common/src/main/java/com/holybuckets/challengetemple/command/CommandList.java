package com.holybuckets.challengetemple.command;

//Project imports

import com.holybuckets.challengetemple.core.ChallengeTempleApi;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.CommandRegistry;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;


import java.util.List;

public class CommandList {

    public static final String CLASS_ID = "033";
    private static final String PREFIX = "hbTemples";

    public static void register() {
        CommandRegistry.register(ExitTemple::noArgs);
        CommandRegistry.register(LoadChallenge::challengeId);
    }

    //Exit Temple Command
    private static class ExitTemple {
        private static LiteralArgumentBuilder<CommandSourceStack> noArgs() {
            return Commands.literal(PREFIX)
                .then(Commands.literal("exitTemple")
                    .executes(context -> execute(context.getSource()))
                );
        }

        private static int execute(CommandSourceStack source) {
            try {
                ServerPlayer player = source.getPlayerOrException();
                String msg = ChallengeTempleApi.forceExitChallenge(player);
                if(msg == null)
                    source.sendSuccess(() -> Component.literal( "Challenge exited" ), true);
                else
                    source.sendFailure(Component.translatable(msg));

            } catch (Exception e) {
                source.sendFailure(Component.translatable("Unknown error processing command: ", e.getMessage()));
                return 1;
            }

            return 0;
        }
    }

    //Load Challenge Command  
    private static class LoadChallenge {

        private static LiteralArgumentBuilder<CommandSourceStack> challengeId() {
            return Commands.literal(PREFIX)
                .then(Commands.literal("loadChallenge")
                    .then(Commands.argument("challengeId", StringArgumentType.string()))
                    .executes(context -> {
                        String id = StringArgumentType.getString(context, "challengeId");
                        return execute(context.getSource(), id);
                    })
                );
        }

        private static int execute(CommandSourceStack source, String id) {
            // Implementation will go here
            return 0;
        }
    }


}
//END CLASS COMMANDLIST
