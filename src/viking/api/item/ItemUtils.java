package viking.api.item;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Tab;

import viking.api.Timing;
import viking.framework.VMethodProvider;

public class ItemUtils extends VMethodProvider
{
	public boolean itemOnItem(String itemOne, String itemTwo)
	{
		Item one = inventory.getItem(itemOne), two = inventory.getItem(itemTwo);
		String selectedItem = inventory.getSelectedItemName();
		
		if(selectedItem == null || (!selectedItem.equals(itemOne) && inventory.deselectItem()))
		{
			if(one.interact("Use") && two.interact())
				return true;
		}
		
		return false;
	}
	
	public boolean wield(String name)
	{
		if(!tabs.open(Tab.INVENTORY))
			return false;
		
		if(equipment.contains(name))
			return true;
		
		Item i = inventory.getItem(name);
		if(i != null)
			return i.interact("Wield", "Wear") && Timing.waitCondition(() -> equipment.contains(name), 700);
		
		return false;
	}
	
	public boolean wield(int id)
	{
		if(!tabs.open(Tab.INVENTORY))
			return false;
		
		if(equipment.contains(id))
			return true;
		
		Item i = inventory.getItem(id);
		if(i != null)
			return i.interact("Wield", "Wear") && Timing.waitCondition(() -> equipment.contains(id), 700);
		
		return false;
	}
}
