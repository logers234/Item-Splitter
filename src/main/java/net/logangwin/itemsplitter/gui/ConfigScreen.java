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

    @SuppressWarnings({"unused", "InstantiationOfUtilityClass"})
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public GeneralSettings generalSettings = new GeneralSettings();

    @SuppressWarnings({"unused", "InstantiationOfUtilityClass"})
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public AnimationSettings animationSettings = new AnimationSettings();

    public enum IconState {
        MINIMAL,
        COLORED,
        WHITE
    }

    public static class GeneralSettings {
        @ConfigEntry.Gui.Tooltip()
        public static boolean enableChargeCircle = true;

        @ConfigEntry.Gui.Tooltip()
        public static int timeDelay = 1000;

        @ConfigEntry.Gui.Tooltip()
        public static int splitCircleStartDelay = 100;

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public static IconState iconState = IconState.COLORED;
    }

    public static class AnimationSettings {
        @ConfigEntry.Gui.Tooltip()
        public static boolean enableAnimations = true;

        @ConfigEntry.Gui.Tooltip()
        public static int animationTime = 1000;

        @ConfigEntry.Gui.Tooltip()
        public static float startingOpacity = 0.3f;
    }
}