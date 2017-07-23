package com.happypeople.vi.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.happypeople.vi.MessageTarget;
import com.happypeople.vi.ScreenCursorPosition;
import com.happypeople.vi.ScreenModel;
import com.happypeople.vi.ScreenModelChangedEvent;
import com.happypeople.vi.View;

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
@Component
@Scope("prototype")
public class AwtView implements View, MessageTarget {
	final static Logger log=LoggerFactory.getLogger(AwtView.class);

	/** Blinking frequency of cursor */
	private final static long C_BLINK_MILLIES=750;

	/** The swing Component where the real drawing is done. */
	private final JComponent paintingArea;

	/** Set of event listeners. */
	private final Set<ViewSizeChangedEventListener> viewSizeChangedEventListeners=new HashSet<>();

	/** Last drawn screen cursor position.
	 * TODO get rid of that, use formatted text in ScreenModel/ScreenLines instead.
	 **/
	private ScreenCursorPosition sPos=ScreenCursorPosition.ORIGIN;

	/** Timestamp blinking cursor was set to visible state (happens on every cursor movement) */
	private long cTime=System.currentTimeMillis();
	/** Timestamp until that the cursor thread should wait before triggering a repaint. */
	private long nextWakeup=cTime+C_BLINK_MILLIES;
	/** this char is drawn while drawing the blinking cursor */
	private String charUnderCursor=" ";

	private final ScreenBuffer screenBuffer=new ScreenBuffer();

	private final JFrame frame;

	/** TODO add a "addKeyEventListener(...)" method, to get rid of the
	 * constructor arg keyEventTarget.
	 * @param linesModel the data to display on screen
	 * @param keyEventTarget queue where key events are added, should be removed, and wired on the caller side
	 */
	public AwtView(final BlockingQueue<KeyEvent> keyEventTarget) {
		this.frame=new JFrame("vi");
		frame.addWindowListener(new CloseTheWindowListener(frame));

		paintingArea=new AwtViewPanel();
		frame.getContentPane().add(paintingArea);

		paintingArea.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(final ComponentEvent e) {
				final int oldX=screenBuffer.getSizeColumns();
				final int oldY=screenBuffer.getSizeLines();

				final java.awt.Component c=e.getComponent();
				screenBuffer.resize(c.getWidth(), c.getHeight());

				final int newX=screenBuffer.getSizeColumns();
				final int newY=screenBuffer.getSizeLines();

				if(oldX!=newX || oldY!=newY) {
					fireViewSizeChanged(screenBuffer.getSizeColumns(), screenBuffer.getSizeLines());
				}
			}

			@Override
			public void componentMoved(final ComponentEvent e) {
				// ignore
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				log.info("component shown");
			}

			@Override
			public void componentHidden(final ComponentEvent e) {
				log.info("component hidden");
			}
		});

		// add the listener for keyboard events
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(final KeyEvent e) {
				keyEventTarget.offer(e);
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(final KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		// the cursor thread, sleeps until nextWakeup, then repaints and increments nextWakeup by C_BLINK_MILLIES
		final Thread t=new Thread() {
			@Override
			public void run() {
				while(true) {
					final long current=System.currentTimeMillis();
					if(current<nextWakeup) {
						try {
							Thread.sleep(nextWakeup-current);
						} catch (final InterruptedException e) {
							// ignore
						}
					} else {
						nextWakeup+=C_BLINK_MILLIES;
						// TODO call screenBuffer.cursorRender() to do optimized rendering
						screenBuffer.renderCursor();
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();

		frame.setSize(400, 400);
		frame.setLocation(600, 100);
		frame.setResizable(true);
		frame.setBackground(COLOR_BACKGROUND);
		frame.setVisible(true);
	}

	@Override
	public void setVisible(final boolean visible) {
		frame.setVisible(visible);
	}

	/** This is the actual painting surface
	 */
	private class AwtViewPanel extends JComponent {

		@Override
		public void paint(final Graphics g) {
			super.paint(g);
			final Rectangle clip=g.getClipBounds();
			screenBuffer.paint(g, clip.x, clip.y, clip.x+clip.width, clip.y+clip.height);
		}
	}

	private final static Color COLOR_BACKGROUND=Color.BLACK;

	private final static class FontData {
		public Font font;
		public FontMetrics fontMetrics;
		public int lineHeight;
		public int colWidht;
	}

	private class ScreenBuffer {
		/** Immutable Datastructure used to hold Font related values. */
		private FontData fontData;

		/** Lock used to synchronize acess to image. */
		private final Object imageLock=new Object();
		/** The image buffer, initial dummy */
		private BufferedImage image=new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		private Graphics2D g=image.createGraphics();

		/** Size in columns, not pixels. */
		private int sizeColumns=0;
		/** Size in lines, not pixels. */
		private int sizeLines=0;

		ScreenBuffer() {
			setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
		}

		public void setFont(final Font font) {
			final BufferedImage tmpImage=new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g=tmpImage.createGraphics();
			final FontData fontData=new FontData();
			fontData.font=font;
			fontData.fontMetrics=g.getFontMetrics(font);
			fontData.lineHeight=Math.max(1, fontData.fontMetrics.getHeight());
			fontData.colWidht=Math.max(1, fontData.fontMetrics.stringWidth("0123456789abcdef")/16);
			this.fontData=fontData;
		}

		public void resize(final int width, final int height) {
			log.info("resize, pixels, width="+width+" height="+height);
			final int newSizeColumns=Math.max(1, width/fontData.colWidht);
			final int newSizeLines=Math.max(1, height/fontData.lineHeight);

			if(newSizeColumns!=sizeColumns || newSizeLines!=sizeLines) {
				log.info("really resize, cols/width="+newSizeColumns+" lines/height="+newSizeLines);
				final BufferedImage newImage=new BufferedImage(
						newSizeColumns*fontData.colWidht, newSizeLines*fontData.lineHeight, BufferedImage.TYPE_INT_RGB);
				final Graphics2D g=newImage.createGraphics();
				g.setColor(COLOR_BACKGROUND);
				g.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());

				synchronized(imageLock) {
					final int intersectX=Math.min(newImage.getWidth(), image.getWidth());
					final int intersectY=Math.min(newImage.getHeight(), image.getHeight());
					g.drawImage(image,
							0, 0, intersectX, intersectY,
							0, 0, intersectX, intersectY,
							null);
					this.image=newImage;
					this.g=g;
					sizeColumns=newSizeColumns;
					sizeLines=newSizeLines;
				}
			}
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

		/** Creates inplace iterator for
		 * @param str source string
		 * @param maxLen len of chunks of str
		 * @return Iterable to iterator over parts of str of max len maxLen
		 */
		public Iterable<String> split2line(final String str, final int maxLen) {
			return new Iterable<String>() {
				int idx=0;
				@Override
				public Iterator<String> iterator() {
					return new Iterator<String>() {
						@Override
						public boolean hasNext() {
							return idx==0 || idx<str.length();
						}
						@Override
						public String next() {
							final int len=Math.min(str.length()-idx, maxLen);
							final String split=str.substring(idx, idx+len);
							idx+=Math.max(1, len);
							return split;
						}
					};
				}
			};
		}

		private void renderCursor() {
			final boolean cursorVisible=(((System.currentTimeMillis()-cTime)/C_BLINK_MILLIES)&0x1)==0;

			final int lineStartPx=(int)(fontData.lineHeight*sPos.getY());
			final int colStartPx=(int)(fontData.colWidht*sPos.getX());
			log.info("rendering cursor at: "+colStartPx+":"+lineStartPx+" char:"+charUnderCursor+" visible:"+cursorVisible);
			g.setColor(cursorVisible?Color.WHITE:Color.BLACK);
			g.fillRect(colStartPx, lineStartPx, fontData.colWidht, fontData.lineHeight);

			g.setColor(cursorVisible?Color.BLACK:Color.WHITE);
			g.drawString(charUnderCursor, colStartPx, lineStartPx+fontData.fontMetrics.getMaxAscent());
			paintingArea.repaint(colStartPx, lineStartPx, fontData.colWidht, fontData.lineHeight);
		}

		public void render(final ScreenModelChangedEvent evt) {
			final ScreenModel screenModel=evt.getSource();
			sPos=screenModel.getCursorPosition();

			g.setFont(fontData.font);

			int baselinePx=fontData.fontMetrics.getMaxAscent(); // baseline of first line
			final int lengthLimit=sizeLines*sizeColumns;

			int logicalLineNo=0;
			int screenLineNo=0;
			int linestartPx=0;
			while(baselinePx<image.getHeight() && logicalLineNo<screenModel.getDataLineCount()) {
				final String logicalLine=screenModel.render(logicalLineNo, lengthLimit);

				for(final String screenLine : split2line(logicalLine, sizeColumns)) {

					// calc real Position. cPosX can be after the end of line. In this case we
					// display the cursor at the last char of the line.
					// If the line is empty we position the cursor at lCPosX=0
					//final long lCPosX= cPosX<screenLine.length() ? cPosX : (screenLine.length()-1<0? 0 : screenLine.length()-1);

					// redraw background
					g.setColor(COLOR_BACKGROUND);
					g.fillRect(0, linestartPx, image.getWidth(), fontData.lineHeight);
					g.setColor(Color.WHITE);

					// simply draw the string
					g.drawString(screenLine, 0, baselinePx);

					/*
					if(cPosY==screenLineNo && (((System.currentTimeMillis()-cTime)/C_BLINK_MILLIES)&0x1)==0) { // cursor visible blink phase
						// split the line in the part before the cursor, the cursor, and the part after the cursor
						int cursorXoffset=0;

						if(lCPosX>0) { // left of cursor
							final String leftStr=screenLine.substring(0, (int)lCPosX);
							cursorXoffset=fm.stringWidth(leftStr);
							g.drawString(leftStr, 0, baselinePx-fm.getDescent());
						}
						// for empty lines there is no char under the cursor, no char at position 0. In this case we use the blank
						final String cursorChar=screenLine.length()>0?screenLine.substring((int)lCPosX, (int)lCPosX+1) : "\b";
						final int cursorWidth=fm.stringWidth(cursorChar);

						// draw the Cursor, that is the Cursor background,
						g.setColor(C_COLOR);
						g.fillRect(cursorXoffset, baselinePx-lineHeight, cursorWidth, lineHeight);

						// ...and the character in the cursor
						g.setColor(Color.WHITE);
						g.drawString(cursorChar, cursorXoffset, baselinePx-fm.getDescent());

						// draw the part right of the cursor
						if(lCPosX<screenLine.length()-1) {
							final String rightStr=screenLine.substring((int)lCPosX+1);
							g.drawString(rightStr, cursorXoffset+cursorWidth, baselinePx-fm.getDescent());
						}
					} else {
						// simply draw the line string
						g.drawString(screenLine, 0, baselinePx-fm.getDescent());
					}
					 */
					baselinePx+=fontData.lineHeight;
					linestartPx+=fontData.lineHeight;
					if(screenLineNo==sPos.getY()) {
						if(screenLine.length()>sPos.getX())
							charUnderCursor=screenLine.substring((int)sPos.getX(), (int)sPos.getX()+1);
						else
							charUnderCursor="\b";
					}
					screenLineNo++;
				}
				logicalLineNo++;
			}
			// draw fillers until end of screen
			while(screenLineNo<getSizeLines()) {
				g.setColor(COLOR_BACKGROUND);
				g.fillRect(0, linestartPx, image.getWidth(), fontData.lineHeight);
				g.setColor(Color.WHITE);
				g.drawString("~", 0, baselinePx);
				baselinePx+=fontData.lineHeight;
				linestartPx+=fontData.lineHeight;
				screenLineNo++;
			}

			renderCursor();

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

	@Override
	public void cusorPositionChanged(final ScreenModelChangedEvent evt) {
		resetCTime();
		// TODO render cursor only
		screenBuffer.render(evt);
	}

	/* ScreenModelChangedEventListener implementation */
	@Override
	public void screenModelChanged(final ScreenModelChangedEvent evt) {
		resetCTime();
		// TODO render only the changed lines, optimize rendering
		screenBuffer.render(evt);
	}

	protected void fireViewSizeChanged(final int sizeX, final int sizeY) {
		final ViewSizeChangedEvent evt=createEvent(sizeX, sizeY);

		for(final ViewSizeChangedEventListener listener : viewSizeChangedEventListeners)
			listener.viewSizeChanged(evt);
	}

	@Override
	public void addViewSizeChangedEventListener(final ViewSizeChangedEventListener listener) {
		viewSizeChangedEventListeners.add(listener);
		listener.viewSizeChanged(createEvent(screenBuffer.getSizeColumns(), screenBuffer.getSizeLines()));
	}

	private ViewSizeChangedEvent createEvent(final int x, final int y) {
		return new ViewSizeChangedEvent() {
			@Override
			public int getSizeX() {
				return x;
			}

			@Override
			public int getSizeY() {
				return y;
			}
		};
	}

	@Override
	public void showMessage(final String msg) {
		// TODO implement
		System.out.println("showMessage: "+msg);
	}
}
