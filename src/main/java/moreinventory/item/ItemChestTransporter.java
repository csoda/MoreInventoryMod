package moreinventory.item;

import java.util.List;
import java.util.Map;

import moreinventory.MoreInventoryMod;
import moreinventory.item.inventory.InventoryChestTransporter;
import moreinventory.tileentity.storagebox.StorageBoxType;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemChestTransporter extends Item
{
	private TileEntity tileEntity;

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemChestTransporter()
	{
		super();
		setMaxStackSize(1);
		setCreativeTab(MoreInventoryMod.customTab);
		setHasSubtypes(true);
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool)
	{
		int dm = itemstack.getItemDamage();
		if (3 <= dm && dm <= 18)
		{
			InventoryChestTransporter invChestTP = new InventoryChestTransporter(itemstack);
			list.add("\"" + invChestTP.getContentsItemName(itemstack) + "\"");
		}
	}

	@Override
	public boolean onItemUseFirst(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
			int par5, int par6, int par7, float par8, float par9, float par10)
	{
		if (par3World.isRemote)
		{
			return false;
		}

		if (par1ItemStack.getItemDamage() == 0)
		{
			Block onBlock = par3World.getBlock(par4, par5, par6);
			int onBlockDamage = par3World.getBlockMetadata(par4, par5, par6);
			if (MoreInventoryMod.transportableChest.containsKey(onBlock.getUnlocalizedName()))
			{
				tileEntity = par3World.getTileEntity(par4, par5, par6);
				if (tileEntity == null || !(tileEntity instanceof IInventory)
						|| ((IInventory) tileEntity).getSizeInventory() < 27)
				{
					return true;
				}
			}
			else
			{
				return false;
			}

			InventoryChestTransporter invChestTP = new InventoryChestTransporter(par1ItemStack);
			if (invChestTP.transferToPlayer(tileEntity))
			{
				if (onBlock.equals(Blocks.chest))
					par1ItemStack.setItemDamage(1);
				else if (onBlock.equals(Blocks.trapped_chest))
					par1ItemStack.setItemDamage(2);
				else if (onBlock.equals(MoreInventoryMod.StorageBox))
					par1ItemStack.setItemDamage(onBlockDamage + 3);
				else
				{
					Map<Integer, Integer> IconMap = MoreInventoryMod.transportableChestIcon.get(onBlock
							.getUnlocalizedName());
					if (IconMap.containsKey(onBlockDamage))
					{
						par1ItemStack.setItemDamage(IconMap.get(onBlockDamage) + 19);
					}
					else if (IconMap.containsKey(-1))
					{
						par1ItemStack.setItemDamage(IconMap.get(-1) + 19);
					}
					else
					{
						par1ItemStack.setItemDamage(19);
					}
				}
				par3World.setBlockToAir(par4, par5, par6);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
			int par5, int par6, int par7, float par8, float par9, float par10)
	{
		if (par3World.isRemote)
		{
			return true;
		}
		if (par1ItemStack.getItemDamage() != 0)
		{
			InventoryChestTransporter invChestTP = new InventoryChestTransporter(par1ItemStack);
			if (invChestTP.placeBlock(par3World, par2EntityPlayer, par4, par5, par6, par7, par8, par9, par10))
			{
				if (!par2EntityPlayer.capabilities.isCreativeMode)
				{
					par1ItemStack.setItemDamage(0);
					par1ItemStack.stackTagCompound = null;
				}
				else
				{
					InventoryPlayer iinv = par2EntityPlayer.inventory;
					iinv.setInventorySlotContents(iinv.currentItem, new ItemStack(MoreInventoryMod.ChestTransporter));
				}

			}
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[49];
		icons[0] = iconRegister.registerIcon("moreinv:ChestTransporter");
		icons[1] = iconRegister.registerIcon("moreinv:ChestTransporter_Chest");
		icons[2] = iconRegister.registerIcon("moreinv:ChestTransporter_TrapChest");
		for (int i = 0; i < StorageBoxType.values().length; i++)
		{
			if (i != StorageBoxType.Glass.ordinal() && i != StorageBoxType.CobbleStone.ordinal()
					&& i != StorageBoxType.Ender.ordinal())
				icons[i + 3] = iconRegister.registerIcon("moreinv:ChestTransporter_"
						+ StorageBoxType.values()[i].name() + "Box");
		}
		for (int i = 0; i < 30; i++)
		{
			icons[i + 19] = iconRegister.registerIcon("moreinv:ChestTransporter_mod" + i);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int par1)
	{
		return icons[par1];
	}
}