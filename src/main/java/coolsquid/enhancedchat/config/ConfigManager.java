package coolsquid.enhancedchat.config;

import java.io.File;
import java.util.*;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;

public class ConfigManager {

	public static final Configuration config = new Configuration(new File("./config/EnhancedChat.cfg"));
	public static boolean allowColorCodes;
	public static boolean ping;
	public static int chatHistoryLimit;
	public static List<String> filters;
	public static Map<String, String> substitutions;
	public static String defaultChatText;
	public static int minimumMessageLength;
	public static boolean copyShortcut;

	public static void loadConfig() {
		config.load();
		allowColorCodes = config.getBoolean("allowColorCodes", "chat", true,
				"Whether to allow the use of color codes or not. (requires mod to be present on both server and client)");
		ping = config.getBoolean("ping", "chat", false,
				"Whether to ping users when their name is @mentioned in chat. (clientside only)");
		chatHistoryLimit = config.getInt("chatLogLimit", "chat", 100, 0, Integer.MAX_VALUE,
				"The maximum number of chat messages to display in the ingame chat history. (clientside only)");
		filters = Lists.newArrayList(
				config.getStringList("filters", "chat", new String[0], "Regex filters that remove chat messages."));
		substitutions = new LinkedHashMap<>();
		String prevSub = null;
		for (String sub : config.getStringList("substitutions", "chat", new String[0],
				"Allows you to replace phrases matching a specified regex with a specified replacement. Write the substitution and the replacement on separate lines. Insert an empty line between each pair. The replacement may use captured groups.")) {
			if (sub.isEmpty()) {
				continue;
			} else if (prevSub == null) {
				prevSub = sub;
			} else {
				substitutions.put(prevSub, sub);
				prevSub = null;
			}
		}
		if (prevSub != null) {
			throw new IllegalArgumentException("Every substitution needs to be followed by a replacement!");
		}
		defaultChatText = config.getString("defaultChatText", "chat", "",
				"The default text in the chat GUI. Clientside only.");
		minimumMessageLength = config.getInt("minimumMessageLength", "chat", 0, 0, 128,
				"The minimum length of chat messages. Serverside only.");
		copyShortcut = config.getBoolean("copyShortcut", "chat", true,
				"Allows you to copy a chat message by clicking it while pressing control.");
		if (config.hasChanged()) {
			config.save();
		}
	}
}