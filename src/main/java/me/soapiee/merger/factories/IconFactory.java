package me.soapiee.merger.factories;

import me.soapiee.merger.Merger;
import me.soapiee.merger.enums.ClickAction;
import me.soapiee.merger.enums.PageType;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.utils.CustomLogger;
import me.soapiee.merger.utils.Message;
import me.soapiee.merger.utils.MessageManager;
import me.soapiee.merger.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class IconFactory {

    private final Merger main;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;
//    private final TexturesProvider texturesProvider;

    public IconFactory(Merger main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
//        texturesProvider = main.getInternalsManager().getTexturesProvider();
    }

    // Used to create generic/required icons
    public Icon createIcon(FileConfiguration config, String path, CommandSender sender) {

//        if (config.getString(path + ".type", "").equalsIgnoreCase("texture"))
//            return new Icon(createItemStack(path, sender), createLore(path), getSlot(path));

        Material material = getMaterial(config, path + ".value", sender);
        ItemStack item = createItemStack(config, material, path);
        List<String> lore = createLore(config, path);
        int slot = getSlot(config, path);
        ClickAction action = getClickAction(config, path, sender);

        Icon icon = new Icon(item, lore, slot, action);

        if (action == ClickAction.OPEN) icon.setPage(getNavigationPage(config, path, sender));

        return icon;
    }

    private Material getMaterial(FileConfiguration config, String path, CommandSender sender) {
        String input = config.isSet(path) ? config.getString(path) : "GRASS_BLOCK";
        Material material;

        try {
            material = Material.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            material = Material.GRASS_BLOCK;
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDMATERIAL, path));
        }

        return material;
    }

    private ItemStack createItemStack(FileConfiguration config, Material material, String path) {
        String displayName = config.getString(path + ".display_name", "&7 ");
        boolean glowing = config.getBoolean(path + ".glowing", false);

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Utils.addColour(displayName));

        if (glowing) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private List<String> createLore(FileConfiguration config, String path) {
        if (!config.isSet(path + ".lore")) return new ArrayList<>();

        return config.isList(path + ".lore") ? config.getStringList(path + ".lore") : new ArrayList<>();
    }

    private int getSlot(FileConfiguration config, String path) {
        return config.getInt(path + ".slot", -1);
    }

    private ClickAction getClickAction(FileConfiguration config, String path, CommandSender sender) {
        ClickAction action;
        String rawAction = config.getString(path + ".click_action", "null");
        if (rawAction.contains(";")) rawAction = rawAction.split(";")[0];

        try {
            action = ClickAction.valueOf(rawAction.toUpperCase());
        } catch (IllegalArgumentException e) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDCLICKACTION, path));
            action = ClickAction.NULL;
        }

        return action;
    }

    private PageType getNavigationPage(FileConfiguration config, String path, CommandSender sender) {
        PageType page;
        String rawPage = config.getString(path + ".click_action", "null");
        if (rawPage.contains(";")) rawPage = rawPage.split(";")[1];

        try {
            page = PageType.valueOf(rawPage.toUpperCase());
        } catch (IllegalArgumentException e) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDPAGE, path));
            page = PageType.NONE;
        }

        return page;
    }

//    private ItemStack createItemStack(String path, CommandSender sender) {
//        String textureInput = config.getString(path + ".value");
//        ItemStack itemStack = createItemStack(Material.PLAYER_HEAD, path);
//
//        return texturesProvider.getTexturedSkull(itemStack, textureInput, sender);
//    }
}
