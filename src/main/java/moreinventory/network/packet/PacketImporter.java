package moreinventory.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.tileentity.TileEntityImporter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by c_soda on 14/02/21.
 */
public class PacketImporter extends PacketTileEntityBase{

    boolean isRegister;
    boolean Include;
    boolean channel;

    public PacketImporter(){}

    public PacketImporter(int x, int y, int z, boolean isRegister, boolean Include, boolean channel)
    {
        super(x,y,z);
        this.isRegister = isRegister;
        this.Include = Include;
        this.channel = channel;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.encodeInto(ctx, buffer);
        buffer.writeBoolean(isRegister);
        buffer.writeBoolean(Include);
        buffer.writeBoolean(channel);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.decodeInto(ctx, buffer);
        this.isRegister = buffer.readBoolean();
        this.Include = buffer.readBoolean();
        this.channel = buffer.readBoolean();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null && tile instanceof TileEntityImporter){
            ((TileEntityImporter) tile).handlePacketClient(isRegister,Include, channel);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null && tile instanceof TileEntityImporter){
            ((TileEntityImporter) tile).handlePacketServer(isRegister, Include, channel);
        }
    }
}
