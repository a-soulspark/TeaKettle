package soulspark.tea_kettle.core.init;

import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.effects.ModEffect;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects {
	public static DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, TeaKettle.MODID);
	
	public static RegistryObject<Effect> CAFFEINE = EFFECTS.register("caffeine", () -> new ModEffect(EffectType.BENEFICIAL, 7881519)
		.addAttributesModifier(Attributes.MOVEMENT_SPEED, "607eddba-6deb-4a65-a09a-235011f13b0d", 0.06f, AttributeModifier.Operation.MULTIPLY_TOTAL));
	
	public static RegistryObject<Effect> ZEN = EFFECTS.register("zen", () -> new ModEffect(EffectType.BENEFICIAL, 426458));
}
