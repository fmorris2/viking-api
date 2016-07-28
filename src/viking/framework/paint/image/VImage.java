package viking.framework.paint.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * This class represents our VImage class which is essentially
 * a wrapper around a drawable image. The idea behind this class
 * is to provide a simple way to load and draw images from the web.
 * Each VImage is loaded on a separate Thread, to avoid any unresponsive
 * web requests from blocking the main script logic
 * 
 * @author The Viking
 *
 */
public class VImage
{
	private Image image;
	
	public VImage(String url)
	{
		loadImage(url);
	}
	
	private void loadImage(String url)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					image = ImageIO.read(new URL(url));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void draw(Graphics2D g, int x, int y)
	{
		if(image != null)
			g.drawImage(image, x, y, null);
	}
}