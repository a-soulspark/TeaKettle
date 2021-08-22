package soulspark.tea_kettle.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.Config;

public class ConfigCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation(TeaKettle.MODID, "config_flag");
    private final String path;

    public ConfigCondition(String path)
    {
        this.path = path;
    }

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test()
    {
//        TeaKettle.LOGGER.info(Config.SPEC.getValues().get(path).toString());
        Object config = Config.SPEC.getValues().get(path);
        return config instanceof ForgeConfigSpec.BooleanValue && ((ForgeConfigSpec.BooleanValue) config).get();
    }

    @Override
    public String toString()
    {
        return path;
    }

    public static class Serializer implements IConditionSerializer<ConfigCondition>
    {
        public static final Serializer INSTANCE = new ConfigCondition.Serializer();

        @Override
        public void write(JsonObject json, ConfigCondition value)
        {
            json.addProperty("key", value.path);
        }

        @Override
        public ConfigCondition read(JsonObject json)
        {
            return new ConfigCondition(JSONUtils.getString(json, "path"));
        }

        @Override
        public ResourceLocation getID()
        {
            return ConfigCondition.NAME;
        }
    }
}
