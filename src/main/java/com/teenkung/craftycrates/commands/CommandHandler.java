package com.teenkung.craftycrates.commands;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.events.JoinEvent;
import com.teenkung.craftycrates.utils.Functions;
import com.teenkung.craftycrates.utils.selector.ChanceRandomSelector;
import com.teenkung.craftycrates.utils.selector.WeightedRandomSelector;
import com.teenkung.craftycrates.utils.record.ItemPair;
import com.teenkung.craftycrates.utils.record.RarityStorage;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {

            } else if (args[0].equalsIgnoreCase("open")) {
                if (args.length <= 3) {
                    if (ConfigLoader.getBannerIdsList().contains(args[1])) {
                        ArrayList<ItemPair> ret = JoinEvent.getDataManager().get(player).requestPull(args[1], Integer.valueOf(args[2]));
                        Functions.giveItem(ret, player);
                        Functions.dispatchCommand(ret, player);
                    } else {
                        player.sendMessage(colorize("&cUnknown Banner ID, please check your arguments (case sensitive)"));
                    }
                } else {
                    player.sendMessage(colorize("&cInvalid argument! /craftycrates open <bannerID> <amount>"));
                }
            } else if (args[0].equalsIgnoreCase("info")) {

            } else if (args[0].equalsIgnoreCase("log")) {
                
            } else if (args[0].equalsIgnoreCase("reload")) {

            }
        } else {
            ItemStack stack = new ItemStack(Material.DIAMOND_AXE, 2);
            ReadWriteNBT nbt = NBT.itemStackToNBT(stack);
            System.out.println(nbt);

            /*System.out.println(ConfigLoader.getBannerChance("TEST"));
            long start = System.currentTimeMillis();
            int S2 = 0;
            int S3 = 0;
            int S4 = 0;
            int S5 = 0;
            int S6 = 0;
            for (int i = 0; i < 10; i++) {
                String selectedID = ChanceRandomSelector.selectByChance(ConfigLoader.getBannerChance("TEST"));
                RarityStorage storage = ConfigLoader.getRarityStorage("TEST", selectedID);
                String pullID = ConfigLoader.getPullIDByFileName(storage.pool());
                String selectedPoolID = WeightedRandomSelector.select(ConfigLoader.getPullsWeight(pullID));
                String ItemCategory = ConfigLoader.getPoolStorage(selectedID, selectedPoolID).category();
                System.out.println("Randomized: " + selectedID);
                System.out.println("PullID: " + selectedPoolID);
                System.out.println(ItemCategory);



                if (Objects.equals(selectedID, "S2")) {
                    S2++;
                } else if (Objects.equals(selectedID, "S3")) {
                    S3++;
                } else if (Objects.equals(selectedID, "S4")) {
                    S4++;
                } else if (Objects.equals(selectedID, "S5")) {
                    S5++;
                } else if (Objects.equals(selectedID, "S6")) {
                    S6++;
                }
            }
            float S2p = (S2/10000F)*100;
            float S3p = (S3/10000F)*100;
            float S4p = (S4/10000F)*100;
            float S5p = (S5/10000F)*100;
            float S6p = (S6/10000F)*100;
            long end = System.currentTimeMillis();

            System.out.println(colorize("&d"+S2+" | "+S3+" | "+S4+" | "+S5+" | "+S6));
            System.out.println(colorize("&b"+S2p+"% | "+S3p+"% | "+S4p+"% | "+S5p+"% | "+S6p+"%"));
            System.out.println(colorize("&eProcess Time: "+(end-start)+"ms"));*/
        }
        return false;
    }
}
