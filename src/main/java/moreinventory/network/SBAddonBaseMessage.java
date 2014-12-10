package moreinventory.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;

public class SBAddonBaseMessage implements IMessage, IMessageHandler<SBAddonBaseMessage, IMessage>
{
	private int x, y, z;
	private boolean isPrivate;
	private String owner;

	public SBAddonBaseMessage() {}

	public SBAddonBaseMessage(int x, int y, int z, boolean isPrivate, String owner)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.isPrivate = isPrivate;
		this.owner = owner;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		isPrivate = buf.readBoolean();
		owner = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeBoolean(isPrivate);
		ByteBufUtils.writeUTF8String(buf, owner);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(SBAddonBaseMessage message, MessageContext ctx)
	{
		WorldClient world = FMLClientHandler.instance().getWorldClient();
		TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

		if (tile != null && tile instanceof TileEntitySBAddonBase)
		{
			((TileEntitySBAddonBase)tile).handlePacket(message.isPrivate, message.owner);
		}

		return null;
	}
}