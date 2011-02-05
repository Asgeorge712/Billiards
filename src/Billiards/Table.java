package Billiards;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * class to represent the palying table
 *
 */
public class Table extends JPanel implements MouseListener,
                                             MouseMotionListener,
                                             Runnable {

	//This is Professional Pool table dimensions 4.5' x 9' = 54" x 108" * 8pixels/inch = 432x864 pixels.
	public static final int SIZEY = 432;
	public static final int SIZEX = 864;


    Vector<Circle> circles; // collection of the circles
    Vector<Circle> fallen; // collection of the circles
    
	int queBallIndex = -1;

    Bouncer bouncer;   		// to reference shuffle window
    Engine  engine;    		// the engine that drives the game
    
    public boolean ready;   // must be true to go again
    public boolean scratched = false;
    public boolean movingQ = true;

    Vector<Pocket> pockets;	//Collection of pockets on the table;
   	private Thread t;

	public static final double CoF = 120;


    int targetCircle = -1;
    boolean aimingQueueBall = false;
    HashMap<String, Integer> queueLine;

    /*******************************************************
     * constructor. sets up the panel stuff, adds
     * mouse listenners, starts game engine, etc
     *
     * @param shuffle
     ******************************************************/
    public Table(Bouncer bouncer) {
        //Always call super
        super();

        //Makes the moving display smoother
        setDoubleBuffered(true);

        //Default bg colour
        setBackground(new Color (51, 102, 51));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        //Add mouse listeners
        addMouseListener(this);
        addMouseMotionListener(this);

        //Reference to bouncer
        this.bouncer = bouncer;

		//Initialize the circles.
        circles = new Vector<Circle>();
        
        // Initialize engine
        engine = new Engine( this );

        populateCircles();
        engine.setCircles( circles );

        populatePockets();

        //Start the Engine
        Thread e = new Thread( engine );
		e.setPriority(Thread.NORM_PRIORITY);
        e.start();

        // start thread
        Thread t = new Thread(this);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();

        // ready!
        ready = true;
    }


	/*************************************************
	*
	*
	*
	**************************************************/
    public void newGame() {
		//init circles.
        populateCircles();
        engine.setCircles( circles );
        movingQ = true;

    }


	/*************************************************
	*
	*
	*
	**************************************************/
    public void readdQueueBall() {
		boolean foundQueueBall = false;
		for ( Circle c : circles ) {
			if ( c.name.equals("Queue Ball") ) foundQueueBall = true;
		}

		if ( !foundQueueBall ) {
			double cx =  800+engine.TABLE_OFFSET_X;
			double cy = SIZEY/2+engine.TABLE_OFFSET_Y;

			System.out.println("Adding que ball at (x,y): " + cx + "," + cy);

			Circle c = new Circle(0, "Que Ball", Color.WHITE, cx, cy, 0, 0, 30);
			circles.add( c );
			queBallIndex = findQueBallIndex( circles );
		}
		else {
			System.out.println("Queue Ball is already on the table!!");
		}
	}


	/*************************************************
	*
	*
	*
	**************************************************/
	public int findQueBallIndex( Vector<Circle> circles ) {
		Circle c;
		int index = -1;
		for ( int i = 0 ; i < circles.size(); i++ ) {
			c = (Circle)circles.elementAt(i);
			if ( c.name.equals("Que Ball") ) index = i;
		}
		if ( index == -1 ) {
			System.out.println("There's NO QUE BALL!!!!");
			System.exit(0);
		}
		return index;
	}


	/*************************************************
	*
	*
	*
	**************************************************/
	public void populatePockets() {
		pockets = new Vector<Pocket>();
		Pocket p;

		p = new Pocket("Top Left",     Pocket.STANDARD_COLOR, 86, 86, 48);
		pockets.add( p );

		p = new Pocket("Top Mid",      Pocket.STANDARD_COLOR, 508, 76, 48);
		pockets.add( p );

		p = new Pocket("Top Right",    Pocket.STANDARD_COLOR, 930, 86, 48);
		pockets.add( p );

		p = new Pocket("Bottom Left",  Pocket.STANDARD_COLOR, 86, 496, 48);
		pockets.add( p );

		p = new Pocket("Bottom Mid",   Pocket.STANDARD_COLOR, 508, 506, 48);
		pockets.add( p );

		p = new Pocket("Bottom Right", Pocket.STANDARD_COLOR, 930, 496, 48);
		pockets.add( p );

	}


	/*************************************************
	*
	*
	*
	**************************************************/
	public void populateCircles() {
		circles = new Vector<Circle>();
    	fallen = new Vector<Circle>();

		Circle c;

		double midy = SIZEY/2 + Engine.TABLE_OFFSET_Y-15;
		double firstRow = 150 + Engine.TABLE_OFFSET_X;

		// name, Color color, double x, double y, int speed, double direction, int size) {

		c = new Circle(0, "Que Ball", Color.WHITE, (SIZEX*.75)+Engine.TABLE_OFFSET_X, midy, 0, 0, 30);
		circles.add( c );

		//First Row (going down)
		c = new Circle(1, "One Ball", new Color(255, 255, 102),firstRow, midy-62, 0, 0, 30);
		circles.add( c );
		c = new Circle(2, "Two Ball", new Color( 51, 51, 255), firstRow, midy-31, 0, 0, 30);
		circles.add( c );
		c = new Circle(3, "Three Ball", new Color(204, 0, 51), firstRow, midy, 0, 0, 30);
		circles.add( c );
		c = new Circle(4, "Four Ball", new Color(255, 0, 153), firstRow, midy+31, 0, 0, 30);
		circles.add( c );
		c = new Circle(5, "Five Ball", new Color(255, 102, 0), firstRow, midy+62, 0, 0, 30);
		circles.add( c );

		//Second Row
		c = new Circle(6, "Six Ball", new Color(51, 255, 0),  firstRow+31, midy-47, 0, 0, 30);
		circles.add( c );
		c = new Circle(7, "Seven Ball", new Color(102, 0, 0), firstRow+31, midy-16, 0, 0, 30);
		circles.add( c );
		c = new Circle(8, "Eight Ball", Color.BLACK,          firstRow+31, midy+16, 0, 0, 30);
		circles.add( c );
		c = new Circle(9, "Nine Ball", new Color(204, 204, 0),firstRow+31, midy+47, 0, 0, 30);
		circles.add( c );


		//Third Row
		c = new Circle(10, "Ten Ball", new Color(153, 0, 255), firstRow+62, midy-31, 0, 0, 30);
		circles.add( c );
		c = new Circle(11, "Eleven Ball", new Color(153, 0, 0), firstRow+62, midy, 0, 0, 30);
		circles.add( c );
		c = new Circle(12, "Twelve Ball", new Color(255, 0, 153), firstRow+62, midy+31, 0, 0, 30);
		circles.add( c );
		//Forth Row
		c = new Circle(13, "Thirteen Ball", new Color(153, 255, 153), firstRow+93, midy-16, 0, 0, 30);
		circles.add( c );
		c = new Circle(14, "Fourteen Ball", new Color(0, 0, 153), firstRow+93, midy+16, 0, 0, 30);
		circles.add( c );

		//Fifth Row
		c = new Circle(15, "Fifteen Ball", new Color(0, 0, 255), firstRow+124, midy, 0, 0, 30);
		circles.add( c );


		queBallIndex = findQueBallIndex( circles );

		for ( int x = 0 ; x < circles.size(); x++ ) {
			engine.calcFriction( c);
		}

	}


    /*******************************************************
     * private method to paint all circles
     *
     * this will throw an exception if a disk is removed by the
     * other thread durring the exact time this is looping
     *
     * @param g paint
     ******************************************************/
    private void paintCircles(Graphics g) {
        try {
            for (Circle c : circles)
                paintCircle(g, c);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            paintCircles(g); // retry so the disks never not get painted
        }
    }


    /******************************************************
     * private method to paint a circle on the screen
     *
     *
     * @param g paint
     * @param d circle
     ******************************************************/
    private void paintCircle( Graphics g, Circle c ) {
        if (c == null) return;
        int fontSize = 10;
        int dx = (int)c.x+8;
        int dy = (int)c.y+8;
        
        if ( c.ballNumber > 0 && c.ballNumber < 9 ) {
            g.setColor( c.color );
            g.fillOval((int)c.x - 1, (int)c.y - 1, 30, 30 );

            g.setColor( Color.WHITE );
            g.fillOval(dx, dy, 12, 12 );

            char[] num = { Character.forDigit(c.ballNumber, 10) }; 
            Font font = new Font("Courier New", Font.PLAIN, fontSize);
            g.setFont(font);
            g.setColor( Color.BLACK );
            g.drawChars(num, 0, num.length, (int)StrictMath.round(c.x+12), (int)StrictMath.round(c.y+17) );
        }
        else if ( c.ballNumber > 9 ) {
            g.setColor( Color.WHITE );
            g.fillOval((int)c.x - 1, (int)c.y - 1, 30, 30 );
            
            g.setColor( c.color );
            int x = (int)c.x + 11;
            int y = (int)c.y;
            
            g.drawLine(x+11, y+1, x+11, y+27);
            g.drawLine(x+10, y+1, x+10, y+27);
            g.drawLine(x+9,  y,   x+9, y+28);
            g.drawLine(x+8,  y,   x+8, y+28);
            
            g.fillRect(x, y, 8, 29);
            
            g.drawLine(x-1, y,   x-1, y+28);
            g.drawLine(x-2, y,   x-2, y+28);
            g.drawLine(x-3, y+1, x-3, y+27);
            g.drawLine(x-4, y+1, x-4, y+27);

            g.setColor( Color.WHITE );
            g.fillOval(dx, dy, 12, 12 );

            int bi = c.ballNumber-10;
            char[] num = { '1', Character.forDigit(bi, 10) }; 
            Font font = new Font("Courier New", Font.PLAIN, fontSize);
            g.setFont(font);
            g.setColor( Color.BLACK );
            g.drawChars(num, 0, num.length, (int)StrictMath.round(c.x+8), (int)StrictMath.round(c.y+17) );
        }
        else if ( c.ballNumber == 9 ) {
            g.setColor( Color.WHITE );
            g.fillOval((int)c.x - 1, (int)c.y - 1, 30, 30 );
            
            g.setColor( c.color );
            int x = (int)c.x + 11;
            int y = (int)c.y;
            
            g.drawLine(x+11, y+1, x+11, y+27);
            g.drawLine(x+10, y+1, x+10, y+27);
            g.drawLine(x+9,  y,   x+9, y+28);
            g.drawLine(x+8,  y,   x+8, y+28);
            
            g.fillRect(x, y, 8, 29);
            
            g.drawLine(x-1, y,   x-1, y+28);
            g.drawLine(x-2, y,   x-2, y+28);
            g.drawLine(x-3, y+1, x-3, y+27);
            g.drawLine(x-4, y+1, x-4, y+27);

            g.setColor( Color.WHITE );
            g.fillOval(dx, dy, 12, 12 );

            char[] num = { Character.forDigit(c.ballNumber, 10) }; 
            Font font = new Font("Courier New", Font.PLAIN, fontSize);
            g.setFont(font);
            g.setColor( Color.BLACK );
            g.drawChars(num, 0, num.length, (int)StrictMath.round(c.x+12), (int)StrictMath.round(c.y+17) );
        }
        else if ( c.ballNumber == 0 ) {
            g.setColor( Color.WHITE );
            g.fillOval((int)c.x - 1, (int)c.y - 1, 30, 30 );
        }
    }


	/*******************************************************
	*
	*
	*
	*
	********************************************************/
	private void paintPockets( Graphics g) {
		if ( pockets == null ) populatePockets();
		try {
			for (Pocket p : pockets)
				paintPocket(g, p);
			}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			paintPockets(g); // retry so the disks never not get painted
        }
	}


	/*******************************************************
	*
	*
	*
	*
	********************************************************/
	private void paintPocket( Graphics g, Pocket p) {
		if ( p == null ) return;
		g.setColor( p.color );
		g.fillOval(  p.x, p.y, p.size, p.size );
	}


	/*******************************************************
	*
	*
	*
	*
	********************************************************/
	private void paintTray( Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		String trayTitle = "The Fallen Balls";
		char[] titleChars = trayTitle.toCharArray();
		//String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		Font font = new Font("Comic Sans MS", Font.BOLD, 20 );
		g2.setFont(font);
		g2.drawChars(titleChars, 0, titleChars.length , 450, 590);
		g2.setColor( Color.GRAY );
		g2.fill3DRect(299, 598, 450, 33, true);
		g2.setColor( Color.BLACK );
		g2.draw3DRect(298, 597, 451, 34, true);
	
		paintFallen( g );
	}
	

	/*******************************************************
	*
	*
	*
	*
	********************************************************/
	public void paintFallen( Graphics g ) {
		try {
			for (Circle f : fallen)
				paintCircle(g, f);
		}
		catch (Exception ex) {
			System.out.println("ERROR IN paintFallen!: " + ex.getMessage());
		}
	}

		
	/*******************************************************
	*
	*
	*
	*
	********************************************************/
	public void addToFallen( Circle c ) {
		System.out.println("Adding fallen ball: " + c.name );
		fallen.add( c );
		int fallenCount = fallen.size();
		c.x = 270+(fallenCount*31);
		c.y = 600;
	}
	
	
	/*******************************************************
	*
	*
	*
	*
	********************************************************/
    private void paintQueueLine( Graphics g) {
		g.setColor( Color.WHITE );
		if ( queueLine == null ) return;
		g.drawLine( ((Integer)queueLine.get("x1")).intValue(),
					((Integer)queueLine.get("y1")).intValue(),
					((Integer)queueLine.get("x2")).intValue(),
					((Integer)queueLine.get("y2")).intValue());
	}



	/*******************************************************
	*
	*
	*
	*
	********************************************************/
	private void paintTable( Graphics g ) {
		g.setColor(Color.black);
		//System.out.println("Painting table outline: ");
		g.drawRect(	engine.TABLE_OFFSET_X-30, engine.TABLE_OFFSET_Y-30, SIZEX+60, SIZEY+60);
		g.drawRect(	engine.TABLE_OFFSET_X, engine.TABLE_OFFSET_Y, SIZEX, SIZEY);
		
		int x25  = (int)StrictMath.round((SIZEX/4)   + engine.TABLE_OFFSET_X);
		int x75  = (int)StrictMath.round((SIZEX/4*3) + engine.TABLE_OFFSET_X);
		int midy = (int)StrictMath.round((SIZEY/2)   + engine.TABLE_OFFSET_Y);
		g.fillOval( x25-5, midy-5, 10, 10);
		g.fillOval( x75-5, midy-5, 10, 10);
		g.setColor( Color.white );
		g.fillOval( x25-2, midy-2, 4, 4);
		g.fillOval( x75-2, midy-2, 4, 4);

		paintPockets( g );
		paintTray( g );
	}



    /******************************************************
     * public standard paint method
     *
     *
     * @param g paint
     * @param d circle
     ******************************************************/
    @Override
    public void paint(Graphics g) {
        // paint real panel stuff
        super.paint(g);

        //Make the table
        paintTable( g );

        // paint the disks
        paintCircles(g);
        if ( aimingQueueBall ) paintQueueLine( g );
    }





    /*********************************************
    *
    *
    *
    *
    *********************************************/
    public void mouseClicked(MouseEvent e) {

    }


    /*********************************************
    *
    *
    *
    *
    *********************************************/
    public void mousePressed(MouseEvent e) {
		if ( !ready ) {
			return;
		}

		System.out.println("I clicked at " + e.getPoint());

		//for some reason I need to subtract 10 pixels from each to get relative coords.
		int x = e.getPoint().x - 15;
		int y = e.getPoint().y - 15;

		//See if that point in inside a circle
		Circle k;
		for ( int j = 0 ; j < circles.size(); j++ ) {
			k = (Circle)circles.elementAt(j);
			int kx = (int)StrictMath.round(k.x);
			int ky = (int)StrictMath.round(k.y);
			int xDif = Math.abs( kx - x );
			int yDif = Math.abs( ky - y );
			int radius = (int)StrictMath.round(k.size/2);


			if ( xDif <= radius  && yDif <= radius ) {
				//System.out.println("I clicked inside: " + k.name + "!!!");
				if ( j == queBallIndex && !movingQ ) aimingQueueBall = true;
				targetCircle = j;
				k.beingDragged = true;
			}
		}
    }


    /*********************************************
    *
    *
    *
    *
    *********************************************/
    public void mouseDragged(MouseEvent e) {
		if ( !ready ) {
			return;
		}

		Circle queBall = (Circle)circles.elementAt( queBallIndex );

		int mx = e.getX();
		int my = e.getY();
		if ( aimingQueueBall ) {
			queueLine = new HashMap<String, Integer>();
			queueLine.put("x1", (int)StrictMath.round(mx));
			queueLine.put("y1", (int)StrictMath.round(my));

			if ( engine.aimHelp ) {
				int bx = (int)StrictMath.round(queBall.x)+15;
				int by = (int)StrictMath.round(queBall.y)+15;
				double dx = bx - mx;
				double dy = by - my;
				double i = 1000 / ( Math.sqrt( dx*dx + dy*dy ) );
				double ex = ( dx*i) + mx;
				double ey = ( dy*i) + my;

				queueLine.put("x2", (int)StrictMath.round(ex));
				queueLine.put("y2", (int)StrictMath.round(ey));
			}
			else {
				queueLine.put("x2", (int)StrictMath.round(queBall.x)+15);
				queueLine.put("y2", (int)StrictMath.round(queBall.y)+15);
			}
		}
		else if ( movingQ ) {
			queBall.x = mx;
			queBall.y = my;
		}
    }


    /*********************************************
    *
    *
    *
    *
    *********************************************/
    public void mouseReleased(MouseEvent e) {
		if ( !ready ) {
			return;
		}

		Circle queBall = (Circle)circles.elementAt( queBallIndex );

		System.out.println("Released the mouse at: (" + e.getX() + "," + e.getY() + ")!! aiming? " + aimingQueueBall);
		queueLine = null;

		if ( aimingQueueBall ) {
			double x1 = e.getX();
			double y1 = e.getY();
			double x2 = queBall.x+15;
			double y2 = queBall.y+15;


			//Calculate the Queball velocity based on the disance and angle
			//of the mouse at release from the center of the que ball.
			// The speed has to be a number between 0 and 10.

			double dx = (x2 - x1)/50;
			double dy = (y2 - y1)/50;
			System.out.println("X Distance=" + dx);
			System.out.println("Y Distance=" + dy);
			
			double k = 1; //( Math.abs(dx) > Math.abs(dy) ) ? 5/Math.abs(dx) : 5/Math.abs(dy);
			dx = dx * k;
			dy = dy * k;

			ready = false;

			queBall.dx = dx;
			queBall.dy = dy;

			System.out.println("Queue ball speed is: " + queBall.dx + ", " + queBall.dy);
			//movement = ( queBall.dx == 0 && queBall.dy == 0 ) ? false : true;

			engine.calcFriction( queBall );

			//System.out.println( q.toString() );
			aimingQueueBall = false;
			SoundEffect.QUE.play();
		}
		else if ( movingQ ) {
			movingQ = false;
			//queBall.x = e.getX()-15;
			//queBall.y = e.getY()-15;

			Circle j;
			for ( int a = 0 ; a < circles.size(); a++ ) {
				j = (Circle)circles.elementAt( a );

				if ( a != queBallIndex ) {
					// fix possible overlapping
					double dx = queBall.x - j.x;
					double dy = queBall.y - j.y;
					double d2 = (dx*dx) + (dy*dy);
					double circleSize2 = (j.size/2 + j.size/2) * (j.size/2 + j.size/2);

					if ( d2 < circleSize2 && engine.overlap )
						engine.fixOverlap(queBall, j);
				}
			}
		}
    }


	/***************************************************************
	*
	*
	*
	***************************************************************/
    public void mouseEntered(MouseEvent e) {}
	/***************************************************************
	*
	*
	*
	***************************************************************/
    public void mouseExited(MouseEvent e) {}


	/***************************************************************
	*
	*
	*
	***************************************************************/
    public void mouseMoved(MouseEvent e) {
		bouncer.setStatus( "Pointer is at: (" + e.getX() + "," + e.getY() + ")" );
		if ( movingQ ) {
			Circle queBall = (Circle)circles.elementAt( queBallIndex );
			int mx = e.getX();
			int my = e.getY();

			double newX = queBall.x;
			double newY = queBall.y;
			
			double xRight = SIZEX + engine.TABLE_OFFSET_X - 15;
			double xLeft  = (SIZEX*0.75) + engine.TABLE_OFFSET_X;
			
			double yBottom = SIZEY + engine.TABLE_OFFSET_Y-15;
			double yTop    = engine.TABLE_OFFSET_Y+15;

			if ( mx > xRight ) newX = xRight;
			else if ( mx < xLeft  ) newX = xLeft;
			else newX = mx;
				
			if ( my > yBottom ) newY = yBottom; 
			else if ( my < yTop ) newY = yTop;
			else newY = my;
			
			queBall.x = newX-15;
			queBall.y = newY-15;
		}
	}

	/*********************************************
	*
	*
	*
	*
	**********************************************/
	public void run() {
        // repaint every 9 ms (~100 fps)
        while (true) {
            repaint();
            try {
                Thread.sleep( 9, 1 );
            }
            catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}