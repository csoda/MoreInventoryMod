package moreinventory.plugin.appeng;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import moreinventory.util.MIMItemBoxList;
import moreinventory.util.MIMUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;

import java.util.Arrays;

/**
 * Created by Furia on 15/01/30.
 */
public class MEInventoryStorageBoxNetwork implements IMEInventory<IAEItemStack> {
    protected final TileEntitySBAddonBase storage;

    public MEInventoryStorageBoxNetwork(TileEntitySBAddonBase tile)
    {
        this.storage = tile;
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack input, Actionable type, BaseActionSource src)
    {
        //挿入後の余剰を返す

        if (input == null)
            return null;
        if (input.getStackSize() == 0L)
            return null;

        TileEntityStorageBox storage = getStorageBox(input.getItemStack());
        if(storage == null) return input;

        ItemStack template = storage.getContents();
        if(template == null)
            return input;

        if(!input.isSameType(template))
            return input;

        int storedCount = storage.getContentItemCount();

        int maxStacksCount = storage.getUsableInventorySize();
        int maxOneStackSize = template.getMaxStackSize();

        int maxStorableCount;
        if(storage.getTypeName().equals("Ender")){
            maxStorableCount = Integer.MAX_VALUE;
        }else{
            maxStorableCount = maxStacksCount * maxOneStackSize;
        }

        int remainingFreeCount = maxStorableCount - storedCount;

        if(remainingFreeCount <= 0)
            return input;

        IAEItemStack result = input.copy();

        result.setStackSize(Math.max(0,input.getStackSize() - remainingFreeCount));

        boolean isModulate = type == Actionable.MODULATE;
        if(isModulate){
            long remainingInjectCount = input.getStackSize() - result.getStackSize();
            do{
                ItemStack tmp = input.getItemStack().copy();

                int workCount = (int)Math.min(tmp.getMaxStackSize(),remainingInjectCount);
                tmp.stackSize = workCount;

                storage.tryPutIn(tmp);

                if(0 < tmp.stackSize){
                    result.setStackSize(Math.min(input.getStackSize() , result.getStackSize() + tmp.stackSize));
                    break;
                }else{
                    remainingInjectCount -= workCount;
                }
            }while(0 < remainingInjectCount);
        }

        if(result.getStackSize() == 0)
            result = null;

        this.storage.markDirty();
        this.storage.getWorldObj().markBlockForUpdate(this.storage.xCoord, this.storage.yCoord, this.storage.zCoord);
        this.storage.getWorldObj().notifyBlockChange(this.storage.xCoord, this.storage.yCoord, this.storage.zCoord, this.storage.getBlockType());
        return result;
    }

    @Override
    public IAEItemStack extractItems(IAEItemStack request, Actionable type, BaseActionSource src)
    {
        //排出可能分を返す

        if(request == null) return null;

        ItemStack requestStack = request.getItemStack();

        TileEntityStorageBox storage = getStorageBox(requestStack);
        if(storage == null) return null;

        int firstSlotIdx = storage.getFirstItemIndex();
        if(firstSlotIdx < 0)
            return null;

        ItemStack template = storage.getContents();
        if(template == null)
            return null;
        if(!(MIMUtils.compareStacksWithDamage(requestStack,template) && ItemStack.areItemStackTagsEqual(requestStack,template)))
            return null;


        int storedCount = storage.getContentItemCount();
        if(storedCount == 0)
            return null;

        long extractCount = Math.min(storedCount, requestStack.stackSize);

        IAEItemStack result = AEApi.instance().storage().createItemStack(storage.getStackInSlot(storage.getFirstItemIndex()));
        result.setStackSize(extractCount);

        boolean isModulate = type == Actionable.MODULATE;
        if(isModulate){
            long remainingExtractCount = extractCount;
            do{
                ItemStack extract = storage.loadItemStack((int)Math.min(template.getMaxStackSize(),remainingExtractCount));
                remainingExtractCount -= extract.stackSize;
            }while(0 < remainingExtractCount);
        }

        this.storage.markDirty();
        this.storage.getWorldObj().markBlockForUpdate(this.storage.xCoord, this.storage.yCoord, this.storage.zCoord);
        this.storage.getWorldObj().notifyBlockChange(this.storage.xCoord,this.storage.yCoord,this.storage.zCoord,this.storage.getBlockType());

        return result;
    }

    private TileEntityStorageBox getStorageBox(ItemStack itemstack)
    {

        MIMItemBoxList list = storage.getStorageBoxNetworkManager().getBoxList();

        if(list == null) return null;

        for (int i = 0; i < list.getListSize(); i++)
        {
            if(MIMUtils.compareStacksWithDamage(itemstack, list.getItem(i)) && ItemStack.areItemStackTagsEqual(itemstack, list.getItem(i))){
                TileEntity tile = list.getTileBeyondDim(i);
                if(tile == null) continue;
                if(!(tile instanceof TileEntityStorageBox)) continue;

                return (TileEntityStorageBox)tile;
            }
        }

        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out)
    {

        MIMItemBoxList list = storage.getStorageBoxNetworkManager().getBoxList();

        if(list == null) return out;

        for (int i = list.getListSize(); 0 < i--;)
        {
            ItemStack stack = list.getItem(i);

            if(stack == null) continue;


            int[] pullPos = list.getBoxPos(i);
            TileEntity tile = storage.getWorldObj().getTileEntity(pullPos[0], pullPos[1], pullPos[2]);

            if(tile == null) continue;
            if(!(tile instanceof TileEntityStorageBox)) continue;

            TileEntityStorageBox currentBox = (TileEntityStorageBox)tile;

            int allCount = currentBox.getContentItemCount();

            if(allCount <= 0) continue;

            IAEItemStack stored = AEApi.instance().storage().createItemStack(currentBox.getStackInSlot(currentBox.getFirstItemIndex()));
            stored.setStackSize(allCount);
            out.add(stored);
        }

        return out;
    }

    @Override
    public StorageChannel getChannel()
    {
        return StorageChannel.ITEMS;
    }
}
