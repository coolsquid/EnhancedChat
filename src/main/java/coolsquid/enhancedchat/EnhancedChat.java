package coolsquid.enhancedchat;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import coolsquid.enhancedchat.command.CommandEnhancedChat;
import coolsquid.enhancedchat.command.CommandEnhancedChatClient;
import coolsquid.enhancedchat.config.ConfigManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@Mod(modid = EnhancedChat.MODID, name = EnhancedChat.NAME, version = EnhancedChat.VERSION, dependencies = EnhancedChat.DEPENDENCIES, guiFactory = "coolsquid.enhancedchat.config.ConfigGuiFactory", updateJSON = "https://raw.githubusercontent.com/coolsquid/EnhancedChat/master/version.json", acceptableRemoteVersions = "*")
public class EnhancedChat {

	public static final String MODID = "enhancedchat";
	public static final String NAME = "EnhancedChat";
	public static final String VERSION = "1.1.4";
	public static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,)";

	@Mod.EventHandler
	public void onInit(FMLPreInitializationEvent event) {
		ConfigManager.loadConfig();
		MinecraftForge.EVENT_BUS.register(this);
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			ClientCommandHandler.instance.getCommands().put("enhancedchatclient", new CommandEnhancedChatClient());
		}
	}

	@Mod.EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
		if (event.getServer().isDedicatedServer()) {
			event.registerServerCommand(new CommandEnhancedChat());
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent event) throws IOException {
		if (ConfigManager.ping) {
			String[] words = event.getMessage().getUnformattedText().split("\\s");
			for (String word : words) {
				if (word.startsWith("@") && word.substring(1).equals(Minecraft.getMinecraft().player.getName())) {
					Minecraft.getMinecraft().ingameGUI.displayTitle("Someone pinged you!", "", 2, 5, 5);
				}
			}
		}
		if (!ConfigManager.filters.isEmpty()) {
			for (String filter : ConfigManager.filters) {
				if (Pattern.compile(filter).matcher(event.getMessage().getUnformattedText()).find()) {
					event.setCanceled(true);
					return;
				}
			}
		}
		if (!ConfigManager.substitutions.isEmpty()) {
			String message = event.getMessage().getUnformattedText();
			for (Entry<String, String> e : ConfigManager.substitutions.entrySet()) {
				message = message.replaceAll(e.getKey(), e.getValue());
			}
			event.setMessage(new TextComponentString(message));
		}
	}

	@SubscribeEvent
	public void onChat(ServerChatEvent event) {
		if (event.getMessage().length() < ConfigManager.minimumMessageLength) {
			event.setCanceled(true);
			event.getPlayer().sendMessage(new TextComponentString("<EnhancedChat> The message needs to be at least "
					+ ConfigManager.minimumMessageLength + " chars long!"));
			return;
		}
		if (!ConfigManager.filters.isEmpty()) {
			for (String filter : ConfigManager.filters) {
				if (Pattern.compile(filter).matcher(event.getMessage()).find()) {
					event.setCanceled(true);
					return;
				}
			}
		}
		if (!ConfigManager.substitutions.isEmpty()) {
			String message = event.getMessage();
			for (Entry<String, String> e : ConfigManager.substitutions.entrySet()) {
				message = message.replaceAll(e.getKey(), e.getValue());
			}
			event.setComponent(new TextComponentTranslation("chat.type.text", event.getPlayer().getDisplayName(),
					ForgeHooks.newChatWithLinks(message)));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onGuiOpen(InitGuiEvent.Pre event) {
		if (event.getGui() != null && event.getGui() instanceof GuiChat && !ConfigManager.defaultChatText.isEmpty()) {
			if (((String) ReflectionHelper.getPrivateValue(GuiChat.class, (GuiChat) event.getGui(), 5)).isEmpty()) {
				ReflectionHelper.setPrivateValue(GuiChat.class, (GuiChat) event.getGui(), ConfigManager.defaultChatText,
						5);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMouseClicked(InputEvent.MouseInputEvent event) {
		if (ConfigManager.copyShortcut && Mouse.isButtonDown(0) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
				&& Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
			ITextComponent message = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatComponent(Mouse.getX(),
					Mouse.getY());
			if (message != null) {
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(new StringSelection(message.getUnformattedText()), null);
			}
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(MODID)) {
			ConfigManager.config.save();
			ConfigManager.loadConfig();
		}
	}
}
