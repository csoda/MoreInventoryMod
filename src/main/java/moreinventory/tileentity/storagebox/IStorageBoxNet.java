package moreinventory.tileentity.storagebox;

public interface IStorageBoxNet
{
	public StorageBoxNetworkManager getStorageBoxNetworkManager();

	public void setStorageBoxNetworkManager(StorageBoxNetworkManager SBNetManager);

	public String getOwnerName();

	public boolean isPrivate();
}