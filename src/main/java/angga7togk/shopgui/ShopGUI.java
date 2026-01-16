package angga7togk.shopgui;

import angga7togk.shopgui.command.SGCommand;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

public class ShopGUI extends PluginBase {

    public Config cfg;
    public Config shop;
    public static String prefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        prefix = getConfig().getString("prefix");

        this.saveResource("shop.yml");
        this.shop = new Config(getDataFolder() + "/shop.yml", Config.YAML);
        this.getServer().getCommandMap().register("shop", new SGCommand(this));
    }
}
