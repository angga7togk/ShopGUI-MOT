package angga7togk.shopgui.command;

import angga7togk.shopgui.ShopGUI;
import angga7togk.shopgui.menu.SGMenu;
import angga7togk.shopgui.utils.SGUtils;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;

public class SGCommand extends Command {

    private final ShopGUI plugin;
    public SGCommand(ShopGUI plugin){
        super("shop", "Open the shop menu", "/shop <category>", new String[]{"cshop"});
        this.setPermission("shop.command");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(sender instanceof Player player){
            SGMenu anu = new SGMenu(plugin);
            SGUtils utils = new SGUtils(plugin);
            if(args.length < 1){
                anu.mainShop((Player) sender);
                return false;
            }
            Item itemHand = player.getInventory().getItemInHand();
            String category;
            int price;
            switch (args[0]){
                case "addcategory":
                case "createcategory":
                    if(!player.hasPermission("shop.edit")){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "you dont have permissions");
                        return false;
                    }
                    if(args.length < 2){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "usage: /shop addcategory <category name>");
                        return false;
                    }
                    category = args[1];
                    if(utils.categoryExists(category)){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "category already exists");
                        return false;
                    }

                    if(itemHand.isNull()){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "please held item in hand");
                        return false;
                    }

                    utils.createCategory(category, itemHand);
                    player.sendMessage(ShopGUI.prefix + TextFormat.GREEN + "created new a category : " + category);
                    break;
                case "removecategory":
                case "deletecategory":
                case "delcategory":
                    if(!player.hasPermission("shop.edit")){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "you dont have permissions");
                        return false;
                    }
                    if(args.length < 2){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "usage: /shop removecategory <category name>");
                        return false;
                    }
                    category = args[1];
                    if(!utils.categoryExists(category)){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "category not found");
                        return false;
                    }


                    utils.removeCategory(category);
                    player.sendMessage(ShopGUI.prefix + TextFormat.GREEN + "removed " + category + " category");
                    break;
                case "additem":
                case "sellitem":
                    if(!player.hasPermission("shop.edit")){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "you dont have permissions");
                        return false;
                    }
                    if(args.length < 3){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "usage: /shop additem <category name> <price>");
                        return false;
                    }
                    category = args[1];
                    if(!utils.categoryExists(category)){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "category not found");
                        return false;
                    }

                    try {
                        price = Integer.parseInt(args[2]);
                    }catch (NumberFormatException e){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "price must be a number");
                        return false;
                    }

                    if(itemHand.isNull()){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "please held item in hand");
                        return false;
                    }

                    utils.addItem(category, itemHand, price);
                    player.sendMessage(ShopGUI.prefix + TextFormat.GREEN + "added item " + "\n" +
                            "Successfully added the " + itemHand.getName() + " item for " + price + " in the " + category + " category");
                    break;
                case "removeitem":
                case "deleteitem":
                case "delitem":
                case "unsellitem":
                    if(!player.hasPermission("shop.edit")){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "you dont have permissions");
                        return false;
                    }
                    if(args.length < 2){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "usage: /shop removeitem <category name>");
                        return false;
                    }
                    category = args[1];
                    if(!utils.categoryExists(category)){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "category not found");
                        return false;
                    }

                    if(itemHand.isNull()){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "please held item in hand");
                        return false;
                    }

                    if(!utils.itemExists(category, itemHand)){
                        player.sendMessage(ShopGUI.prefix + TextFormat.RED + "item not found");
                        return false;
                    }

                    utils.removeItem(category, itemHand);
                    player.sendMessage(ShopGUI.prefix + TextFormat.GREEN + "removed " + itemHand.getName() + " in " + category + " category");
                    break;
                default:
                    anu.openCategoryShop((Player) sender, args[0]);
                    break;
            }
        }
        return true;
    }
}
