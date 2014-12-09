package moreinventory.network;

import io.netty.buffer.ByteBuf;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class StorageBoxConfigMessage implements IMessage, IMessageHandler<StorageBoxConfigMessage, IMessage>
{
	private int x, y, z, connect;
	private boolean isPrivate, checkNBT, canInsert;
	private String owner;

	public StorageBoxConfigMessage() {}

	public StorageBoxConfigMessage(int x, int y, int z, boolean isPrivate, boolean checkNBT, boolean canInsert, int connect, String owner)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.isPrivate = isPrivate;
		this.checkNBT = checkNBT;
		this.canInsert = canInsert;
		this.connect = connect;
		this.owner = owner;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		isPrivate = buf.readBoolean();
		checkNBT = buf.readBoolean();
		canInsert = buf.readBoolean();
		connect = buf.readInt();
		owner = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeBoolean(isPrivate);
		buf.writeBoolean(checkNBT);
		buf.writeBoolean(canInsert);
		buf.writeInt(connect);
		ByteBufUtils.writeUTF8String(buf, owner);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(StorageBoxConfigMessage message, MessageContext ctx)
	{
		WorldClient world = FMLClientHandler.instance().getWorldClient();
		TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

		if (tile != null && tile instanceof TileEntityStorageBox)
		{
			((TileEntityStorageBox)tile).handlePacketConfig(message.isPrivate, message.checkNBT, message.canInsert, message.connect, message.owner);
		}

		return null;
	}
}