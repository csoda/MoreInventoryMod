package moreinventory.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import moreinventory.tileentity.TileEntityCatchall;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class CatchallMessage implements IMessage, IMessageHandler<CatchallMessage, IMessage>
{
	private int x, y, z;
	private ItemStack[] items;

	public CatchallMessage() {}

	public CatchallMessage(int x, int y, int z, ItemStack[] items)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.items = items;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		items = new ItemStack[buf.readInt()];

		for (int i = 0; i < items.length; ++i)
		{
			items[i] = ByteBufUtils.readItemStack(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(items.length);

		for (ItemStack itemstack : items)
		{
			ByteBufUtils.writeItemStack(buf, itemstack);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(CatchallMessage message, MessageContext ctx)
	{
		WorldClient world = FMLClientHandler.instance().getWorldClient();
		TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

		if (tile != null && tile instanceof TileEntityCatchall)
		{
			((TileEntityCatchall)tile).handlePacketData(message.items);
		}

		return null;
	}
}