package moreinventory.network.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by c_soda on 14/02/21.
 */
public class PacketSBAddonBase extends PacketTileEntityBase{

    private boolean isPrivate;
    private String owner;

    public PacketSBAddonBase(){}

    public PacketSBAddonBase(int x, int y, int z, boolean isPrivate, String owner){
        super(x,y,z);
        this.isPrivate = isPrivate;
        this.owner = owner;
    }
    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.encodeInto(ctx, buffer);
        buffer.writeBoolean(isPrivate);
        ByteBufUtils.writeUTF8String(buffer, owner);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.decodeInto(ctx, buffer);
        this.isPrivate = buffer.readBoolean();
        this.owner = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null&& tile instanceof TileEntitySBAddonBase){
            ((TileEntitySBAddonBase) tile).handlePacket(isPrivate, owner);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
