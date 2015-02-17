package power;

import java.util.HashSet;

public class HashSetTrigger<E> extends HashSet<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3672522447871226492L;
	
	private E itemToCheck;
	
	HashSetTrigger(E trigger)
	{
		super ();
		itemToCheck = trigger;
		
	}
	
	public boolean addElement (E itemToAdd) throws TargetFoundException
	{
		if (itemToAdd.equals(itemToCheck))
		{
			throw new TargetFoundException();
		}
		return super.add(itemToAdd);
	}

}
