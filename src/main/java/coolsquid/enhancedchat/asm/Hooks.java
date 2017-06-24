package coolsquid.enhancedchat.asm;

import coolsquid.enhancedchat.config.ConfigManager;

public class Hooks {

	public static boolean isAllowedCharacter(char c) {
		return c >= 32 && c != 127 && (ConfigManager.allowColorCodes ? true : c != 167);
	}

	public static int getChatHistoryLimit() {
		return ConfigManager.chatHistoryLimit;
	}
}