package moreinventory.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import moreinventory.MoreInventoryMod;
import moreinventory.item.inventory.InvPouch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by c_soda on 14/02/21.
 */
public class PacketPouch extends AbstractPacket{

    boolean isCollectedByBox;
    boolean isCollectMainInv;
    boolean isAutoCollect;

    public PacketPouch(){}

    public PacketPouch(boolean isCollectedByBox, boolean isCollectMainInv, boolean isAutoCollect)
    {
        this.isCollectedByBox = isCollectedByBox;
        this.isCollectMainInv = isCollectMainInv;
        this.isAutoCollect = isAutoCollect;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeBoolean(isCollectedByBox);
        buffer.writeBoolean(isCollectMainInv);
        buffer.writeBoolean(isAutoCollect);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        this.isCollectedByBox = buffer.readBoolean();
        this.isCollectMainInv = buffer.readBoolean();
        this.isAutoCollect = buffer.readBoolean();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        ItemStack itemstack = player.getCurrentEquippedItem();
        if (itemstack != null && itemstack.getItem() == MoreInventoryMod.Pouch)
        {
            InvPouch pouch = new InvPouch(player, itemstack);
            pouch.handleClientPacket(isCollectedByBox, isCollectMainInv, isAutoCollect);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        ItemStack itemstack = player.getCurrentEquippedItem();
        if (itemstack != null && itemstack.getItem() == MoreInventoryMod.Pouch)
        {
            InvPouch pouch = new InvPouch(player, itemstack);
            pouch.handleServerPacket(isCollectedByBox, isCollectMainInv, isAutoCollect);
        }
    }
}
