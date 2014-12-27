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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class StorageBoxContentsMessage implements IMessage, IMessageHandler<StorageBoxContentsMessage, IMessage>
{
	private int x, y, z, entityId;
	private ItemStack itemstack;

	public StorageBoxContentsMessage() {}

	public StorageBoxContentsMessage(int x, int y, int z, ItemStack itemstack)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.itemstack = itemstack;
	}

	public StorageBoxContentsMessage(int id, ItemStack itemstack)
	{
		this.entityId = id;
		this.itemstack = itemstack;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		entityId = buf.readInt();
		itemstack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(entityId);
		ByteBufUtils.writeItemStack(buf, itemstack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(StorageBoxContentsMessage message, MessageContext ctx)
	{
		WorldClient world = FMLClientHandler.instance().getWorldClient();

		if (message.entityId > 0)
		{
			Entity entity = world.getEntityByID(message.entityId);

			if (entity != null && entity instanceof EntityMinecartStorageBox)
			{
				((EntityMinecartStorageBox)entity).handlePacketContents(message.itemstack);
			}
		}
		else
		{
			TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

			if (tile != null && tile instanceof TileEntityStorageBox)
			{
				((TileEntityStorageBox)tile).handlePacketContents(message.itemstack);
			}
		}

		return null;
	}
}