package me.ijese.jorgitohack.features.modules.client;

import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;

public class NickHider
        extends Module {
    private static NickHider instance;
    public final Setting<Boolean> changeOwn = this.register(new Setting<Boolean>("MyName", true));
    public final Setting<String> ownName = this.register(new Setting<Object>("Name", "Name here...", v -> this.changeOwn.getValue()));

    public NickHider() {
        super("NickHider", "Helps with creating NickHider", Module.Category.CLIENT, false, false, false);
        instance = this;
    }

    public static NickHider getInstance() {
        if (instance == null) {
            instance = new NickHider();
        }
        return instance;
    }

    public static String getPlayerName() {
        if (NickHider.fullNullCheck() || !PingBypass.getInstance().isConnected()) {
            return mc.getSession().getUsername();
        }
        String name = PingBypass.getInstance().getPlayerName();
        if (name == null || name.isEmpty()) {
            return mc.getSession().getUsername();
        }
        return name;
    }
}