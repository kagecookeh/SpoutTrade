package net.ark3l.SpoutTrade.Util;

import java.util.logging.Logger;

public class Log {
	
	public static Logger log = Logger.getLogger("Minecraft");
	
	public static void info(String msg) {
		log.info("[SpoutTrade] " + msg);
	}
	
	public static void warning(String msg) {
		log.warning("[SpoutTrade] " + msg);
	}
	
	public static void severe(String msg) {
		log.severe("[SpoutTrade] " + msg);
	}

}
