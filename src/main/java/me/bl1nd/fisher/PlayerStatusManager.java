package me.bl1nd.fisher;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class PlayerStatusManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Set player to survival mode
        player.setGameMode(GameMode.SURVIVAL);

        // Reset hunger level
        player.setFoodLevel(20);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // Prevent hunger changes
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // Prevent players from taking damage
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        // Prevent fishing rod from losing durability
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH
                || event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY
                || event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {

            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            // Check if the item in hand is a fishing rod
            if (item.getType() == Material.FISHING_ROD) {
                // Set the item durability to its maximum to prevent it from breaking
                item.setDurability((short) 0);
            }
        }
    }
}
