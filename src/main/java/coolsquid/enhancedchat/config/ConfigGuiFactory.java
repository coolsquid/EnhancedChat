package coolsquid.enhancedchat.config;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import coolsquid.enhancedchat.EnhancedChat;

public class ConfigGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new Gui(parentScreen);
	}

	public static class Gui extends GuiConfig {

		public Gui(GuiScreen parent) {
			super(parent, new ConfigElement(ConfigManager.config.getCategory("chat")).getChildElements(),
					EnhancedChat.MODID, EnhancedChat.MODID, false, false, EnhancedChat.NAME + " configuration",
					ConfigManager.config.getConfigFile().getAbsolutePath());
		}
	}
}