package coolsquid.enhancedchat.command;

import java.util.Arrays;
import java.util.List;

import coolsquid.enhancedchat.config.ConfigManager;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandEnhancedChat extends CommandBase {

	@Override
	public String getName() {
		return "enhancedchat";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) {
			sender.sendMessage(msg("<EnhancedChat> Missing arguments.", new Style().setColor(TextFormatting.BLUE)));
			return;
		}
		if (args[0].equals("reload")) {
			ConfigManager.loadConfig();
			sender.sendMessage(
					msg("<EnhancedChat> Reloaded the config.", new Style().setColor(TextFormatting.BLUE)));
		} else if (args[0].equals("clear")) {
			for (int i = 0; i < 1000; i++) {
				FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
						.sendMessage(new TextComponentString(" "));
			}
			sender.sendMessage(msg("<EnhancedChat> Cleared the chat.", new Style().setColor(TextFormatting.BLUE)));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos pos) {
		return Arrays.asList("reload", "clear");
	}

	private static TextComponentString msg(String message, Style style) {
		return (TextComponentString) new TextComponentString(message).setStyle(style);
	}
}