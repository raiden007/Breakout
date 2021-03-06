/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;


	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	public void run() {
		/* You fill this in, along with any subsidiary methods */
        addMouseListeners();
		setupGame();
		playGame();
	}
	
	// Setups the game
	private void setupGame() {
		setupBricks();
		setupPaddle();
	}
		
	// Places the bricks
	private void setupBricks() {
		
		int startXPosition = 0;		
		int startYPosition = BRICK_Y_OFFSET;
		// For each row adds bricks and color
		for (int i=0;i<NBRICK_ROWS;i++) {
			for (int j=0;j<NBRICKS_PER_ROW;j++) {
				rect = new GRect(startXPosition, startYPosition, BRICK_WIDTH, BRICK_HEIGHT);
				add (rect);
				startXPosition += BRICK_WIDTH+BRICK_SEP;
				rect.setFilled(true);
				if (i==0||i==1) {
					rect.setFillColor(Color.RED);
				} else if (i==2||i==3){
					rect.setFillColor(Color.ORANGE);
				} else if (i==4||i==5) {
					rect.setFillColor(Color.YELLOW);
				} else if (i==6||i==7) {
					rect.setFillColor(Color.GREEN);
				} else {
					rect.setFillColor(Color.CYAN);
				}
			}
			startXPosition = 0;
			startYPosition += BRICK_HEIGHT+BRICK_SEP;
		}
		
		
	}
	
	// Sets up the paddle
	private void setupPaddle() {
		int paddleXPosition = (WIDTH-PADDLE_WIDTH)/2;
		paddle = new GRect (paddleXPosition,HEIGHT-PADDLE_Y_OFFSET,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	// Tracks the mouse coordonates and moves the paddle
	public void mouseMoved(MouseEvent e) {
		double paddleXPosition = e.getX() - lastX;
		paddle.move(paddleXPosition, 0);
		lastX = e.getX();
		if (lastX<0) {
			paddle.setLocation(0, HEIGHT-PADDLE_Y_OFFSET);
		} else if (lastX>WIDTH-PADDLE_WIDTH) {
			paddle.setLocation(WIDTH-PADDLE_WIDTH, HEIGHT-PADDLE_Y_OFFSET);
		}
	}
	
	// Play the game
	public void playGame() {
		setupBall();
	}
	
	// Places the ball and moves it
	public void setupBall() {
		int ballXPosition = (WIDTH-BALL_RADIUS)/2;
		int ballYPosition = (HEIGHT-BALL_RADIUS)/2;
		ball = new GOval (ballXPosition,ballYPosition,BALL_RADIUS,BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy += 3;
		speed = 10;
		showTimer();
		// moving the ball
		int lives = NTURNS;
		int timesHitPaddle = 0;
		while (lives>0) {
			ball.move(vx, vy);
			pause(speed);
			if (ball.getY()>=570) {
				lives--;
				vy = -vy;
			} else if (ball.getY()<=0) {
				vy = -vy;
			} else if (ball.getX()<=0) {
				vx = -vx;
			} else if (ball.getX()>=390) {
				vx = -vx;
			}
			// Check for collisions
			GObject collider = getCollidingObject(ball.getX(),ball.getY());
//			println(collider);
			// If it's the paddle reverse the Y movement
			if (collider==paddle) {
				timesHitPaddle++;
				vy = -vy;
				if (timesHitPaddle==7) {
					speed = speed - 0.5;
					timesHitPaddle=0;
				}
			// If not then it's only a brick so reverse the movement and remove the object
			}  else if (collider!=null) {
				remove(collider);
				vy = -vy;
				removedBricks++;
//				println(removedBricks);
				if (removedBricks>=100) {
					GLabel label = new GLabel ("YOU WON!!!",(WIDTH-200)/2,HEIGHT/2);
					label.setFont("Serif-36");
					label.setColor(Color.RED);
					add(label);
				}
				
				
			}
		}
		// Label to show when you have lost
		GLabel label = new GLabel ("YOU LOST!!!",(WIDTH-200)/2,HEIGHT/2);
		label.setFont("Serif-36");
		label.setColor(Color.RED);
		add(label);
	}
	
	// Get Collision method
	private GObject getCollidingObject(double x, double y) {
		// Get elements in all 4 corners of the ball
		GObject topLeftCollision = getElementAt(x,y);
		GObject topRightCollision = getElementAt(x+10,y);
		GObject lowerLeftCollision = getElementAt(x,y+10);
		GObject lowerRightCollision = getElementAt(x+10,y+10);
		// If there is no collision on top left
		if (topLeftCollision==null) {
			// And no collision on top right
			if (topRightCollision==null) {
				// And no collision on lower left
				if (lowerLeftCollision==null) {
					// And no collision on lower right return null
					if (lowerRightCollision==null) {
						return null;
					// Otherwise return where the collision occured
					} else {
						return lowerRightCollision;
					}
				} else {
					return lowerLeftCollision;
				}
			} else {
				return topRightCollision;
			}
		} else {
			return topLeftCollision;
		}
		
	}
	
	private void showTimer() {
		int seconds = 3;
		for (int i=0;i<3;i++) {
			setSize(400,800);
			GLabel label = new GLabel (""+seconds,(WIDTH-50)/2,(HEIGHT-100)/2);
			label.setFont("Serif-36");
			label.setColor(Color.RED);
			add(label);
			pause(1000);
			remove(label);
			seconds--;
		}

	}
	
	
	
	// Instance Variables
	private GRect rect;
	private GRect paddle;
	private double lastX;
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double speed;
	private int removedBricks=0;
}
