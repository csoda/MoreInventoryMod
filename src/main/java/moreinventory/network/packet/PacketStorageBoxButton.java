package moreinventory.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by c_soda on 14/02/22.
 */
public class PacketStorageBoxButton extends PacketTileEntityBase{

    private byte channel;

    public PacketStorageBoxButton(){}

    public PacketStorageBoxButton(int x, int y, int z, byte channel){
        super(x,y,z);
        this.channel = channel;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.encodeInto(ctx, buffer);
        buffer.writeByte(channel);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.decodeInto(ctx, buffer);
        this.channel = buffer.readByte();
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null && tile instanceof  TileEntityStorageBox){
            ((TileEntityStorageBox) tile).handlePacketButton(channel, player.getDisplayName());
        }
    }
}
