package viking.framework.worker;

import viking.framework.VMethodProvider;
import viking.framework.mission.Mission;

/**
 * This class represents a specific "task" to execute in a certain script.
 * For example, it could represent "chop" in a WC script, or "fish" in a Fishing
 * script.
 * 
 * @author The Viking
 *
 * @param <T> The mission we're working for
 */
public abstract class Worker<T extends Mission> extends VMethodProvider
{
	protected T mission;
	
	/**
	 * Primary constructor for the Worker class. We supply
	 * it with a mission, so that it can access any data it needs
	 * 
	 * @param mission the mission that is driving this worker
	 */
	public Worker(T mission)
	{
		this.mission = mission;
		exchangeContext(mission.getScript());
	}
	
	/**
	 * This method tells us whether or not we need to repeat execution of this worker
	 * 
	 * @return true if we need to repeat execution of this worker, false if we should go back to the root
	 */
	public abstract boolean needsRepeat();
	
	/**
	 * This method handles the execution of this worker
	 */
	public abstract void work();
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
