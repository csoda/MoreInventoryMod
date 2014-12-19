package moreinventory.core;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.Loader;
import moreinventory.network.OpenUrlMessage;
import moreinventory.util.Version;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.net.URI;
import java.util.List;

public class CommandMoreInventoryMod implements ICommand
{
	@Override
	public int compareTo(Object obj)
	{
		return getCommandName().compareTo(((ICommand)obj).getCommandName());
	}

	@Override
	public String getCommandName()
	{
		return "moreinventorymod";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		throw new CommandNotFoundException();
	}

	@Override
	public List getCommandAliases()
	{
		return Lists.newArrayList("mim");
	}

	@Override
	public void processCommand(ICommandSender sender, final String[] args)
	{
		if (args.length <= 0 || args[0].equalsIgnoreCase("version"))
		{
			ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, MoreInventoryMod.metadata.url);
			IChatComponent component;
			IChatComponent message = new ChatComponentText(" ");

			component = new ChatComponentText("MoreInventoryMod");
			component.getChatStyle().setColor(EnumChatFormatting.GREEN);
			message.appendSibling(component);
			message.appendText(" " + Version.getCurrent());

			if (Version.isDev())
			{
				message.appendText(" ");
				component = new ChatComponentText("dev");
				component.getChatStyle().setColor(EnumChatFormatting.RED);
				message.appendSibling(component);
			}

			message.appendText(" for " + Loader.instance().getMCVersionString() + " ");
			component = new ChatComponentText("(Latest: " + Version.getLatest() + ")");
			component.getChatStyle().setColor(EnumChatFormatting.GRAY);
			message.appendSibling(component);
			message.getChatStyle().setChatClickEvent(click);
			sender.addChatMessage(message);

			message = new ChatComponentText("  ");
			component = new ChatComponentText(MoreInventoryMod.metadata.description);
			component.getChatStyle().setChatClickEvent(click);
			message.appendSibling(component);
			sender.addChatMessage(message);

			message = new ChatComponentText("  ");
			component = new ChatComponentText(MoreInventoryMod.metadata.url);
			component.getChatStyle().setColor(EnumChatFormatting.DARK_GRAY).setChatClickEvent(click);
			message.appendSibling(component);
			sender.addChatMessage(message);
		}
		else if (args[0].equalsIgnoreCase("forum") || args[0].equalsIgnoreCase("url"))
		{
			if (sender instanceof MinecraftServer)
			{
				try
				{
					Desktop.getDesktop().browse(new URI(MoreInventoryMod.metadata.url));
				}
				catch (Exception ignored) {}
			}
			else if (sender instanceof EntityPlayerMP)
			{
				MoreInventoryMod.network.sendTo(new OpenUrlMessage(MoreInventoryMod.metadata.url), (EntityPlayerMP)sender);
			}
		}
		else if ((args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("cfg")) && sender instanceof EntityPlayerMP)
		{
			if (args.length > 2)
			{
				boolean value = CommandBase.parseBoolean(sender, args[2]);
				String uuid = ((EntityPlayerMP)sender).getUniqueID().toString();

				switch (args[1])
				{
					case "isCollectTorch":
						if (value)
						{
							Config.isCollectTorch.add(uuid);
						}
						else
						{
							Config.isCollectTorch.remove(uuid);
						}

						break;
					case "isCollectArrow":
						if (value)
						{
							Config.isCollectArrow.add(uuid);
						}
						else
						{
							Config.isCollectArrow.remove(uuid);
						}

						break;
					case "isFullAutoCollectPouch":
						if (value)
						{
							Config.isFullAutoCollectPouch.add(uuid);
						}
						else
						{
							Config.isFullAutoCollectPouch.remove(uuid);
						}

						break;
					case "leftClickCatchall":
						if (value)
						{
							Config.leftClickCatchall.add(uuid);
						}
						else
						{
							Config.leftClickCatchall.remove(uuid);
						}

						break;
				}
			}
			else
			{
				IChatComponent component = new ChatComponentText("Usage: /moreinventorymod config <key> <true/false>");
				component.getChatStyle().setColor(EnumChatFormatting.RED);

				sender.addChatMessage(component);
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return sender instanceof MinecraftServer || sender instanceof EntityPlayerMP;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		switch (args.length)
		{
			case 1:
				return CommandBase.getListOfStringsMatchingLastWord(args, "version", "forum", "config");
			case 2:
				if (args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("cfg"))
				{
					return CommandBase.getListOfStringsMatchingLastWord(args, "isCollectTorch", "isCollectArrow", "isFullAutoCollectPouch", "leftClickCatchall");
				}
			case 3:
				if (args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("cfg"))
				{
					return CommandBase.getListOfStringsMatchingLastWord(args, "true", "false");
				}
			default:
				return null;
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}
}