package soulspark.tea_kettle.core.init;

import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.blocks.CupBlock;
import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.common.blocks.TeaBlock;
import soulspark.tea_kettle.common.blocks.TeaBushBlock;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TeaKettle.MODID);
	
	public static final RegistryObject<KettleBlock> KETTLE = BLOCKS.register("kettle", () ->
			new KettleBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.BLUE).hardnessAndResistance(0.5f).sound(SoundType.METAL).notSolid()));
	
	public static final RegistryObject<CupBlock> CUP = BLOCKS.register("cup", () ->
			new CupBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.5f).sound(SoundType.STONE).notSolid()));
	
	public static final RegistryObject<TeaBlock> GREEN_TEA = BLOCKS.register("green_tea", () ->
			new TeaBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.5f).sound(SoundType.STONE).notSolid()));
	
	public static final RegistryObject<TeaBlock> WHITE_TEA = BLOCKS.register("white_tea", () ->
			new TeaBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.5f).sound(SoundType.STONE).notSolid()));
	
	public static final RegistryObject<TeaBlock> OOLONG_TEA = BLOCKS.register("oolong_tea", () ->
			new TeaBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.5f).sound(SoundType.STONE).notSolid()));
	
	public static final RegistryObject<TeaBlock> BLACK_TEA = BLOCKS.register("black_tea", () ->
			new TeaBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.5f).sound(SoundType.STONE).notSolid()));
	
	public static final RegistryObject<TeaBlock> ROSE_TEA = BLOCKS.register("rose_tea", () ->
			new TeaBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.5f).sound(SoundType.STONE).notSolid()));
	
	public static final RegistryObject<TeaBlock> BAMBOO_TEA = BLOCKS.register("bamboo_tea", () ->
			new TeaBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.5f).sound(SoundType.STONE).notSolid()));
	
	public static final RegistryObject<TeaBushBlock> TEA_BUSH = BLOCKS.register("tea_bush", () ->
			new TeaBushBlock(AbstractBlock.Properties.create(Material.PLANTS).tickRandomly().doesNotBlockMovement().sound(SoundType.SWEET_BERRY_BUSH).notSolid()));
}
