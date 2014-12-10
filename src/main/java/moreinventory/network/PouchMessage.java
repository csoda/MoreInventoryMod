package moreinventory.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PouchMessage implements IMessage
{
	private boolean isCollectedByBox;
	private boolean isCollectMainInv;
	private boolean isAutoCollect;

	public PouchMessage() {}

	public PouchMessage(boolean isCollectedByBox, boolean isCollectMainInv, boolean isAutoCollect)
	{
		this.isCollectedByBox = isCollectedByBox;
		this.isCollectMainInv = isCollectMainInv;
		this.isAutoCollect = isAutoCollect;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		isCollectedByBox = buf.readBoolean();
		isCollectMainInv = buf.readBoolean();
		isAutoCollect = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(isCollectedByBox);
		buf.writeBoolean(isCollectMainInv);
		buf.writeBoolean(isAutoCollect);
	}

	public static class Client implements IMessageHandler<PouchMessage, IMessage>
	{
		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(PouchMessage message, MessageContext ctx)
		{
			EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
			ItemStack itemstack = player.getCurrentEquippedItem();

			if (itemstack != null && itemstack.getItem() == MoreInventoryMod.pouch)
			{
				InventoryPouch pouch = new InventoryPouch(player, itemstack);

				pouch.handleClientPacket(message.isCollectedByBox, message.isCollectMainInv, message.isAutoCollect);
			}

			return null;
		}
	}

	public static class Server implements IMessageHandler<PouchMessage, IMessage>
	{
		@Override
		public IMessage onMessage(PouchMessage message, MessageContext ctx)
		{
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack itemstack = player.getCurrentEquippedItem();

			if (itemstack != null && itemstack.getItem() == MoreInventoryMod.pouch)
			{
				InventoryPouch pouch = new InventoryPouch(player, itemstack);

				pouch.handleServerPacket(message.isCollectedByBox, message.isCollectMainInv, message.isAutoCollect);
			}

			return null;
		}
	}
}