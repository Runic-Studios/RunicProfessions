package com.runicrealms.plugin.professions.shop;

import com.runicrealms.plugin.common.util.ChatUtils;
import com.runicrealms.plugin.item.shops.RunicItemRunnable;
import com.runicrealms.plugin.item.shops.RunicShopGeneric;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import com.runicrealms.plugin.professions.Profession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ProfessionTutorHelper {

    private static final Map<String, Integer> REQUIRED_ITEMS = new HashMap<String, Integer>() {{
        put("coin", 0);
    }};

    @SuppressWarnings("unused")
    public ProfessionTutorHelper() {
        RunicShopGeneric ignoredAT = getAlchemistTutor();
        RunicShopGeneric ignoredBST = getBlacksmithTutor();
        RunicShopGeneric ignoredET = getEnchanterTutor();
        RunicShopGeneric ignoredJT = getJewelerTutor();
    }

    private static ItemStack professionTutorIcon(Profession profession, Material material) {
        ItemStack infoItem = new ItemStack(material);
        ItemMeta meta = infoItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "LEARN PROFESSION - " + profession.getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Learn this profession!");
        lore.add("");
        lore.addAll(ChatUtils.formattedText("&e" + profession.getDescription()));
        lore.add("");
        lore.addAll(ChatUtils.formattedText("&cWarning: This will RESET your current profession!"));
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        infoItem.setItemMeta(meta);
        return infoItem;
    }

    public RunicShopGeneric getAlchemistTutor() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<RunicShopItem>() {{
            add(new RunicShopItem(REQUIRED_ITEMS, professionTutorIcon(Profession.ALCHEMIST, Material.GLASS_BOTTLE), runProfessionTutorBuy(Profession.ALCHEMIST)));
        }};
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Alchemist Tutor", Collections.singletonList(225), shopItems);
    }

    public RunicShopGeneric getBlacksmithTutor() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<RunicShopItem>() {{
            add(new RunicShopItem(REQUIRED_ITEMS, professionTutorIcon(Profession.BLACKSMITH, Material.IRON_CHESTPLATE), runProfessionTutorBuy(Profession.BLACKSMITH)));
        }};
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Blacksmith Tutor", Collections.singletonList(226), shopItems);
    }

    public RunicShopGeneric getEnchanterTutor() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<RunicShopItem>() {{
            add(new RunicShopItem(REQUIRED_ITEMS, professionTutorIcon(Profession.ENCHANTER, Material.PURPLE_DYE), runProfessionTutorBuy(Profession.ENCHANTER)));
        }};
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Enchanter Tutor", Collections.singletonList(228), shopItems);
    }

    public RunicShopGeneric getJewelerTutor() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<RunicShopItem>() {{
            add(new RunicShopItem(REQUIRED_ITEMS, professionTutorIcon(Profession.JEWELER, Material.REDSTONE), runProfessionTutorBuy(Profession.JEWELER)));
        }};
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Jeweler Tutor", Collections.singletonList(230), shopItems);
    }

    private RunicItemRunnable runProfessionTutorBuy(Profession profession) {
        return player -> {
            player.closeInventory();
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                    "profset profession " + player.getName() + " " + profession.getName());
        };
    }
}
