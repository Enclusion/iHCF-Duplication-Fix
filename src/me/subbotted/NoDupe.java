package me.subbotted;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.util.com.google.common.base.Strings;

public class NoDupe extends JavaPlugin implements Listener{

	String MSG_TO_PLAYER;
	String MSG_TO_STAFF;
	String BAN_COMMAND;
	Boolean BAN_ENABLED;
	Boolean MINECART_ENABLED;
	Boolean CHEST_ENABLED;
	
	/*
antidupe_chest_enabled: true
antidupe_minecart_enabled: true

antidupe_message_to_player: '&a[AntiDupe] &rThis dupe glitch has been &c&lpatched&r.'
antidupe_message_to_staff: '&a[AntiDupe] &a[Staff] &c<player> &rattempted to exploit the iHCF dupe glitch!'

antidupe_ban: false
antidupe_ban_command: '/ban <player> &a[AntiDupe] &rAttempting to exploit a duplication glitch'(non-Javadoc)
	 */
	
	public void onEnable(){
		ConsoleCommandSender msg = Bukkit.getServer().getConsoleSender();
		msg.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 50));
		msg.sendMessage(ChatColor.RED + "iHCF AntiDupe v1.0 by subbotted");
		msg.sendMessage(ChatColor.WHITE + "See 'iHCFDupe/config.yml' to configure this plugin.");
		msg.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 50));
		
		this.saveResource("config.yml", false);
		this.getServer().getPluginManager().registerEvents(this, this);
		
		this.MSG_TO_PLAYER = getConfig().getString("antidupe_message_to_player");
		this.MSG_TO_STAFF = getConfig().getString("antidupe_message_to_staff");
		this.BAN_COMMAND = getConfig().getString("antidupe_ban_command");
		this.BAN_ENABLED = getConfig().getBoolean("antidupe_ban");
		this.MINECART_ENABLED = getConfig().getBoolean("antidupe_minecart_enabled");
		this.CHEST_ENABLED = getConfig().getBoolean("antidupe_chest_enabled");
	}
	
	/*
	 * ChatColor.translateAlternateColorCodes('&', stringToColor); shorter alternative
	 */
	public String C(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	/*
	 * Ban Player
	 */
	public void ban(Player p){
		if(BAN_ENABLED){
			ConsoleCommandSender cmdSender = Bukkit.getServer().getConsoleSender();
			getServer().dispatchCommand(cmdSender, this.BAN_COMMAND.replaceAll("<player>", p.getName()));	
		}
	}
	
	/*
	 * Chest Fix
	 */
    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if(!p.getItemInHand().hasItemMeta()){
        	return;
        }
        ItemMeta meta = p.getItemInHand().getItemMeta();
        if (meta.getDisplayName().contains(" Key Reward")){
        	p.sendMessage(C(this.MSG_TO_PLAYER));
        	Bukkit.broadcast(C(this.MSG_TO_STAFF.replaceAll("<player>", e.getPlayer().getName())), "antidupe.alert");
            e.setBuild(false);
            ban(p);
        }
    }
    
    /*
     * Minecart Fix
     */
    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onCraft(CraftItemEvent e){
    	ArrayList<Material> items = new ArrayList<Material>();
    	for(ItemStack item : e.getInventory().getContents()){
    		if(item.getType() == Material.MINECART || item.getType() == Material.CHEST || item.getType() == Material.TRAPPED_CHEST){
    			items.add(item.getType());
    		}
    	}
    	
    	if((items.contains(Material.MINECART) && items.contains(Material.CHEST)) || (items.contains(Material.MINECART) && items.contains(Material.TRAPPED_CHEST))){
    		((Player) e.getWhoClicked()).sendMessage(C(this.MSG_TO_PLAYER));
    		Bukkit.broadcast(C(this.MSG_TO_STAFF.replaceAll("<player>", e.getWhoClicked().getName())), "antidupe.alert");
    		ban((Player) e.getWhoClicked());
    		e.setCancelled(true);
    	}
    }
	
}
