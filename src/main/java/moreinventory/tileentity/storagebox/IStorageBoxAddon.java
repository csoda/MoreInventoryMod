package moreinventory.tileentity.storagebox;

import net.minecraft.world.World;

public interface IStorageBoxAddon extends IStorageBoxNet
{
	public void onSBNetInvChanged(World world, int x, int y, int z, int id, int damage);

	public void onTripleClicked(World world, int x, int y, int z, int id, int damage);
}