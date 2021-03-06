package viking.api.travel;

import java.util.List;
import java.util.concurrent.Callable;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.input.mouse.MiniMapTileDestination;

import viking.api.Timing;
import viking.api.condition.VCondition;
import viking.framework.VMethodProvider;

/**
 * This class will hold various utility method relating
 * to walking
 *
 * @author The Viking
 */
public class WalkingUtils extends VMethodProvider {
	
	private static final int RUN_TOGGLE = random(60, 90);
	
	private int failures;
	
    /**
     * The general walking method in the WalkingUtils class.
     * This method will intelligently determine which walking system
     * provided by the API to use. Will use WebWalking if the distance
     * is far, or the default Walking class if the distance is shorter
     *
     * @param pos            the position to WALK to
     * @param breakCondition a condition to break out of the walking for
     * @param waitCondition  a condition to wait for after the walking finishes
     * @param cycleTime      the cycle time for the wait condition
     * @param timeout        the timeout for the wait condition
     * @return true if we've successfully walked to the position, false otherwise
     * @throws InterruptedException
     */
    public boolean walkTo(Position pos, VCondition breakCondition, VCondition waitCondition, int cycleTime, int timeout, boolean useDax) {
    	script.log(this, false, "Walk to " + pos);
 
    	boolean daxSuccess = false;
    	
    	if(useDax && pos.getZ() == myPosition().getZ())
    	{
	    	//try dax walker
    		script.log(this, false, "Getting dax path...");
	    	List<Position> dax = daxPath.getPath(pos);
	    	script.log(this, false, "Dax path received");
	    	
	    	if(dax.size() > 0)
	    	{
	    		script.log(this, false, "Walking dax path to " + pos.toString());
	    		if(walkPath(dax))
	    			daxSuccess = true;
	    		else
	    			failures++;
	    	}
    	}
    	
    	if(failures > 2 || !daxSuccess || !map.canReach(pos))
    	{
    		script.log(this, false, "Using OSBot web to walk to " + pos.toString());
	    	widgets.closeOpenInterface();
	    	
	    	WebWalkEvent walkEvent = new WebWalkEvent(pos);
	        walkEvent.setBreakCondition(breakCondition == null ? conditions.NOT_LOGGED_IN : breakCondition.or(conditions.NOT_LOGGED_IN));
	        
	        Event event = execute(walkEvent);
	        
	        if(!event.hasFailed())
	        	failures = 0;
	
	        //execute the WALK event
	        return event.hasFinished()
	                && waitCondition == null ? true : Timing.waitCondition(waitCondition, cycleTime, timeout);
    	}
    	
    	failures = 0;
    	return true;
    }
    
    public boolean walkTo(Position pos, VCondition breakCondition, VCondition waitCondition, int cycleTime, int timeout)
    {
    	return walkTo(pos, breakCondition, waitCondition, cycleTime, timeout, true);	
   	}

    /**
     * This method is the most basic WALK method in this class. It calls upon
     * the general WALK method, but doesn't make use of a break condition or wait
     * condition.
     *
     * @param pos The position to WALK to
     * @return true if we've successfully walked to the position, false otherwise
     * @throws InterruptedException
     */
    public boolean walkTo(Position pos) {
        return walkTo(pos, null, null, -1, -1, true);
    }
    
    public boolean walkTo(Position pos, boolean useDax) {
        return walkTo(pos, null, null, -1, -1, useDax);
    }

    /**
     * This method calls upon the general walkTo method, but walks to a random position
     * in the area provided and also breaks out of the walking method as soon as the player
     * enters the area.
     *
     * @param a The area to WALK to
     * @return true if the player successfully walked to the area, false otherwise
     */
    public boolean walkToArea(Area a) {
        final VCondition IN_AREA = conditions.inAreaCondition(a);
        return walkTo(a.getRandomPosition(), IN_AREA, IN_AREA, 600, 3500, true);
    }
    
    public boolean walkToArea(Area a, boolean useDax) {
        final VCondition IN_AREA = conditions.inAreaCondition(a);
        return walkTo(a.getRandomPosition(), IN_AREA, IN_AREA, 600, 3500, useDax);
    }

    /**
     * This method calls upon the general walkTo method, but walks to a random position
     * in the area provided and also breaks out of the walking method as soon as the player
     * enters the area.
     *
     * @param a         The area to WALK to
     * @param break_condition The stopping condition
     * @param wait_condition The waiting condition
     * @return true if the player successfully walked to the area, false otherwise
     */
    public boolean walkToArea(Area a, Callable<Boolean> break_condition, Callable<Boolean> wait_condition) {
        VCondition b_condition = new VCondition() {
            @Override
            public boolean evaluate() {
            	try {return break_condition.call();}catch(Exception e){e.printStackTrace();}
            	return false;
            }
        };
        VCondition w_condition = new VCondition() {
            @Override
            public boolean evaluate() {
            	try {return wait_condition.call();}catch(Exception e){e.printStackTrace();}
            	return false;
            }
        };
        return walkTo(a.getRandomPosition(), b_condition, w_condition, 600, 3500, true);
    }

    public boolean walkToArea(Area a, Callable<Boolean> break_condition) {
        return walkToArea(a, break_condition, null);
    }
    
    public boolean walkPath(List<Position> positions)
    {
    	for(int i = 0; i < positions.size(); i++)
    	{
    		if(!settings.isRunning() && settings.getRunEnergy() > RUN_TOGGLE && settings.open())
    			settings.setRunning(true);
    		
    		Position targetPos = positions.get(i);
    		MiniMapTileDestination miniMap = null;
    		try
    		{
	    		miniMap = new MiniMapTileDestination(bot, targetPos);
	    		for(int z = i; z < positions.size(); z++)
	    		{
	    			MiniMapTileDestination further = new MiniMapTileDestination(bot, positions.get(z));
	    			if(further.isVisible())
	    			{
	    				miniMap = further;
	    				i = z;
	    			}
	    			else
	    				break;
	    		}
    		}
    		catch(Exception e)
    		{
    			script.log(this, false, "Error getting mini map tile destination");
    		}
    		
    		if(miniMap != null && miniMap.isVisible())
    		{
    			waitMs(random(20, 70));
    			if(mouse.click(miniMap) && Timing.waitCondition(() -> myPlayer().isMoving(), 2500))
    			{
    				if(i == positions.size() - 1)
    					return true;
    				
    				Timing.waitCondition(miniMapIsVisible(positions.get(i + 1)), 7500);
    			}
    			else if(!map.canReach(targetPos)) //player didn't start moving after clicked next tile... check if we can reach
    			{
    				script.log(this, false, "Can't reach target pos!");
    				return false;
    			}
    		}    			
    	}
    	
    	return false;
    }
    
    private VCondition miniMapIsVisible(Position pos)
    {
    	return new VCondition()
		{

			@Override
			public boolean evaluate()
			{
				MiniMapTileDestination map = new MiniMapTileDestination(bot, pos);
				return map != null && map.isVisible();
			}
    		
		};
    }

}