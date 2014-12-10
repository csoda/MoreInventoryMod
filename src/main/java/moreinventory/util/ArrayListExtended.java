package moreinventory.util;

import java.util.ArrayList;
import java.util.Collection;

public class ArrayListExtended<E> extends ArrayList<E>
{
	public ArrayListExtended()
	{
		super();
	}

	public ArrayListExtended(Collection<? extends E> c)
	{
		super(c);
	}

	public boolean addIfAbsent(E value)
	{
		return value != null && !contains(value) && add(value);
	}

	public boolean addObject(Object obj)
	{
		return obj != null && add((E)obj);
	}

	public ArrayListExtended<E> addAllObject(Collection c)
	{
		for (Object obj : c.toArray())
		{
			addObject(obj);
		}

		return this;
	}

	public ArrayListExtended<E> addAllObject(Iterable iterable)
	{
		for (Object obj : iterable)
		{
			addObject(obj);
		}

		return this;
	}

	public ArrayListExtended<E> addAllObject(Object... objects)
	{
		for (Object obj : objects)
		{
			addObject(obj);
		}

		return this;
	}

	public E get(int index, E value)
	{
		return index < 0 || index >= size() || get(index) == null ? value : get(index);
	}
}