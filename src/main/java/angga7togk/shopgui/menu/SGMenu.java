package angga7togk.shopgui.menu;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.*;

import com.angga7togk.core.Core;
import com.angga7togk.core.api.economy.EconomyService;
import com.angga7togk.core.economy.Transaction;
import com.angga7togk.core.economy.TransactionType;
import com.formconstructor.form.CustomForm;
import com.formconstructor.form.ModalForm;
import com.formconstructor.form.element.custom.Input;
import com.formconstructor.form.element.general.Label;

import angga7togk.shopgui.ShopGUI;

public class SGMenu {

    private final ShopGUI plugin;
    private final Map<Player, FakeInventory> openInventories = new HashMap<>();

    public SGMenu(ShopGUI plugin) {
        this.plugin = plugin;
    }

    public void openCategoryShop(Player player, String category) {
        FakeInventory inv = new FakeInventory(InventoryType.DOUBLE_CHEST);
        inv.setTitle(TextFormat.BOLD + "SHOP | " + category);

        openInventories.put(player, inv);
        fillCategoryPage(player, category, 1);

        player.addWindow(inv);
    }

    public void fillCategoryPage(Player player, String category, int page) {
        FakeInventory inv = openInventories.get(player);
        if (inv == null)
            return;

        inv.clearAll();
        EconomyService economy = Core.getApi().economy();
        Map<Integer, List<String>> pageMap = new HashMap<>();

        int pageI = 1;
        int i = 0;
        pageMap.put(pageI, new ArrayList<>());

        for (String itemString : plugin.shop.getSection("shop." + category).getKeys(false)) {
            pageMap.get(pageI).add(itemString);
            if (i == 35) {
                pageI++;
                i = 0;
                pageMap.put(pageI, new ArrayList<>());
            }
            i++;
        }

        for (String itemKeys : pageMap.get(page)) {
            String[] items = itemKeys.split(":");
            long price = plugin.shop.getLong("shop." + category + "." + itemKeys);
            long tax = economy.calculateTax(price);

            inv.addItem(Item.get(
                    Integer.parseInt(items[0]),
                    Integer.parseInt(items[1])).setLore(
                            TextFormat.GOLD + "Price: " + TextFormat.GREEN + economy.format(price),
                            TextFormat.GOLD + "Tax Rate: " + TextFormat.RED + economy.getTaxRate() + "%%",
                            "",
                            TextFormat.GOLD + "Total: " + TextFormat.BOLD + TextFormat.GREEN
                                    + economy.format((price + tax))));
        }

        inv.setItem(47, Item.get(402, 14, 1).setCustomName("§bPrevious")
                .setLore(TextFormat.YELLOW + "(" + page + "/" + pageMap.size() + ")"));
        inv.setItem(49, Item.get(331).setCustomName("§eBack"));
        inv.setItem(51, Item.get(402, 1, 1).setCustomName("§bNext")
                .setLore(TextFormat.YELLOW + "(" + page + "/" + pageMap.size() + ")"));

        inv.setDefaultItemHandler((item, event) -> {
            event.setCancelled();
            Player p = event.getTransaction().getSource();

            if (item.getCustomName().equals("§bPrevious") && page > 1) {
                fillCategoryPage(p, category, page - 1);
            } else if (item.getCustomName().equals("§bNext") && page < pageMap.size()) {
                fillCategoryPage(p, category, page + 1);
            } else if (item.getCustomName().equals("§eBack")) {
                inv.close(p, () -> mainShop(p), 15);
            } else {
                String itemIds = item.getId() + ":" + item.getDamage();
                long price = plugin.shop.getLong("shop." + category + "." + itemIds);
                inv.close(p, () -> onCheckout(p, Item.get(item.getId(), item.getDamage()), price),
                        10);
            }
        });
    }

    public void mainShop(Player player) {
        FakeInventory inv = new FakeInventory(InventoryType.DOUBLE_CHEST);
        inv.setTitle(TextFormat.BOLD + "SHOP");
        Map<String, String> itemEvent = new HashMap<>();
        for (String category : plugin.shop.getSection("category").getKeys(false)) {
            String[] items = plugin.shop.getString("category." + category).split(":");
            inv.addItem(Item.get(Integer.parseInt(items[0]), Integer.parseInt(items[1]), 1)
                    .setCustomName(TextFormat.AQUA + category));
            itemEvent.put(items[0] + ":" + items[1], category);
        }

        inv.setDefaultItemHandler((item, event) -> {
            event.setCancelled();
            Player target = event.getTransaction().getSource();
            String itemIds = item.getId() + ":" + item.getDamage();
            inv.close(player, () -> openCategoryShop(target, itemEvent.get(itemIds)), 15);
        });
        player.addWindow(inv);
    }

    // public void categoryShop(Player player, String category, int page) {
    // FakeInventory inv = new FakeInventory(InventoryType.DOUBLE_CHEST);
    // inv.setTitle(TextFormat.BOLD + "SHOP | " + category);
    // double myMoney = LlamaEconomy.getAPI().getMoney(player);
    // Map<Integer, Map<String, Double>> pageMap = new HashMap<>();

    // // Membuat Page Map :V
    // int pageI = 1;
    // int i = 0;
    // pageMap.put(pageI, new HashMap<>());
    // for (String itemString : plugin.shop.getSection("shop." +
    // category).getKeys(false)) {
    // pageMap.get(pageI).put(itemString, plugin.shop.getDouble("shop." + category +
    // "." + itemString));
    // if (i == 35) {
    // pageI++;
    // i = 0;
    // pageMap.put(pageI, new HashMap<>());
    // }
    // i++;
    // }

    // // Add item
    // Set<String> keySet = pageMap.get(page).keySet();
    // for (String itemKeys : keySet) {
    // String[] items = itemKeys.split(":");
    // double price = plugin.shop.getDouble("shop." + category + "." + itemKeys);
    // inv.addItem(Item.get(Integer.parseInt(items[0]),
    // Integer.parseInt(items[1])).setLore(
    // TextFormat.GOLD + "My Money, " + TextFormat.GREEN + myMoney,
    // TextFormat.GOLD + "Price, " + TextFormat.GREEN + price));
    // }

    // inv.setItem(47, Item.get(402, 14, 1).setCustomName(TextFormat.AQUA +
    // "Previous")
    // .setLore(TextFormat.YELLOW + "(" + page + "/" + pageMap.size() + ")"));
    // inv.setItem(49, Item.get(331, 0, 1).setCustomName(TextFormat.YELLOW + "Back
    // To Main Shop"));
    // inv.setItem(51, Item.get(402, 1, 1).setCustomName(TextFormat.AQUA + "Next")
    // .setLore(TextFormat.YELLOW + "(" + page + "/" + pageMap.size() + ")"));

    // inv.setDefaultItemHandler((item, event) -> {
    // event.setCancelled();
    // Player target = event.getTransaction().getSource();
    // String itemIds = item.getId() + ":" + item.getDamage();
    // double price = plugin.shop.getLong("shop." + category + "." + itemIds);
    // if (item.getCustomName().equals(TextFormat.AQUA + "Previous")) {
    // if (page > 1) {
    // categoryShop(target, category, page - 1);
    // }
    // } else if (item.getCustomName().equals(TextFormat.AQUA + "Next")) {
    // if (page < pageMap.size()) {
    // categoryShop(target, category, page + 1);
    // }
    // } else if (item.getCustomName().equals(TextFormat.YELLOW + "Back To Main
    // Shop")) {
    // mainShop(target);
    // } else {
    // inv.onClose(player);
    // this.plugin.getServer().getScheduler().scheduleDelayedTask(this.plugin,
    // () -> onCheckout(target, Item.get(item.getId(), item.getDamage()), price),
    // 10);
    // }
    // });

    // this.plugin.getServer().getScheduler().scheduleDelayedTask(this.plugin, () ->
    // {
    // Integer windowId = player.addWindow(inv);
    // playerWindows.put(player, windowId);
    // }, 10);
    // }

    protected void onCheckout(Player player, Item item, long price) {
        CustomForm form = new CustomForm(TextFormat.BOLD + "Checkout");
        form.addElement(new Label("§ehow much do you want to buy?"));
        form.addElement("amount", new Input("Amount", "64"));

        form.setHandler((targetP, res) -> {
            Input amountInput = res.getInput("amount");
            if (amountInput == null) {
                targetP.sendMessage(ShopGUI.prefix + "§cthe form cannot be empty!");
                return;
            }
            if (amountInput.getValue().isBlank()) {
                targetP.sendMessage(ShopGUI.prefix + "§cthe form cannot be empty!");
                return;
            }
            int amount;
            try {
                amount = Integer.parseInt(amountInput.getValue().toString());
            } catch (NumberFormatException e) {
                targetP.sendMessage(ShopGUI.prefix + "§cplease enter numbers!");
                return;
            }

            Inventory inv = targetP.getInventory();
            EconomyService economy = Core.getApi().economy();
            long myMoney = economy.getBalance(targetP.getName());
            if (inv.isFull()) {
                targetP.sendMessage(ShopGUI.prefix + "§cInventory is full!");
                return;
            }

            if (myMoney < (price * amount)) {
                targetP.sendMessage(ShopGUI.prefix + "§cYou dont have enough money!");
                return;
            }

            item.setCount(amount);
            onBuy(targetP, item, (price * amount));
        });
        form.send(player);
    }

    protected void onBuy(Player player, Item item, long totalPrice) {
        EconomyService economy = Core.getApi().economy();
        long tax = economy.calculateTax(totalPrice);
        ModalForm form = new ModalForm(TextFormat.BOLD + "Confirmation");
        form.setContent("§l§ePurchase details\n§rItems, §a" + item.getName() + "\n§rAmount, §a" + item.getCount()
                + "\n§rPrice, §a" + economy.format(totalPrice) + "\n§rPB1, §c"
                + economy.format(tax) + "\n§rTotal, §a"
                + economy.format(totalPrice + tax));
        form.setPositiveButton("§l§aBuy Now");
        form.setNegativeButton("§l§cCancel");

        form.setHandler((targetP, data) -> {
            if (!data) {
                targetP.sendMessage(ShopGUI.prefix + "§corder cancelled");
                return;
            }
            Inventory inv = targetP.getInventory();
            inv.addItem(item);
            economy.purchase(player.getName(), totalPrice);
            economy.getTransactionService()
                    .record(new Transaction(player.getName(), totalPrice, tax, TransactionType.PURCHASE,
                            "ShopGUI Purchase of "
                                    + item.getCount() + " " + item.getName()));
            player.sendMessage(ShopGUI.prefix + "§aSuccessfully to buy " + item.getCount() + " " + item.getName());
        });

        form.send(player);
    }
}
