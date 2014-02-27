package moreinventory.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.util.CSutil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by c_soda on 14/02/21.
 */
public class PacketCatchall extends PacketTileEntityBase {

    private ItemStack[] items;

    public PacketCatchall(){};

    public PacketCatchall(int x, int y, int z,ItemStack[] items){
        super(x,y,z);
        this.items = items;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.encodeInto(ctx, buffer);

        buffer.writeInt(items.length);
        for(ItemStack aItem : items){
            CSutil.writeStack(buffer,aItem);
        }

    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        super.decodeInto(ctx, buffer);

        int length = buffer.readInt();
        this.items = new ItemStack[length];
        for(int i =0; i < length; i++){
            items[i] = CSutil.readStack(buffer);
        }

    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        World world = player.getEntityWorld();
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null&&tile instanceof TileEntityCatchall){
            ((TileEntityCatchall) tile).handlePacketData(items);

        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
