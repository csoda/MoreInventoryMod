package moreinventory.util;

import com.google.common.base.Predicate;

public class BlockMetaFilter implements Predicate<BlockMeta>
{
	private final String filter;

	public BlockMetaFilter(String filter)
	{
		this.filter = filter;
	}

	@Override
	public boolean apply(BlockMeta entry)
	{
		return MIMUtils.blockMetaFilter(entry, filter);
	}
}