package moreinventory.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

public class StorageBoxButtonMessage implements IMessage, IMessageHandler<StorageBoxButtonMessage, IMessage>
{
	private int x, y, z;
	private byte channel;

	public StorageBoxButtonMessage() {}

	public StorageBoxButtonMessage(int x, int y, int z, byte channel)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.channel = channel;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		channel = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeByte(channel);
	}

	@Override
	public IMessage onMessage(StorageBoxButtonMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		WorldServer world = player.getServerForPlayer();
		TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

		if (tile != null && tile instanceof TileEntityStorageBox)
		{
			((TileEntityStorageBox)tile).handlePacketButton(message.channel, player.getUniqueID().toString());
		}

		return null;
	}
}