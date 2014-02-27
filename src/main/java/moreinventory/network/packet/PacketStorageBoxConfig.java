package moreinventory.network.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by c_soda on 14/02/21.
 */
public class PacketStorageBoxConfig extends PacketTileEntityBase {

    boolean isPrivate;
    boolean checkNBT;
    boolean canInsert;
    int connectCount;

    String owner;

    public PacketStorageBoxConfig(){}

    public PacketStorageBoxConfig(int x, int y, int z,boolean isPrivate, boolean checkNBT, boolean canInsert, int connectCount, String owner){
        super(x,y,z);
        this.isPrivate = isPrivate;
        this.checkNBT = checkNBT;
        this.canInsert = canInsert;
        this.connectCount = connectCount;
        this.owner = owner;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.encodeInto(ctx, buffer);
        buffer.writeBoolean(isPrivate);
        buffer.writeBoolean(checkNBT);
        buffer.writeBoolean(canInsert);
        buffer.writeInt(connectCount);
        ByteBufUtils.writeUTF8String(buffer, owner);

    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.decodeInto(ctx, buffer);
        this.isPrivate = buffer.readBoolean();
        this.checkNBT = buffer.readBoolean();
        this.canInsert = buffer.readBoolean();
        this.connectCount = buffer.readInt();
        this.owner = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null && tile instanceof TileEntityStorageBox){
            ((TileEntityStorageBox) tile).handlePacketConfig(isPrivate, checkNBT, canInsert, connectCount, owner);
        }
    }
}
