package moreinventory.network;

import io.netty.buffer.ByteBuf;
import moreinventory.tileentity.TileEntityImporter;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ImporterMessage implements IMessage
{
	private int x, y, z;
	private boolean register, include, channel;

	public ImporterMessage() {}

	public ImporterMessage(int x, int y, int z, boolean register, boolean include, boolean channel)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.register = register;
		this.include = include;
		this.channel = channel;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		register = buf.readBoolean();
		include = buf.readBoolean();
		channel = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeBoolean(register);
		buf.writeBoolean(include);
		buf.writeBoolean(channel);
	}

	public static class Client implements IMessageHandler<ImporterMessage, IMessage>
	{
		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(ImporterMessage message, MessageContext ctx)
		{
			WorldClient world = FMLClientHandler.instance().getWorldClient();
			TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

			if (tile != null && tile instanceof TileEntityImporter)
			{
				((TileEntityImporter)tile).handlePacketClient(message.register, message.include, message.channel);
			}

			return null;
		}
	}

	public static class Server implements IMessageHandler<ImporterMessage, IMessage>
	{
		@Override
		public IMessage onMessage(ImporterMessage message, MessageContext ctx)
		{
			WorldServer world = ctx.getServerHandler().playerEntity.getServerForPlayer();
			TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

			if (tile != null && tile instanceof TileEntityImporter)
			{
				((TileEntityImporter)tile).handlePacketServer(message.register, message.include, message.channel);
			}

			return null;
		}
	}
}