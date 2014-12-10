package moreinventory.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import moreinventory.tileentity.TileEntityTransportManager;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

public class TransportManagerMessage implements IMessage
{
	private int x, y, z;
	private byte face, topFace, sneak;

	public TransportManagerMessage() {}

	public TransportManagerMessage(int x, int y, int z, byte face, byte topFace, byte sneak)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
		this.topFace = topFace;
		this.sneak = sneak;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		face = buf.readByte();
		topFace = buf.readByte();
		sneak = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeByte(face);
		buf.writeByte(topFace);
		buf.writeByte(sneak);
	}


    public static class Client implements IMessageHandler<TransportManagerMessage, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(TransportManagerMessage message, MessageContext ctx)
        {
            WorldClient world = FMLClientHandler.instance().getWorldClient();
            TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

            if (tile != null && tile instanceof TileEntityTransportManager)
            {
                ((TileEntityTransportManager)tile).handleCommonPacketData(message.face, message.topFace, message.sneak);
            }

            return null;
        }
    }

    public static class Server implements IMessageHandler<TransportManagerMessage, IMessage>
    {
        @Override
        public IMessage onMessage(TransportManagerMessage message, MessageContext ctx)
        {
            WorldServer world = ctx.getServerHandler().playerEntity.getServerForPlayer();
            TileEntity tile = world.getTileEntity(message.x, message.y, message.z);

            if (tile != null && tile instanceof TileEntityTransportManager)
            {
                ((TileEntityTransportManager)tile).handleCommonPacketData(message.face, message.topFace, message.sneak);
            }

            return null;
        }
    }
}