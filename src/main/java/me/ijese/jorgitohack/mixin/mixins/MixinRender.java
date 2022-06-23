package me.ijese.jorgitohack.mixin.mixins;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = {Render.class})
public
class MixinRender< T extends Entity > {
    //@Inject(method = {"shouldRender"}, at = {@At(value = "HEAD")}, cancellable = true)
    /**
     * @author iJese
     */
    @Overwrite
    public
    boolean shouldRender ( T livingEntity , ICamera camera , double camX , double camY , double camZ /*, CallbackInfoReturnable < Boolean > info*/ ) {
        //if ( livingEntity == null || camera == null ) {
        //    info.setReturnValue ( false );
        //} else {
        //    livingEntity.getRenderBoundingBox ( );
        //}
        try {
            AxisAlignedBB axisalignedbb = livingEntity.getRenderBoundingBox().grow(0.5D);

            if (axisalignedbb.hasNaN() || axisalignedbb.getAverageEdgeLength() == 0.0D)
            {
                if ( livingEntity != null )
                    axisalignedbb = new AxisAlignedBB(((Entity)livingEntity).posX - 2.0D, ((Entity)livingEntity).posY - 2.0D, ((Entity)livingEntity).posZ - 2.0D, ((Entity)livingEntity).posX + 2.0D, ((Entity)livingEntity).posY + 2.0D, ((Entity)livingEntity).posZ + 2.0D);
            }

            return (livingEntity.isInRangeToRender3d(camX, camY, camZ) && (((Entity)livingEntity).ignoreFrustumCheck || camera.isBoundingBoxInFrustum(axisalignedbb)));
        } catch ( Exception ignored ) {
            return false;
        }

    }
}