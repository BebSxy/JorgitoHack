package me.ijese.jorgitohack.features.modules.misc;

import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Bind;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.TextUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;

public class KitDelete
        extends Module {
    private final Setting<Bind> deleteKey = this.register(new Setting<Bind>("Key", new Bind(-1)));
    private boolean keyDown;

    public KitDelete() {
        super("KitDelete", "Automates /deleteukit", Module.Category.MISC, false, false, false);
    }

    @Override
    public void onTick() {
        if (this.deleteKey.getValue().getKey() != -1) {
            if (KitDelete.mc.currentScreen instanceof GuiContainer && Keyboard.isKeyDown((int)this.deleteKey.getValue().getKey())) {
                Slot slot = ((GuiContainer)KitDelete.mc.currentScreen).getSlotUnderMouse();
                if (slot != null && !this.keyDown) {
                    KitDelete.mc.player.sendChatMessage("/deleteukit " + TextUtil.stripColor(slot.getStack().getDisplayName()));
                    this.keyDown = true;
                }
            } else if (this.keyDown) {
                this.keyDown = false;
            }
        }
    }
}

