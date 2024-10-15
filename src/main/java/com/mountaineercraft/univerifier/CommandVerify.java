package com.mountaineercraft.univerifier;

import com.mountaineercraft.univerifier.mail.EmailUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.mail.internet.InternetAddress;

import static com.mountaineercraft.univerifier.tokens.Verification.addVerificationToken;

public class CommandVerify implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (!player.hasPermission("univerifier.verified")) {
                if (strings.length == 1) {
                    boolean result = true;
                    try {
                        InternetAddress email = new InternetAddress(strings[0]);
                        email.validate();
                    } catch (Exception e) {
                        player.sendMessage("Invalid email address.");
                        result = false;
                    }

                    // If has the correct domain, to make sure the user is a student
                    if (result && strings[0].endsWith(Univerifier.requiredEmailDomain)) {
                        String token = addVerificationToken(player);
                        EmailUtil.sendVerificationEmail(token, strings[0], player);
                        player.sendMessage("Verification email sent to " + strings[0]);
                    } else {
                        player.sendMessage("You must use a " + Univerifier.requiredEmailDomain + " email address.");
                    }

                } else {
                    return false;
                }
            } else {
                player.sendMessage("You are already verified.");
            }
        } else {
            commandSender.sendMessage("You must be a player to use this command.");
        }

        return true;
    }
}
