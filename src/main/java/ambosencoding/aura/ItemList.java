package ambosencoding.aura;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemList {

    public static ItemWrapper TEAM_SELECTOR = ItemWrapper.get(getItem(Material.BED, "§eTeam auswählen §7(Rechtsklick)"));
    public static ItemWrapper KNOCKBACK_STICK = ItemWrapper.get(getItem(Material.STICK, "§bStick", Enchantment.KNOCKBACK, 4));
    public static ItemWrapper TELEPORT_PEARL = ItemWrapper.get(getItem(Material.ENDER_PEARL, "§cTeleporter"));
    public static ItemWrapper PUMPKIN_PIE = ItemWrapper.get(getItem(Material.PUMPKIN_PIE, "§cKürbiskuchen"));
    public static ItemWrapper ROD = ItemWrapper.get(getItem(Material.FISHING_ROD, "§bAngel"));
    public static ItemWrapper EGG = ItemWrapper.get(getItem(Material.EGG, "§bEi"));

    public static ItemWrapper HEALING_POTION = ItemWrapper.get(getItem(Material.POTION, null, (short) 16453));
    public static ItemWrapper POISION_POTION = ItemWrapper.get(getItem(Material.POTION, null, (short) 16388));
    public static ItemWrapper REGENERATION_POTION = ItemWrapper.get(getItem(Material.POTION, null, (short) 16385));
    public static ItemWrapper HARMING_POTION = ItemWrapper.get(getItem(Material.POTION, null, (short) 16460));

    public static ItemWrapper HELMET = ItemWrapper.get(getItem(Material.IRON_HELMET, "§bHelm"));
    public static ItemWrapper CHESTPLATE = ItemWrapper.get(getItem(Material.IRON_CHESTPLATE, "§bBrustpanzer"));
    public static ItemWrapper LEGGINGS = ItemWrapper.get(getItem(Material.IRON_LEGGINGS, "§bHose"));
    public static ItemWrapper BOOTS = ItemWrapper.get(getItem(Material.DIAMOND_BOOTS, "§bSchuhe", Enchantment.PROTECTION_FALL, 10));

    public static ItemWrapper MENU_WOOL = ItemWrapper.get(getItem(Material.WOOL, null));

    private static ItemStack getItem(Material mat, String displayName, String... lore) {
        return getItem(mat, displayName, (short) 0, lore);
    }

    private static ItemStack getItem(Material mat, String displayName, short durability, String... lore) {
        ItemStack item = new ItemStack(mat, 1, durability);
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getItem(Material mat, String displayName, Enchantment enchantment, int level) {
        ItemStack item = getItem(mat, displayName);
        item.addUnsafeEnchantment(enchantment, level);
        return item;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ItemWrapper {

        private final ItemStack item;

        private static ItemWrapper get(ItemStack item) {
            return new ItemWrapper(item);
        }

        public ItemStack get() {
            return get(1);
        }

        public ItemStack get(int amount) {
            ItemStack cloned = item.clone();
            cloned.setAmount(amount);
            return cloned;
        }

        public ItemStack getWithDurabilityLoreAndName(int durability, String name, String... lore) {
            ItemStack item = get(1);
            item.setDurability((short) durability);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList(lore));
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            return item;
        }

    }

}
