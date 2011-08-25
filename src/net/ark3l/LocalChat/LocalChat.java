package net.ark3l.LocalChat;

import java.util.logging.Logger;

import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.java.JavaPlugin;

public class LocalChat extends JavaPlugin {

	    @Override
	    public void onEnable() {
			Logger logger = Logger.getLogger("Minecraft");
			logger.severe("ID 10 T Error!");
	    }

	    @Override
	    public void onDisable() {
	        getServer().getLogger().info("[KrinMode] KrinMode v1.0 disabled.");
	    }
	}

