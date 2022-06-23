package me.ijese.jorgitohack.mixin.mixins;

import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.modules.render.Animations;
import org.spongepowered.asm.mixin.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ EntityLivingBase.class })
public class MixinEntityLivingBase {
    @Inject(method = {"getArmSwingAnimationEnd"}, at = {@At("HEAD")}, cancellable = true)
    private void getArmSwingAnimationEnd(final CallbackInfoReturnable<Integer> info) {
        if (JorgitoHack.moduleManager.isModuleEnabled("Animations") && Animations.INSTANCE.changeSwing.getValue()) {
            info.setReturnValue(Animations.INSTANCE.swingDelay.getValue().intValue());
        }
    }
}
