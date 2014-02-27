package moreinventory.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by c_soda on 14/02/21.
 */
public class PacketSBAddonBaseConfig extends PacketTileEntityBase {

    byte channel;
    boolean flg;

    public PacketSBAddonBaseConfig(){}

    public PacketSBAddonBaseConfig(int x, int y, int z, byte channel, boolean flg){
        super(x,y,z);
        this.channel = channel;
        this.flg = flg;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.encodeInto(ctx, buffer);
        buffer.writeByte(channel);
        buffer.writeBoolean(flg);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.decodeInto(ctx, buffer);
        this.channel = buffer.readByte();
        this.flg = buffer.readBoolean();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null && tile instanceof TileEntitySBAddonBase){
            ((TileEntitySBAddonBase) tile).handleConfigPacketClient(channel, flg);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null && tile instanceof TileEntitySBAddonBase){
            ((TileEntitySBAddonBase) tile).handleConfigPacketServer(channel, flg , player.getDisplayName());
        }
    }
}
