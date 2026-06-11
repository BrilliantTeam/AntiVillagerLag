package rebelmythik.antivillagerlag.events;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import rebelmythik.antivillagerlag.AntiVillagerLag;
import rebelmythik.antivillagerlag.utils.ColorCode;
import rebelmythik.antivillagerlag.utils.VillagerUtilities;

import java.util.List;


public class BlockAI {
    private final AntiVillagerLag plugin;
    ColorCode colorCodes = new ColorCode();

    public BlockAI(AntiVillagerLag plugin) {
        this.plugin = plugin;
    }

    public void call(Villager vil, Player player) {
        long cooldown = plugin.getConfig().getLong("cooldown");

        Location loc = vil.getLocation();
        Material belowvil = vil.getWorld().getBlockAt(loc.getBlockX(), (loc.getBlockY()-1), loc.getBlockZ()).getType();

        // else check if Villager is disabled with Block
        List<String> blocksThatDisable = plugin.getConfig().getStringList("BlocksThatDisable");
        boolean willBeDisabled = blocksThatDisable.contains(belowvil.name());

        // Handle the correct AI state
        if(vil.isAware()) {
            // Check that the villager is disabled
            if (!willBeDisabled)
                return;
            // check if villager has AI Toggle cooldown
            if(VillagerUtilities.hasCooldown(vil, player, plugin, colorCodes))
                return;
            vil.setAware(false);
            // set all necessary flags and timers
            VillagerUtilities.setMarker(vil, plugin);
            VillagerUtilities.setDisabledByBlock(vil, plugin, true);
            VillagerUtilities.setNewCooldown(vil, plugin, cooldown);
        } else {
            // Re-Enabling AI
            // Check that the villager is disabled and disabled by Block
            if (willBeDisabled || !VillagerUtilities.getDisabledByBlock(vil, plugin))
                return;
            // check if villager has AI Toggle cooldown
            if(VillagerUtilities.hasCooldown(vil, player, plugin, colorCodes))
                return;
            // check if Villager was disabled by AVL
            // prevents breaking NPC plugins
            if (!VillagerUtilities.hasMarker(vil, plugin)) return;
            vil.setAware(true);
            VillagerUtilities.setNewCooldown(vil, plugin, cooldown);
            VillagerUtilities.setDisabledByBlock(vil, plugin, false);
            // remove the marker again
            VillagerUtilities.removeMarker(vil, plugin);
        }
    }
}
