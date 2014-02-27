package moreinventory.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by c_soda on 14/02/21.
 */
public class PacketStorageBox extends PacketTileEntityBase{

    int count;
    byte face;


    public PacketStorageBox(){}

    public PacketStorageBox(int x, int y, int z, int count, byte face){
        super(x,y,z);
        this.count = count;
        this.face =  face;
    }
    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.encodeInto(ctx, buffer);
        buffer.writeInt(count);
        buffer.writeByte(face);

    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.decodeInto(ctx, buffer);
        this.count = buffer.readInt();
        this.face = buffer.readByte();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null&& tile instanceof TileEntityStorageBox){
            ((TileEntityStorageBox) tile).handlePacket(count,face);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
