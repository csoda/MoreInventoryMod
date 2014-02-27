package moreinventory;

import net.minecraft.world.World;

public class CommonProxy {
    // Client stuff
    public void registerRenderers() {
    }
    
	public World getClientWorld()
	{
		return null;
	}
	
	public boolean isClient(){
		return false;
	}
}
