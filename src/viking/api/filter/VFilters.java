package viking.api.filter;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.NPC;

import viking.framework.VMethodProvider;

/**
 * This class holds various pre-made FILTERS for use in Viking scripts
 * 
 * @author The Viking
 */
public class VFilters extends VMethodProvider
{
	public final Filter<NPC> ABLE_TO_ATTACK_NPC = ableToAttack();
	public final Filter<Entity> ABLE_TO_REACH_ENTITY = ableToReach();
	
	/**
	 * Filter for determining whether or not an NPC is able to be attacked.
	 * Checks if the npc is not under attack, and also not interacting with
	 * any other character
	 * 
	 * @return a Filter which FILTERS only NPCs that are viable for attacking
	 */
	private Filter<NPC> ableToAttack()
	{
		return (NPC f) -> !f.isUnderAttack() && f.getInteracting() == null && f.getHealthPercent() > 0;
	}
	
	private Filter<Entity> ableToReach()
	{
		return (Entity e) -> map.canReach(e);
	}
	
	/**
	 * Filter for determining whether or not an NPC is within
	 * a certain distance, inclusive
	 * 
	 * @param distance the maximum distance the npc can be away, inclusive
	 * @return a filter which FILTERS only NPCs that are within a certain distance
	 */
	public Filter<Entity> distanceFilter(int distance)
	{
		return (Entity e) -> myPosition().distance(e) <= distance;
	}
	
	/**
	 * This method combines two VFilter objects into one,
	 * with an "AND" relationship
	 * 
	 * @param o the other VFilter object to combine with this one
	 * @return the new, combined VFilter object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Filter and(Filter<T>... filters)
	{	
		return new Filter<T>()
		{
			@Override
			public boolean match(T t)
			{
				for(Filter<T> filter : filters)
					if(!filter.match(t))
						return false;
				
				return true;
			}
		};
	}
	
	/**
	 * This method combines to VFilter objects into one,
	 * with an "OR" relationship
	 * 
	 * @param o the other VFilter object to combine with this one
	 * @return the new, combined VFilter object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Filter or(Filter<T>... filters)
	{	
		return new Filter<T>()
		{
			@Override
			public boolean match(T t)
			{
				for(Filter<T> filter : filters)
					if(filter.match(t))
						return true;
				
				return false;
			}
		};
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Filter not(Filter<T>... filters)
	{	
		return new Filter<T>()
		{
			@Override
			public boolean match(T t)
			{
				return !(or(filters).match(t));
			}
		};
	}
}
