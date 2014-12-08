package moreinventory.network;

import io.netty.buffer.ByteBuf;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SBAddonBaseConfigMessage implements IMessage
{
	private int x, y, z;
	private byte channel;
	private boolean flag;

	public SBAddonBaseConfigMessage() {}

	public SBAddonBaseConfigMessage(int x, int y, int z, byte channel, boolean flag)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.channel = channel;
		this.flag = flag;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		channel = buf.readByte();
		flag = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeByte(channel);
		buf.writeBoolean(flag);
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
				((TileEntitySBAddonBase)tile).handleConfigPacketClient(message.channel, message.flag);
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
				((TileEntitySBAddonBase)tile).handleConfigPacketServer(message.channel, message.flag, player.getDisplayName());
			}

			return null;
		}
	}
}