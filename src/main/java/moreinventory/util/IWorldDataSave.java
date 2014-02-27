/**
 * 
 */
package moreinventory.util;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author c_soda
 *	This is Interface for CSworldSaveHelper.
 */
public interface IWorldDataSave {

	public void writeToNBT(NBTTagCompound nbt);
	
	public void readFromNBT(NBTTagCompound nbt);
	
}
