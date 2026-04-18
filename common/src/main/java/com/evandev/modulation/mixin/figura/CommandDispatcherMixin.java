package com.evandev.modulation.mixin.figura;

import com.evandev.modulation.client.ClientCommandHelper;
import com.evandev.modulation.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = CommandDispatcher.class, remap = false)
public class CommandDispatcherMixin {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "register", at = @At("HEAD"))
    private void onRegister(LiteralArgumentBuilder command, CallbackInfoReturnable<CommandNode> cir) {
        if ("figura".equals(command.getLiteral())) {
            command.then(LiteralArgumentBuilder.literal("clear")
                    .then(RequiredArgumentBuilder.argument("targets", StringArgumentType.greedyString())
                            .suggests((context, builder) -> {
                                List<String> suggestions = new ArrayList<>();
                                if (context.getSource() instanceof SharedSuggestionProvider provider) {
                                    suggestions.addAll(provider.getOnlinePlayerNames());
                                }
                                suggestions.addAll(Arrays.asList("@a", "@p", "@r", "@s", "@e"));
                                return SharedSuggestionProvider.suggest(suggestions, builder);
                            })
                            .executes(context -> {
                                String targets = StringArgumentType.getString(context, "targets");
                                if (Services.PLATFORM.isPhysicalClient()) {
                                    ClientCommandHelper.forward("modulation_figura clear " + targets);
                                }
                                return 1;
                            })
                    )
            );
        }
    }
}