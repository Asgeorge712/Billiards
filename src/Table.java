package bounce;

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
	int queBallIndex = -1;

    Bouncer bouncer;   		// to reference shuffle window
    Engine  engine;    		// the engine that drives the game
    Color[] colors;    		// disk colour (if no icon)
    boolean ready;     		// must be true to go again
    //boolean movement = true;
    public boolean scratched = false;
    public boolean movingQ = false;

    Vector<Pocket> pockets;	//Collection of pockets on the table;
   	private Thread t;

	public static final double CoF = 100;


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
        // always call super
        super();

        // makes the moving display smoother
        setDoubleBuffered(true);

        // default bg colour
        setBackground(new Color (51, 102, 51));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        // add mouse listeners
        addMouseListener(this);
        addMouseMotionListener(this);

        // reference to shuffle
        this.bouncer = bouncer;

		//init circles.
        circles = new Vector<Circle>();
        // start engine
        engine = new Engine( this );

        populateCircles();
        engine.setCircles( circles );

        populatePockets();

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
    public void readdQueueBall() {
		boolean foundQueueBall = false;
		for ( Circle c : circles ) {
			if ( c.name.equals("Queue Ball") ) foundQueueBall = true;
		}

		if ( !foundQueueBall ) {
			double cx =  800+engine.TABLE_OFFSET_X;
			double cy = SIZEY/2+engine.TABLE_OFFSET_Y;

			System.out.println("Adding que ball at (x,y): " + cx + "," + cy);

			Circle c = new Circle("Que Ball", Color.WHITE, cx, cy, 0, 0, 30);
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
		Circle c;

		double midy = SIZEY/2+engine.TABLE_OFFSET_Y-15;
		double firstRow = 150 + engine.TABLE_OFFSET_X;
		double bOffset = 16;

		// name, Color color, double x, double y, int speed, double direction, int size) {

		c = new Circle("Que Ball", Color.WHITE, 800+engine.TABLE_OFFSET_X, midy, 0, 0, 30);
		circles.add( c );

/**/
		//First Row (going down)
		c = new Circle("One Ball", new Color(255, 255, 102),firstRow, midy-62, 0, 0, 30);
		circles.add( c );
		c = new Circle("Two Ball", new Color( 51, 51, 255), firstRow, midy-31, 0, 0, 30);
		circles.add( c );
		c = new Circle("Three Ball", new Color(204, 0, 51), firstRow, midy, 0, 0, 30);
		circles.add( c );
		c = new Circle("Four Ball", new Color(255, 0, 153), firstRow, midy+31, 0, 0, 30);
		circles.add( c );
		c = new Circle("Five Ball", new Color(255, 102, 0), firstRow, midy+62, 0, 0, 30);
		circles.add( c );



		//Second Row
		c = new Circle("Six Ball", new Color(51, 255, 0),  firstRow+31, midy-47, 0, 0, 30);
		circles.add( c );
		c = new Circle("Seven Ball", new Color(102, 0, 0), firstRow+31, midy-16, 0, 0, 30);
		circles.add( c );
		c = new Circle("Eight Ball", Color.BLACK,          firstRow+31, midy+16, 0, 0, 30);
		circles.add( c );
		c = new Circle("Nine Ball", new Color(204, 204, 0),firstRow+31, midy+47, 0, 0, 30);
		circles.add( c );


		//Third Row
		c = new Circle("Ten Ball", new Color(153, 0, 255), firstRow+62, midy-31, 0, 0, 30);
		circles.add( c );
		c = new Circle("Eleven Ball", new Color(153, 0, 0), firstRow+62, midy, 0, 0, 30);
		circles.add( c );
		c = new Circle("Twelve Ball", new Color(255, 0, 153), firstRow+62, midy+31, 0, 0, 30);
		circles.add( c );
		//Forth Row
		c = new Circle("Thirteen Ball", new Color(153, 255, 153), firstRow+93, midy-16, 0, 0, 30);
		circles.add( c );
		c = new Circle("Fourteen Ball", new Color(0, 0, 153), firstRow+93, midy+16, 0, 0, 30);
		circles.add( c );

		//Fifth Row
		c = new Circle("Fifteen Ball", new Color(0, 0, 255), firstRow+124, midy, 0, 0, 30);
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
        g.setColor( c.color );
        g.fillOval((int)c.x - 1, (int)c.y - 1, c.size, c.size );
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

			double dx = x2 - x1;
			double dy = y2 - y1;

			double k = ( Math.abs(dx) > Math.abs(dy) ) ? 10/Math.abs(dx) : 10/Math.abs(dy);
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
			SoundEffect.QUEUE.play();
		}
		else if ( movingQ ) {
			movingQ = false;
			queBall.x = e.getX()-15;
			queBall.y = e.getY()-15;

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

			double xRight  = SIZEX + engine.TABLE_OFFSET_X - 30;
			double yBottom = SIZEY + engine.TABLE_OFFSET_Y - 30;

			if ( mx < xRight && mx > engine.TABLE_OFFSET_X && my < yBottom && my > engine.TABLE_OFFSET_Y ) {
				queBall.x = e.getX()-15;
				queBall.y = e.getY()-15;
			}
		}
	}



	/***************************************************************
	*
	*
		public class Runner implements Runnable {
		public void run() {

			Circle s1;
			int speed;
			double dir;
			for (int i = 0; i < circles.size(); i++) {
				s1 = circles.elementAt(i);
				if ( s1.dx != 0 || s1.dy != 0 ) {
					movement = true;
					ready = false;
				}
			}

			long time;
			while ( movement ) {
				time = System.currentTimeMillis();

				Circle c;
				boolean foundOneMoving = false;

				for (int i = 0; i < circles.size(); i++) {
					c = circles.elementAt(i);

					//if ( !c.beingDragged() )
					engine.calcPositions( circles );

					if ( c.dx != 0 || c.dy != 0 ) {
						//System.out.println( "Table.Runner: The " + c.name + " is moving! ");
						foundOneMoving = true;
					}
				}

				//revalidate();
				//repaint();

				//We will sleep for 10 milliseconds in total max.
				time = System.currentTimeMillis() - time;
				//System.out.println("this run took " + time + " milliseconds.");
				time = 10 - time;
				if ( time < 0 ) time = 0;
				try {
					Thread.sleep( time, 1 );
				}
				catch( InterruptedException e ) {
					System.out.println("ERROR!! " + e.getMessage() );
				}

				if ( !foundOneMoving ) {
					System.out.println("Nothing is moving, stop loop.");
					movement = false;
				}
			}

			if ( scratched ) {
				bouncer.setStatus( "You scratched!!  You can move the Queue Ball." );
				readdQueueBall();
				movingQ = true;
				scratched = false;
			}
			ready = true;
		}
	}
*
	***************************************************************/


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