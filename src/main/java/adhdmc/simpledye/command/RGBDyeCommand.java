package adhdmc.simpledye.command;

import adhdmc.simpledye.SimpleDye;
import adhdmc.simpledye.util.SDMessage;
import adhdmc.simpledye.util.SDPerm;
import com.destroystokyo.paper.MaterialSetTag;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RGBDyeCommand extends SubCommand {
    MiniMessage miniMessage = SimpleDye.getMiniMessage();

    private static final Pattern pattern = Pattern.compile("#?([a-fA-F0-9]{6})");
    private static final MaterialSetTag RGB_COLORABLE = new MaterialSetTag(new NamespacedKey(SimpleDye.getInstance(), "rgb_colorable"))
            .add(Material.LEATHER_BOOTS)
            .add(Material.LEATHER_LEGGINGS)
            .add(Material.LEATHER_CHESTPLATE)
            .add(Material.LEATHER_HELMET)
            .add(Material.LEATHER_HORSE_ARMOR);

    public RGBDyeCommand() {
        super("rgb", "Allows dying with RGB Colors represented in Hex.", "/sd rgb <hex>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Not a Player Check
        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize(SDMessage.ERROR_NOT_A_PLAYER.getMessage(),
                    Placeholder.parsed("plugin_prefix", SDMessage.PLUGIN_PREFIX.getMessage())));
            return;
        }

        // No Permissions Check
        if (!sender.hasPermission(SDPerm.DYE_RGB_PERM.getPerm())) {
            sender.sendMessage(miniMessage.deserialize(SDMessage.ERROR_NO_PERMISSION.getMessage(),
                    Placeholder.parsed("plugin_prefix", SDMessage.PLUGIN_PREFIX.getMessage())));
            return;
        }

        // No Arguments Check
        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize(SDMessage.ERROR_NO_ARGUMENTS.getMessage(),
                    Placeholder.parsed("plugin_prefix", SDMessage.PLUGIN_PREFIX.getMessage())));
            return;
        }

        Player player = (Player) sender;
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        // Invalid Item Check
        if (!RGB_COLORABLE.isTagged(mainHandItem)) {
            sender.sendMessage(miniMessage.deserialize(SDMessage.ERROR_ITEM_NOT_HEX_DYABLE.getMessage(),
                    Placeholder.parsed("plugin_prefix", SDMessage.PLUGIN_PREFIX.getMessage()),
                    Placeholder.parsed("item", mainHandItem.getType().toString().toLowerCase(Locale.ROOT))));
            return;
        }

        Matcher matcher = pattern.matcher(args[0]);

        // Invalid Hex Code Check
        if (!matcher.find()) {
            sender.sendMessage(miniMessage.deserialize(SDMessage.ERROR_INVALID_HEX_CODE.getMessage(),
                    Placeholder.parsed("plugin_prefix", SDMessage.PLUGIN_PREFIX.getMessage()),
                    Placeholder.parsed("input", args[0])));
            return;
        }

        // Execute Request
        String hex = matcher.group(0);
        String hexTag = "<" + hex + ">";
        int red = Integer.parseInt(hex.substring(1,3), 16);
        int green = Integer.parseInt(hex.substring(3,5), 16);
        int blue = Integer.parseInt(hex.substring(5,7), 16);
        LeatherArmorMeta meta = (LeatherArmorMeta) mainHandItem.getItemMeta();
        meta.setColor(Color.fromRGB(red, green, blue));
        mainHandItem.setItemMeta(meta);
        sender.sendMessage(miniMessage.deserialize(SDMessage.COMMAND_OUTPUT_DYE_SUCCESS.getMessage(),
                Placeholder.parsed("plugin_prefix", SDMessage.PLUGIN_PREFIX.getMessage()),
                Placeholder.parsed("color", hexTag)));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
