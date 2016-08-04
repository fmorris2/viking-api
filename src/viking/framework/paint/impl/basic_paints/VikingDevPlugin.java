package viking.framework.paint.impl.basic_paints;

import java.awt.Color;
import java.lang.management.ManagementFactory;

import viking.api.Timing;
import viking.framework.averager.Averager;
import viking.framework.paint.VikingPaint;
import viking.framework.paint.impl.BasicVikingPlugin;
import viking.framework.script.VikingScript;

import com.sun.management.OperatingSystemMXBean;

public class VikingDevPlugin extends BasicVikingPlugin
{
	private OperatingSystemMXBean bean;
	private Averager avg;
	
	public VikingDevPlugin(VikingScript script, VikingPaint<?> p, Color color, float alpha, int x, int bottomY)
	{
		super(script, p, color, alpha, x, bottomY);
		bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		avg = script.getUtils().getAverager();
	}

	@Override
	protected String[] getInfo()
	{
		avg.add("cpu", bean.getProcessCpuLoad() * 100);
		
		return new String[]
		{
			"Time ran: " + Timing.msToString(paint.getTimeRan()),
			"Moving CPU usage: " + avg.getMoving("cpu") + "%",
			"Available cores: " + bean.getAvailableProcessors(),
			"RAM usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000 
				+ "/" + bean.getCommittedVirtualMemorySize() / 1000000 + "Mb",
			"Physical memory usage: " + bean.getFreePhysicalMemorySize() / 1000000
				+ "/" + bean.getTotalPhysicalMemorySize() / 1000000 + "Mb",
		};
	}

	@Override
	public void reset()
	{
		avg.reset();
	}
}
