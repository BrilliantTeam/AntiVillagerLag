package rebelmythik.antivillagerlag.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import rebelmythik.antivillagerlag.AntiVillagerLag;
import rebelmythik.antivillagerlag.utils.ColorCode;
import rebelmythik.antivillagerlag.utils.VillagerUtilities;

import java.util.List;

public class RadiusOptimizeCommand implements CommandExecutor {

    AntiVillagerLag plugin;
    ColorCode colorcodes = new ColorCode();

    public RadiusOptimizeCommand(AntiVillagerLag plugin) {
        this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("avloptimize")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
                return true;
            }
            Player senderPlayer = (Player) sender;

            if(!sender.hasPermission("avl.optimize")) {
                sender.sendMessage(colorcodes.cm(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(colorcodes.cm(plugin.getConfig().getString("messages.correct-usage")));
                return false;
            }
            if (args.length == 1) {
                try {
                    Integer.parseInt(args[0]);
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage(colorcodes.cm(plugin.getConfig().getString("messages.correct-usage")));
                    return false;
                }
                double radius = Double.parseDouble(args[0]);
                long cooldown = plugin.getConfig().getLong("cooldown");

                if (radius > plugin.getConfig().getDouble("RadiusLimit")) {
                    sender.sendMessage(colorcodes.cm(plugin.getConfig().getString("messages.radius-limit")));
                    return false;
                }

                for (Entity entity : senderPlayer.getNearbyEntities(radius, radius, radius)) {
                    Entity vil = entity;
                    if (entity instanceof Villager) {
                        if(((Villager) entity).isAware()) {
                            // If they don't have cooldown set it
                            if (!VillagerUtilities.hasCooldown((Villager) vil, plugin)) {
                                VillagerUtilities.setNewCooldown((Villager) vil, plugin, (long)0);
                            }
                            long vilCooldown = VillagerUtilities.getCooldown((Villager) vil, plugin);
                            long currentTime = System.currentTimeMillis() / 1000;

                            // If villager has already been disabled check if they do have a cooldown
                            // to prevent bypassing of the cooldown feature
                            if (vilCooldown > currentTime) continue;

                            // Set Villager Name to Optimize Name and disable the AI
                            List<String> namesThatDisable = plugin.getConfig().getStringList("NamesThatDisable");
                            entity.setCustomName(namesThatDisable.get(0));
                            ((Villager) entity).setAware(false);

                            // set all necessary flags and timers
                            VillagerUtilities.setMarker((Villager) vil, plugin);
                            VillagerUtilities.setNewCooldown((Villager) vil, plugin, cooldown);
                        }

                    }
                }
            }
            return true;
        }
        return false;
    }
}
