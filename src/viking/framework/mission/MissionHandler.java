package viking.framework.mission;

import java.util.LinkedList;
import java.util.Queue;

import viking.framework.script.CapitalScript;
import viking.framework.script.VikingScript;

/**
 * This class will handle the execution / flow of missions for any
 * Viking script.
 * 
 * @author The Viking
 */
public class MissionHandler
{
	private VikingScript script;
	private Queue<Mission> missions;
	
	/**
	 * Primary constructor for MissionHandler. Takes in a queue of missions
	 * to initially add to the mission queue
	 * 
	 * @param script the VikingScript relating to this mission handler
	 * @param missions the initial queue of missions to add
	 */
	public MissionHandler(VikingScript script, Queue<Mission> missions)
	{
		this.script = script;
		this.missions = missions;
	}
	
	/**
	 * Handles the main mission execution / driving
	 * 
	 * @return the sleep time for this loop cycle
	 */
	public int execute()
	{
		//if mission queue is null or empty, end the script
		if(missions == null || missions.isEmpty())
		{
			script.log(this, false, "No more missions... Ending");
			script.stop();
			return 10;
		}
		
		Mission current = missions.peek();
		
		script.assertion(current != null, "Current mission is null"); //We should never be supplying null missions to our script, so crash if we did
		
		//check if the mission onStart needs to be called
		if(!current.hasStarted())
		{
			current.onMissionStart();
			current.setStarted(true);
		}
		
		if(current instanceof CapitalMission && ((CapitalMission)current).needsCapital()
				&& script instanceof CapitalScript)
		{
			script.log(this, false, "Mission needs capital!");
			gainCapital();
			return 600;
		}
		
		//if the current mission is over, move on to the next one
		if(current.canEnd())
		{
			script.log(this, false, current.getMissionName() + " can end... moving on to next mission");
			script.log(this, false, current.getEndMessage());
			missions.poll();
			return 10;
		}
		else //execute the current mission
			return current.execute();
	}
	
	private void gainCapital()
	{
		//first, we generate the appropriate money making mission
		Mission m = ((CapitalScript)script).generateMoneyMakingMission();
		
		script.log(this, false, "Added money making mission to gain capital: " + m.getMissionName());
		
		//now, we gracefull set the current mission to the money making mission
		((LinkedList<Mission>)(missions)).addFirst(m);
	}

	//Getters & Setters
	public Queue<Mission> getMissions()
	{
		return missions;
	}
	
	public Mission getCurrent()
	{
		return missions.peek();
	}
}
