package com.herobrot.levelplate.config;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "levelplate")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class LevelplateConfig implements ConfigData {

    @Comment("Calculates Level: Real HP / Old HP * multiplier - multiplier + 1\nIf Scaling Difficulty installed: Real HP / Old HP = Health Modificator")
    public int levelMultiplier = 10;
    @Comment("0xAARRGGBB Format")
    public int nameColor = 553648127;
    public int backgroundColor = -1;
    public float backgroundOpacity = 0.4F;
    @Comment("Added onto the mob height")
    public float levelplateHeight = 0.5F;
    @Comment("Smaller float will increase size")
    public float levelplateSize = -0.025f;
    public boolean showHealth = false;
    @Comment("Setting if for example WTHIT/Jade is installed")
    public boolean showLevel = true;
    public double squaredDistance = 128.0D;
    @Comment("Might increase performance when true")
    public boolean showLevelplateIfObstructed = false;
    @Comment("Exclusively use scaling difficulty level when installed")
    public boolean useRpgDifficultyLvl = true;
    @Comment("Travelers title compat")
    public boolean levelTitle = true;
    @Comment("Show a health bar over mobs")
    public boolean healthBar = false;

    @ConfigEntry.Gui.RequiresRestart
    @Comment("Example: minecraft:zombie")
    public ArrayList<String> excludedEntities = new ArrayList<>(List.of("minecraft:ender_dragon", "minecraft:wither"));
}