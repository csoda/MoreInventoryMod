package moreinventory.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import moreinventory.entity.EntityMinecartStorageBox;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class StorageBoxMessage implements IMessage, IMessageHandler<StorageBoxMessage, IMessage>
{
	private int x, y, z, count, entityId;
	private byte face;
	private String typeName;

	public StorageBoxMessage() {}

	public StorageBoxMessage(int x, int y, int z, int count, byte face, String typeName)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.count = count;
		this.face = face;
		this.typeName = typeName;
	}

	public StorageBoxMessage(int id, int count, byte face, String typeName)
	{
		this.entityId = id;
		this.count = count;
		this.face = face;
		this.typeName = typeName;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		count = buf.readInt();
		entityId = buf.readInt();
		face = buf.readByte();
		typeName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(count);
		buf.writeInt(entityId);
		buf.writeByte(face);
		ByteBufUtils.writeUTF8String(buf, typeName);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(StorageBoxMessage message, MessageContext ctx)
	{
		WorldClient world = FMLClientHandler.instance().getWorldClient();

		if (message.entityId > 0)
		{
			Entity entity = world.getEntityByID(message.entityId);

			if (entity != null && entity instanceof EntityMinecartStorageBox)
			{
				((EntityMinecartStorageBox)entity).handlePacket(message.count, message.face, message.typeName);
			}
		}
		else
		{
			TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

			if (tile != null && tile instanceof TileEntityStorageBox)
			{
				((TileEntityStorageBox)tile).handlePacket(message.count, message.face, message.typeName);
			}
		}

		return null;
	}
}