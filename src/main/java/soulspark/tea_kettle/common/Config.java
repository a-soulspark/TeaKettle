package soulspark.tea_kettle.common;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue QUICK_LATTES;
    public static final ForgeConfigSpec.BooleanValue LIMITED_WATER;
    public static final ForgeConfigSpec.BooleanValue SKIP_TO_DAWN_ZEN;

    static {
        BUILDER.comment("These settings can be tweaked to suit your preferences.", "All values are turned off by default.").push("general");

        QUICK_LATTES = BUILDER.comment("Set to true if lattes should be brewable directly from a cup, without brewing the tea first.").define("quickLattes", false);
        LIMITED_WATER = BUILDER.comment("Set to true if in-world water should be drained when collected by a kettle.", "Water inside fluid containers such as tanks will always be consumed.").define("limitedWater", false);
        SKIP_TO_DAWN_ZEN = BUILDER.comment("Set to true if the Zen effect should skip to dawn instead of dusk when drunk, as before v0.4.0.").define("skipToDawnZen", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
