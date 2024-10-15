package com.mountaineercraft.univerifier.tokens;

import com.mountaineercraft.univerifier.Univerifier;
import io.javalin.Javalin;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class Verification {
    private static final Map<String, Player> verificationTokens = new HashMap<>();
    private Javalin app;

    public static String addVerificationToken(Player player) {
        String token = Long.toHexString(Double.doubleToLongBits(Math.random()));
        verificationTokens.put(token, player);
        return token;
    }

    public Verification() {
        app = Javalin.create().start(25500);

        app.get("/verify", ctx -> {
            String token = ctx.queryParam("token");
            String playerName = ctx.queryParam("player");

            if (token == null || playerName == null) {
                ctx.status(400);
                return;
            }

            Player player = verificationTokens.get(token);
            if (player == null) {
                ctx.status(404);
                return;
            }

            if (!player.getName().equals(playerName)) {
                ctx.status(403);
                return;
            }

            User user = Univerifier.luckPerms.getUserManager().getUser(player.getUniqueId());
            assert user != null;
            user.data().add(Node.builder("univerifier.verified").build());
            Univerifier.luckPerms.getUserManager().saveUser(user);

            player.sendMessage(ChatColor.GREEN + "You have been verified!");
            player.playSound(player.getLocation(), "entity.player.levelup", 1, 1);

            // Clear the player's effects and inventory
            Univerifier.getPlugin(Univerifier.class).getServer().getScheduler().scheduleSyncDelayedTask(Univerifier.getPlugin(Univerifier.class), () -> {
                player.getInventory().clear();
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(5);

                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
            });

            verificationTokens.remove(token);
            ctx.status(200);
            ctx.result("You have been verified!");
        });
    }
}
