package soulspark.tea_kettle.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import soulspark.tea_kettle.common.tile_entities.CupTileEntity;

public class CupTileEntityRenderer extends TileEntityRenderer<CupTileEntity> {
	public CupTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	@Override
	// renders the item in the cup
	public void render(CupTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if (renderDispatcher.renderInfo == null || tileEntityIn.getWorld() == null) return;
		BlockPos pos = tileEntityIn.getPos();
		
		// begin transformations
		matrixStack.push();
		// position
		matrixStack.translate(0.5, 0.1875, 0.5);
		matrixStack.scale(0.25F, 0.25F, 0.25F);
		// apply a random rotation to the item based on block position
		int lightAbove = WorldRenderer.getCombinedLight(tileEntityIn.getWorld(), tileEntityIn.getPos().up());
		
		for (int i = 0; i < tileEntityIn.handler.getSlots(); i++) {
			matrixStack.rotate(Vector3f.YP.rotation((pos.getX() * 1024 + pos.getY() * 2048 + pos.getZ() * 5120) * 0.3214f + i * 1.7f));
			
			Minecraft.getInstance().getItemRenderer().renderItem(tileEntityIn.handler.getStackInSlot(i), ItemCameraTransforms.TransformType.FIXED,
					lightAbove, OverlayTexture.NO_OVERLAY, matrixStack, bufferIn);
		}
		// finish transformations
		matrixStack.pop();
	}
}
