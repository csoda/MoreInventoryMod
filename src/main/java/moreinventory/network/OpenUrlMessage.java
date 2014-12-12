package moreinventory.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.awt.*;
import java.net.URI;

public class OpenUrlMessage implements IMessage, IMessageHandler<OpenUrlMessage, IMessage>
{
	private String url;

	public OpenUrlMessage() {}

	public OpenUrlMessage(String url)
	{
		this.url = url;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		url = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, url);
	}

	@Override
	public IMessage onMessage(OpenUrlMessage message, MessageContext ctx)
	{
		try
		{
			Desktop.getDesktop().browse(new URI(message.url));
		}
		catch (Exception ignored) {}

		return null;
	}
}