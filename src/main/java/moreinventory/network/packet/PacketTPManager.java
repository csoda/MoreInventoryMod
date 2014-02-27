package moreinventory.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.tileentity.TileEntityTransportManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by c_soda on 14/02/21.
 */
public class PacketTPManager extends PacketTileEntityBase{

    byte face;
    byte topface;
    byte sneak;

    public PacketTPManager(){}

    public PacketTPManager(int x, int y, int z, byte face, byte topface, byte sneak){
        super(x,y,z);
        this.face = face;
        this.topface = topface;
        this.sneak = sneak;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.encodeInto(ctx, buffer);
        buffer.writeByte(face);
        buffer.writeByte(topface);
        buffer.writeByte(sneak);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.decodeInto(ctx, buffer);
        this.face = buffer.readByte();
        this.topface = buffer.readByte();
        this.sneak = buffer.readByte();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null && tile instanceof TileEntityTransportManager){
                ((TileEntityTransportManager) tile).handleCommonPacketData(face, topface, sneak);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
