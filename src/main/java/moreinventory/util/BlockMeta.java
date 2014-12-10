package moreinventory.util;

import com.google.common.base.Objects;

import net.minecraft.block.Block;

public class BlockMeta
{
	public final Block block;
	public final int meta;

	public BlockMeta(Block block, int metadata)
	{
		this.block = block;
		this.meta = metadata;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(block, meta);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof BlockMeta)
		{
			BlockMeta blockMeta = (BlockMeta)obj;

			return block == blockMeta.block && meta == blockMeta.meta;
		}

		return false;
	}
}