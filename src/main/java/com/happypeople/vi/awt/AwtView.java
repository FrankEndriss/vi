package com.happypeople.vi.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.happypeople.vi.CursorModel.CursorPositionChangedEvent;
import com.happypeople.vi.LinesModel;
import com.happypeople.vi.LinesModelEditor.LinesModelChangedEvent;
import com.happypeople.vi.View;
import com.happypeople.vi.ViewModel.FirstLineChangedEvent;

/** How painting should be done:
 * The input controller gets commands by the user (AWT-Thread), and triggers changes to the model Objects (VI-Thread)
 * These changes should be rendered to a background buffer image. (VI-Thread)
 * On any change to the background buffer image, we record the changed areas/positions in some kind of queue/list. (VI-Thread)
 * Then on any change a repaint() is triggered (VI-Thread)
 * In repaint(), we draw() all changed areas from background() to the real Component. (AWT-Thread)
 * 
 * Record of changed areas can/should be done like the clip area in awt, ie two Rectangles are simple combined to
 * one big Rectrangle, which leads to that we can simply use getClip() of the component, by
 * Component.repaint(x, y, w, h);
 */
public class AwtView implements View {
	
	/** Blinking frequency of cursor */
	private final static long C_BLINK_MILLIES=500;
	/** Color of the cursor */
	private final static Color C_COLOR=Color.RED;

	private final LinesModel linesModel;
	private final Component paintingArea;
	
	private final Set<ViewSizeChangedEventListener> viewSizeChangedEventListeners=new HashSet<ViewSizeChangedEventListener>();
	
	/** current cursor position X in lines/cols, not pixels */
	private int cPosX=0;
	/** current cursor position Y in lines/cols, not pixels */
	private int cPosY=0;

	/** Timestamp blinking cursor was set to visible state (happens on every cursor movement) */
	private long cTime=System.currentTimeMillis();
	/** Timestamp until that the cursor thread should wait before triggering a repaint. */
	private long nextWakeup=cTime+C_BLINK_MILLIES;
	
	private ScreenBuffer screenBuffer=new ScreenBuffer();

	/**
	 * @param linesModel the data to display on screen
	 * @param keyListener listener which gets the input events
	 */
	public AwtView(final LinesModel linesModel, final BlockingQueue<KeyEvent> inputQueue) {
		// some special hook
		System.setProperty("sun.awt.noerasebackground", "true");

		this.linesModel=linesModel;

		final JFrame frame=new JFrame("vi");
		
		frame.addWindowListener(new CloseTheWindowListener(frame));

		paintingArea=new AwtViewPanel();
		frame.getContentPane().add(paintingArea);

		// for AWT use:
		//frame.add(paintingArea);

		paintingArea.addComponentListener(new ComponentListener() {

			public void componentResized(ComponentEvent e) {
				final Component c=e.getComponent();
				screenBuffer.resize(c.getWidth(), c.getHeight());
				fireViewSizeChanged(screenBuffer.getSizeColumns(), screenBuffer.getSizeLines());
			}

			public void componentMoved(ComponentEvent e) {
				// ignore
			}

			public void componentShown(ComponentEvent e) {
				System.out.println("component shown");
			}

			public void componentHidden(ComponentEvent e) {
				System.out.println("component hidden");
			}
		});

		// add the listener for keyboard events
		frame.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
				inputQueue.offer(e);
			}

			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		// the cursor thread, sleeps until nextWakeup, then repaints and increments nextWakeup by C_BLINK_MILLIES
		new Thread() {
			public void run() {
				while(true) {
					final long current=System.currentTimeMillis();
					if(current<nextWakeup) {
						try {
							Thread.sleep(nextWakeup-current);
						} catch (InterruptedException e) {
							// ignore
						}
					} else {
						nextWakeup+=C_BLINK_MILLIES;
						screenBuffer.render();
					}
				}
			}
		}.start();
		
		frame.setSize(400, 400);
		frame.setLocation(100, 100);
		frame.setResizable(true);
		frame.setBackground(COLOR_BACKGROUND);
		frame.setVisible(true);
	}
	
	/** This is the actual painting surface
	 */
	private class AwtViewPanel extends JComponent {
		
		@Override
		public void paint(final Graphics g) {
			System.out.println("in paint(G)");
			super.paint(g);
			final Rectangle clip=g.getClipBounds();
			screenBuffer.paint(g, clip.x, clip.y, clip.x+clip.width, clip.y+clip.height);
		}
	}

	private final static Color COLOR_BACKGROUND=Color.BLACK;

	private class ScreenBuffer {

		private final Font font=new Font("monospaced", Font.PLAIN, 15);

		/** Lock used to synchronize acess to image. */
		private final Object imageLock=new Object();
		/** The image buffer, initial dummy */
		private BufferedImage image=new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		
		/** Size in columns, not pixels. */
		private int sizeColumns=0;
		/** Size in lines, not pixels. */
		private int sizeLines=0;
		
		public void resize(final int width, final int height) {
			final BufferedImage newImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g=newImage.createGraphics();
			g.setColor(COLOR_BACKGROUND);
			g.fillRect(0, 0, width, height);

			synchronized(imageLock) {
				final int intersectX=Math.min(width, image.getWidth());
				final int intersectY=Math.min(height, image.getHeight());
				g.drawImage(image, 
					0, 0, intersectX, intersectY, 
					0, 0, intersectX, intersectY, 
					null);
				image=newImage;
			}
			
			FontMetrics fm=g.getFontMetrics(font);
			int lineHeight=fm.getHeight()+fm.getDescent();
			int colWidth=fm.stringWidth("0123456789abcdef")/16;
			sizeLines=height/lineHeight;
			sizeColumns=width/colWidth;
		}

		/** This methods simply paints the buffer image to target.
		 * But only the part within the clip.
		 * @param target
		 * @param x, y, x2, y2 The clipping rectangle
		 */
		public void paint(final Graphics target, final int x, final int y, final int x2, final int y2) {
			synchronized(imageLock) {
				target.drawImage(image,
						x, y, x2, y2,
						x, y, x2, y2,
						null);
			}
		}

		public void render() {

			// TODO synchronize
			
			final long callStart=System.currentTimeMillis();
			final Graphics2D g=image.createGraphics();
			g.setFont(font);

			final FontMetrics fm=g.getFontMetrics(font);

			int lineNo=0;
			int baselinePx=0;
			int lineHeight=fm.getHeight()+fm.getDescent();
			
			// TODO optimize to draw only what is inside g.getClipBounds();

			while(baselinePx<image.getHeight() && lineNo<linesModel.getSize()) {
				baselinePx+=lineHeight;
				final String str=linesModel.get(lineNo);
				// calc real Position. cPosX can be after the end of line. In this case we
				// display the cursor at the last char of the line.
				// If the line is empty we position the cursor at lCPosX=0
				final int lCPosX= cPosX<str.length() ? cPosX : (str.length()-1<0? 0 : str.length()-1);
				
				// TODO wrap lines longer than getWidth()
				//fm.stringWidth(str);

				// redraw background
				g.setColor(Color.BLACK);
				g.fillRect(0, baselinePx-lineHeight, image.getWidth(), lineHeight);
				g.setColor(Color.WHITE);

				if(cPosY==lineNo && (((System.currentTimeMillis()-cTime)/C_BLINK_MILLIES)&0x1)==0) { // cursor visible blink phase
					// split the line in the part before the cursor, the cursor, and the part after the cursor
					int cursorXoffset=0;

					if(lCPosX>0) { // left of cursor
						final String leftStr=str.substring(0, lCPosX);
						cursorXoffset=fm.stringWidth(leftStr);
						g.drawString(leftStr, 0, baselinePx-fm.getDescent());
					}
					// for empty lines there is no char under the cursor, no char at position 0. In this case we use the blank
					final String cursorChar=str.length()>0?str.substring(lCPosX, lCPosX+1) : "\b";
					final int cursorWidth=fm.stringWidth(cursorChar);

					// draw the Cursor, that is the Cursor background,
					g.setColor(C_COLOR);
					g.fillRect(cursorXoffset, baselinePx-lineHeight, cursorWidth, lineHeight);

					// ...and the character in the cursor
					g.setColor(Color.WHITE);
					g.drawString(cursorChar, cursorXoffset, baselinePx-fm.getDescent());

					// draw the part right of the cursor
					if(lCPosX<str.length()-1) {
						final String rightStr=str.substring(lCPosX+1);
						g.drawString(rightStr, cursorXoffset+cursorWidth, baselinePx-fm.getDescent());
					}
				} else {
					// simply draw the line string
					g.drawString(str, 0, baselinePx-fm.getDescent());
				}

				lineNo++;
			}
			//System.out.println("paint "+callStart+" in "+(System.currentTimeMillis()-callStart)+"ms");
			// trigger repaint int AWT-Thread
			paintingArea.repaint();
		}

		public int getSizeLines() {
			return sizeLines;
		}

		public int getSizeColumns() {
			return sizeColumns;
		}


	}

	private void resetCTime() {
		cTime=System.currentTimeMillis();
		nextWakeup=cTime+C_BLINK_MILLIES;
	}

	public void changedEvent(LinesModelChangedEvent evt) {
		resetCTime();
		// TODO render only the changed lines
		screenBuffer.render();
	}

	public void cursorPositionChanged(CursorPositionChangedEvent evt) {
		resetCTime();
		cPosX=evt.getScreenX();
		cPosY=evt.getScreenY();
		// TODO repaint only the cursor
		System.out.println("new cursor position, x="+cPosX+", y="+cPosY);
		screenBuffer.render();
	}

	public void firstLineChanged(FirstLineChangedEvent evt) {
		resetCTime();
		screenBuffer.render();
	}

	protected void fireViewSizeChanged(final int sizeX, final int sizeY) {
		final ViewSizeChangedEvent evt=new ViewSizeChangedEvent() {
			public int getSizeX() {
				return sizeX;
			}

			public int getSizeY() {
				return sizeY;
			}
		};
		
		for(ViewSizeChangedEventListener listener : viewSizeChangedEventListeners)
			listener.viewSizeChanged(evt);
	}

	public void addViewSizeChangedEventListener(ViewSizeChangedEventListener listener) {
		viewSizeChangedEventListeners.add(listener);
	}
}
