package me.yhamarsheh.bridgersumo.managers;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.attributes.Disableable;
import me.yhamarsheh.bridgersumo.enums.EventType;
import me.yhamarsheh.bridgersumo.objects.EventItem;
import me.yhamarsheh.bridgersumo.utilities.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventsManager implements Disableable {

    private final BridgerSumo plugin;

    private final List<EventItem> minorItems;
    private final List<EventItem> majorItems;

    private final File file;
    private final YamlConfiguration configuration;

    public EventsManager(BridgerSumo plugin) {
        this.plugin = plugin;

        this.file = new File(plugin.getDataFolder(), "event_items.yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);

        this.minorItems = new ArrayList<>();
        this.majorItems = new ArrayList<>();

        init();
    }

    private void init() {
        if (configuration.getConfigurationSection("minor_events") != null) {
            for (String s : configuration.getConfigurationSection("minor_events").getKeys(false)) {
                minorItems.add(new EventItem(ItemStack.deserialize(configuration.getConfigurationSection("minor_events." + s + ".item").getValues(true)),
                        configuration.getString("minor_events." + s + ".displayName")));
            }
        }

        if (configuration.getConfigurationSection("major_events") != null) {
            for (String s : configuration.getConfigurationSection("major_events").getKeys(false)) {
                majorItems.add(new EventItem(ItemStack.deserialize(configuration.getConfigurationSection("major_events." + s + ".item").getValues(true)),
                        configuration.getString("major_events." + s + ".displayName")));
            }
        }
    }

    public void addItem(EventType eventType, ItemStack itemStack, String displayName) {
        switch (eventType) {
            case MINOR:
                minorItems.add(new EventItem(itemStack, displayName));

                configuration.set("minor_events." + (minorItems.size()) + ".displayName", displayName);
                configuration.set("minor_events." + (minorItems.size()) + ".item", itemStack.serialize());
                break;
            case MAJOR:
                majorItems.add(new EventItem(itemStack, displayName));

                configuration.set("major_events." + (minorItems.size()) + ".displayName", displayName);
                configuration.set("major_events." + (minorItems.size()), itemStack.serialize());
                break;
        }

        try {
            configuration.save(file);
        } catch (IOException ex) {
            BridgerSumo.LOGGER.error(Logger.Reason.CONFIG, "Couldn't save the item to the events items of type " + eventType.name());
        }
    }

    public List<EventItem> getItems(EventType eventType) {
        switch (eventType) {
            case MINOR:
                return minorItems;
            case MAJOR:
                return majorItems;
        }

        return null;
    }

    @Override
    public void disable() {
        minorItems.clear();
        majorItems.clear();
    }
}
