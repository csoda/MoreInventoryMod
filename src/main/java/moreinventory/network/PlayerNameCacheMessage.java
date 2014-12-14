package moreinventory.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerNameCacheMessage implements IMessage, IMessageHandler<PlayerNameCacheMessage, IMessage>
{
	private NBTTagCompound data;

	public PlayerNameCacheMessage() {}

	public PlayerNameCacheMessage(NBTTagCompound data)
	{
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, data);
	}

	@Override
	public IMessage onMessage(PlayerNameCacheMessage message, MessageContext ctx)
	{
		MoreInventoryMod.playerNameCache.readFromNBT(message.data);

		return null;
	}
}