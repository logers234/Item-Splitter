package net.logangwin.itemsplitter.gui;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

@Config(name = "itemsplitter")
public class ConfigScreen implements ConfigData {

    public static ConfigScreen INSTANCE;

    public static void init() {
        AutoConfig.register(ConfigScreen.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ConfigScreen.class).getConfig();
    }

    @ConfigEntry.Gui.Tooltip()
    public boolean enableChargeCircle = true;

    @ConfigEntry.Gui.Tooltip()
    public int timeDelay = 1000;

    @ConfigEntry.Gui.Tooltip()
    public int splitCircleStartDelay = 100;
}