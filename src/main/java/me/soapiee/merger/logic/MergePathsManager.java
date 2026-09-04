package me.soapiee.merger.logic;

import me.soapiee.merger.Merger;
import me.soapiee.merger.logic.items.MergableItem;
import me.soapiee.merger.managers.ConfigManager;
import me.soapiee.merger.utils.MessageManager;
import me.soapiee.merger.utils.CustomLogger;
import me.soapiee.merger.utils.Message;
import org.bukkit.command.CommandSender;

import java.util.*;

public class MergePathsManager {

    private final CustomLogger customLogger;
    private final MessageManager messageManager;

    private final Set<MergePath> itemPaths = new HashSet<>();
    private final Map<String, List<String>> rawPaths = new HashMap<>();

    public MergePathsManager(Merger main) {
        this.customLogger = main.getCustomLogger();
        this.messageManager = main.getMessageManager();
    }

    public void reload(Merger main, CommandSender sender) {
        validateMergePaths(main, sender);
    }

    public void addRawPath(String identifier, List<String> rawItemIdentifier) {
        rawPaths.put(identifier, rawItemIdentifier);
    }

    public boolean existsRaw(String identifier) {
        for (List<String> rawItemsList : rawPaths.values()) {
            if (rawItemsList.contains(identifier)) {
                return true;
            }
        }

        return false;
    }

    public void validateMergePaths(Merger main, CommandSender sender) {
        ItemsManager itemsManager = main.getItemsManager();
        ConfigManager configManager = main.getConfigManager();

        for (String id : rawPaths.keySet()){
            ArrayList<MergableItem> list = new ArrayList<>();

            for (String rawIdentifier : rawPaths.get(id)){
                if (!itemsManager.isMergableItem(rawIdentifier)) continue;

                list.add(itemsManager.getMergableItem(rawIdentifier));
            }

            if (list.isEmpty()) continue;

            itemPaths.add(new MergePath(list));
            if (configManager.isDebugMode()) {
                customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.MERGEPATHCREATED, id, list));
            }
        }
    }
}
