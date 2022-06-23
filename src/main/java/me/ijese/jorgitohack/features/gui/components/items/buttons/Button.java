package me.ijese.jorgitohack.features.gui.components.items.buttons;

import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.gui.JorgitoHackGui;
import me.ijese.jorgitohack.features.gui.components.Component;
import me.ijese.jorgitohack.features.gui.components.items.Item;
import me.ijese.jorgitohack.features.modules.client.ClickGui;
import me.ijese.jorgitohack.util.RenderUtil;
import me.ijese.jorgitohack.util.Util;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class Button
extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 12;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().moduleOutline.getValue().booleanValue()) {
            RenderUtil.drawOutlineRect(this.x, this.y, this.x + (float)this.width, this.y + (float)this.height - 0.5f, this.getState() ? -1 : -2007673515);
            RenderUtil.drawOutlineRect(this.x, this.y, this.x + (float)this.width, this.y + (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? JorgitoHack.colorManager.getColorWithAlpha(JorgitoHack.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) : JorgitoHack.colorManager.getColorWithAlpha(JorgitoHack.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        }
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width, this.y + (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? JorgitoHack.colorManager.getColorWithAlpha(JorgitoHack.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) : JorgitoHack.colorManager.getColorWithAlpha(JorgitoHack.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        JorgitoHack.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 4.0f - (float)JorgitoHackGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public int getHeight() {
        return 11;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : JorgitoHackGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY <= this.getY() + (float)this.height;
    }
}

