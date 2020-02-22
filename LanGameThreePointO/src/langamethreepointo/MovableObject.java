package langamethreepointo;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

public class MovableObject
{
	// unique id #
	private int id;
	private String owner;  // who owns this object SHIP1, SHIP2, 
	
	// does this object exist
	// 0=no or 1=yes
	private int alive;
	private String whatAmI;
	
	// direction to move
	// 0 to 360, 0 is North 
	private int direction;
	
	private int speed;
	
	// current location of object
	private int x;
	private int y;
	private int width;
	private int height;

	// amount of time on the screen
	private float time;
	
	// speed of object
 
	private int speedX;
	private int speedY;
	
	// list of possible images to use
	private ArrayList <Image> imageList;
	
	// current image being displayed
	private Image currentImage;

	// filename of current image being displayed
	private String currentFilename;
	
	// list of filenames for the images (jpg, ...)
	private String [] imageFilenames;
		
	// the JPanel that this object is being displayed on
	private JPanel mainPanel;


    public MovableObject(String owner, int id, String whatAmI, JPanel mainPanel, String filename, int x, int y, int width, int height)
    {
        
               this.owner=owner;
                this.id = id;
                alive = 1;
		direction = 0; // Location.NORTH;
		speed = 3;
		this.mainPanel = null; // make sure you call setPanel
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		time = 0;
		speedX = 3;
		speedY = 3;
		imageList = null;
		currentImage = null;
		imageFilenames = null;
		currentFilename = "";
		setPanel(mainPanel);
		setCurrentFilename(filename);
 	}
 	

	public void setPanel(JPanel mainPanel)
	{
		this.mainPanel = mainPanel;
	}

	public JPanel getPanel()
	{
		return mainPanel;
	}

	public void setImageFilenames(String [] filenames)
	{
		imageFilenames = filenames;
		imageList = new ArrayList<Image>();
		for (int i=0; i < filenames.length; i++)
		{
			try
			{
				Image pic = Toolkit.getDefaultToolkit().getImage(LanGameThreePointO.class.getResource(filenames[i]));
				imageList.add(pic);
			}
			catch (Exception e)
			{
			}
		}
		if (filenames.length > 0)
		{
			currentFilename = filenames[0];
			setImage();
		}
	}

	private void setImage()
	{
		if (currentFilename != null && currentFilename != "")
		{
			try
			{
				currentImage = Toolkit.getDefaultToolkit().getImage(LanGameThreePointO.class.getResource(currentFilename));
				//System.out.println("toolkit ok");
			}
			catch (Exception e)
			{
				currentImage = null;
				currentFilename = "";
				System.out.println("error getImage with toolkit");
			}
		}
	}

	public void setCurrentFilename(String filename)
	{
		currentFilename = filename;
		setImage();
	}

	public void setCurrentFilenamePosition(int position)
	{
		if (imageFilenames == null)
			return;
		if (position < imageFilenames.length && position > -1)
			{
				currentFilename = imageFilenames[position];
				setImage();
			}
		else
			{
				currentFilename = "";
				currentImage = null;
			}
	}

 

    public int getDirection()
    {
        return direction;
    }

    public void setDirection(int direction)
    {
        this.direction = direction;
        this.direction = this.direction % 360;
    }

     public int getAlive()
    {
        return alive;
    }

    public void setAlive(int alive)
    {
        this.alive = alive;
    }

    public int getId()
    {
        return id;
    }

    public String getOwner()
    {
        return owner;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setXY(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getSpeedX()
    {
        return speedX;
    }

    public void setSpeedX(int speedX)
    {
        this.speedX = speedX;
    }

    public int getSpeedY()
    {
        return speedY;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeedXY(int speedX, int speedY)
    {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public void setSpeedY(int speedY)
    {
        this.speedY = speedY;
    }

    public float getTime()
    {
        return time;
    }

    public void setTime(float time)
    {
        this.time = time;
    }


	public void moveUp()
	{
		setY(getY() - speedY);
	}

	public void moveDown()
	{
		setY(getY() + speedY);
	}

	public void moveLeft()
	{
		setX(getX() - speedX);
	}

	public void moveRight()
	{
		setX(getX() + speedX);
	}

	public void moveTo(int x, int y)
	{
		setX(x);
		setY(y);
	}

	public Rectangle getRect()
	{
		return new Rectangle(getX(),getY(),getWidth(),getHeight());
	}

	public boolean intersects(Rectangle rect)
	{
		if (getRect().intersects(rect))
			return true;
		return false;
	}
	
	public boolean intersects(MovableObject other)
	{
		if (getRect().intersects(other.getRect()))
			return true;
		return false;
	}

	public Point getPoint()
	{
		return new Point(getX(),getY());
	}

	public void turnRight()
	{
		setDirection(getDirection()+90);
	}
	
	public boolean containsPoint(Point point)
	{
		if (getRect().contains(point))
			return true;
		return false;
	}

	// moves toward the other object using the speed
	// uses similar triangles
	public void moveTowards(MovableObject other)
	{
		double sideX = Math.abs(other.getX() - getX());
		double sideY = Math.abs(other.getY() - getY());
		double d = Math.sqrt(sideX*sideX + sideY*sideY);
		
		int speedX = (int)Math.round((getSpeed()*sideX)/d);
		int speedY = (int)Math.round((getSpeed()*sideY)/d);
		
		if (getX() < other.getX())
			setX(getX() + speedX);
		else if (getX() > other.getX())
			setX(getX() - speedX);
		
		if (getY() < other.getY())
			setY(getY() + speedY);
		else if (getY() > other.getY())
			setY(getY() - speedY);			
	
	}

	public void draw2(Graphics g)
	{
            Graphics2D g2 = (Graphics2D)g;
            AffineTransform transform = new AffineTransform();
            transform.setToTranslation(getX(), getY());
            //transform.rotate(degreesToRadians(getDirection()),24,24);
            transform.rotate(degreesToRadians(getDirection()),getWidth()/2,getHeight()/2); //this does not work

            g2.drawImage(currentImage, transform, mainPanel);        
	}
    
	public double degreesToRadians(double degrees)
	{
     	return degrees*Math.PI/180;
	}
	
	public void draw(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.black);

		g2.drawImage(currentImage,
			x, 
			y, 
			getWidth(),
			getHeight(),
			mainPanel);
	}

	public String toString()
	{
		return "" + getX() + " " + getY() + " " + getWidth() + " " + getHeight();	
	}
	
	
}  // end of class MovableObject

/*
boolean drawImage(Image img, int x, int y, ImageObserver observer)
  boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
  boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer)
  boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer)
*/

