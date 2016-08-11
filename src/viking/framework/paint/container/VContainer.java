package viking.framework.paint.container;

import java.awt.Graphics2D;
import java.util.List;

import viking.framework.paint.VikingPaint;
import viking.framework.paint.container.component.VComponent;
import viking.framework.paint.image.VImage;
import viking.framework.paint.plugin.VikingPaintPlugin;
import viking.framework.script.VikingScript;

public abstract class VContainer extends VikingPaintPlugin
{
	private VImage image;
	private int x;
	private int y;
	private int width = -1;
	private int height = -1;
	private List<VComponent> components;
	
	public VContainer(VikingScript script, VikingPaint<?> paint, String imageUrl, int x, int y)
	{
		super(script, paint);
		this.x = x;
		this.y = y;
		image = new VImage(imageUrl);
		components = initComponents();
	}
	
	protected abstract List<VComponent> initComponents();

	@Override
	public void draw(Graphics2D g)
	{
		if(image.getImage() != null)
		{		
			image.draw(g, x, y);
			
			if(width == -1 && image.getBounds() != null)
			{
				width = image.getBounds().width;
				height = image.getBounds().height;
			}
			
			for(VComponent c : components)
				c.handle(g);
		}
	}

	@Override
	public void reset()
	{
		for(VComponent c : components)
			c.reset();
	}

}
