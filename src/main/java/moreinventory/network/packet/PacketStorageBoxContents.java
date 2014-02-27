package moreinventory.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.CSutil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by c_soda on 14/02/21.
 */
public class PacketStorageBoxContents extends PacketTileEntityBase {
    ItemStack itemstack;

    public PacketStorageBoxContents(){};

    public PacketStorageBoxContents(int x, int y, int z, ItemStack itemstack){
        super(x, y, z);
        this.itemstack = itemstack;
    }
    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.encodeInto(ctx, buffer);
        CSutil.writeStack(buffer,itemstack);

    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.decodeInto(ctx, buffer);
        this.itemstack = CSutil.readStack(buffer);
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null&& tile instanceof TileEntityStorageBox){
            ((TileEntityStorageBox) tile).handlePacketContents(this.itemstack);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
