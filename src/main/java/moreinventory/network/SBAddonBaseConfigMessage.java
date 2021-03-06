package moreinventory.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

public class SBAddonBaseConfigMessage implements IMessage
{
	private int x, y, z;
	private byte channel;
	private boolean isOwner;

	public SBAddonBaseConfigMessage() {}

	public SBAddonBaseConfigMessage(int x, int y, int z, byte channel, boolean isOwner)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.channel = channel;
		this.isOwner = isOwner;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		channel = buf.readByte();
		isOwner = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeByte(channel);
		buf.writeBoolean(isOwner);
	}

	public static class Client implements IMessageHandler<SBAddonBaseConfigMessage, IMessage>
	{
		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(SBAddonBaseConfigMessage message, MessageContext ctx)
		{
			WorldClient world = FMLClientHandler.instance().getWorldClient();
			TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

			if (tile != null && tile instanceof TileEntitySBAddonBase)
			{
				((TileEntitySBAddonBase)tile).handleConfigPacketClient(message.channel, message.isOwner);
			}

			return null;
		}
	}

	public static class Server implements IMessageHandler<SBAddonBaseConfigMessage, IMessage>
	{
		@Override
		public IMessage onMessage(SBAddonBaseConfigMessage message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			WorldServer world = player.getServerForPlayer();
			TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

			if (tile != null && tile instanceof TileEntitySBAddonBase)
			{
				((TileEntitySBAddonBase)tile).handleConfigPacketServer(message.channel, player.getUniqueID().toString());
			}

			return null;
		}
	}
}