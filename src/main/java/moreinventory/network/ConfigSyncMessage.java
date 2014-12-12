package moreinventory.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moreinventory.core.Config;
import net.minecraft.entity.player.EntityPlayerMP;

public class ConfigSyncMessage implements IMessage, IMessageHandler<ConfigSyncMessage, IMessage>
{
	private boolean isCollectTorch, isCollectArrow, isFullAutoCollectPouch, leftClickCatchall;

	public ConfigSyncMessage() {}

	public ConfigSyncMessage(boolean isCollectTorch, boolean isCollectArrow, boolean isFullAutoCollectPouch, boolean leftClickCatchall)
	{
		this.isCollectTorch = isCollectTorch;
		this.isCollectArrow = isCollectArrow;
		this.isFullAutoCollectPouch = isFullAutoCollectPouch;
		this.leftClickCatchall = leftClickCatchall;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		isCollectTorch = buf.readBoolean();
		isCollectArrow = buf.readBoolean();
		isFullAutoCollectPouch = buf.readBoolean();
		leftClickCatchall = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(isCollectTorch);
		buf.writeBoolean(isCollectArrow);
		buf.writeBoolean(isFullAutoCollectPouch);
		buf.writeBoolean(leftClickCatchall);
	}

	@Override
	public IMessage onMessage(ConfigSyncMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		String uuid = player.getUniqueID().toString();

		if (message.isCollectTorch)
		{
			Config.isCollectTorch.add(uuid);
		}
		else
		{
			Config.isCollectTorch.remove(uuid);
		}

		if (message.isCollectArrow)
		{
			Config.isCollectArrow.add(uuid);
		}
		else
		{
			Config.isCollectArrow.remove(uuid);
		}

		if (message.isFullAutoCollectPouch)
		{
			Config.isFullAutoCollectPouch.add(uuid);
		}
		else
		{
			Config.isFullAutoCollectPouch.remove(uuid);
		}

		if (message.leftClickCatchall)
		{
			Config.leftClickCatchall.add(uuid);
		}
		else
		{
			Config.leftClickCatchall.remove(uuid);
		}

		return null;
	}
}