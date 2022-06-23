package me.ijese.jorgitohack.features.gui.components.items.buttons;

import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.gui.JorgitoHackGui;
import me.ijese.jorgitohack.features.gui.components.items.buttons.Button;
import me.ijese.jorgitohack.features.modules.client.ClickGui;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class BooleanButton
        extends Button {
    private final Setting setting;

    public BooleanButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4f, this.y + (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? JorgitoHack.colorManager.getColorWithAlpha(JorgitoHack.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) : JorgitoHack.colorManager.getColorWithAlpha(JorgitoHack.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        JorgitoHack.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 4.0f - (float)JorgitoHackGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }

    @Override
    public int getHeight() {
        return 11;
    }

    @Override
    public void toggle() {
        this.setting.setValue((Boolean)this.setting.getValue() == false);
    }

    @Override
    public boolean getState() {
        return (Boolean)this.setting.getValue();
    }
}

