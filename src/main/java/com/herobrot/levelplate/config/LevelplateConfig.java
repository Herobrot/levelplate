package com.herobrot.levelplate.config;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "levelplate")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class LevelplateConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public int levelMultiplier = 10;
    @ConfigEntry.Gui.Tooltip
    public String nameColor = "#FFFFFFFF";
    @ConfigEntry.Gui.Tooltip
    public String backgroundColor = "#F52891";
    @ConfigEntry.Gui.Tooltip
    public float backgroundOpacity = 0f;
    @ConfigEntry.Gui.Tooltip
    public float levelplateHeight = 0.5F;
    @ConfigEntry.Gui.Tooltip
    public float levelplateSize = -0.025f;
    public boolean showHealth = false;
    @ConfigEntry.Gui.Tooltip
    public boolean showLevel = true;
    @ConfigEntry.Gui.Tooltip
    public double renderDistance = 30.0;
    @ConfigEntry.Gui.Tooltip
    public boolean showLevelplateIfObstructed = false;
    @ConfigEntry.Gui.Tooltip
    public boolean useScalingDifficultyLvl = true;
    @ConfigEntry.Gui.Tooltip
    public boolean levelTitle = true;
    @ConfigEntry.Gui.Tooltip
    public boolean healthBar = false;
    @ConfigEntry.Gui.Tooltip
    public boolean debugMode = false;

    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Gui.Tooltip
    public ArrayList<String> excludedEntities = new ArrayList<>(List.of("minecraft:ender_dragon", "minecraft:wither"));
}