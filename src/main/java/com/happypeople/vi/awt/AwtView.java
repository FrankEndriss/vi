package com.happypeople.vi.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
public class AwtView implements View {
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

	private String currentMessage;

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
				final java.awt.Component c=e.getComponent();
				recalcViewSizes(c.getWidth(), c.getHeight());
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

		// Add the listener for keyboard events.
		// Note that keyTyped and keyPressed
		// Events are forwarded, keyReleased are not.
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(final KeyEvent e) {
				keyEventTarget.offer(e);
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				keyEventTarget.offer(e);
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

	private void recalcViewSizes(final int pxX, final int pxY) {
		final int oldX=screenBuffer.getSizeColumns();
		final int oldY=screenBuffer.getSizeLines();

		screenBuffer.resize(pxX, pxY);

		final int newX=screenBuffer.getSizeColumns();
		final int newY=screenBuffer.getSizeLines();

		if(oldX!=newX || oldY!=newY) {
			fireViewSizeChanged(screenBuffer.getSizeColumns(), screenBuffer.getSizeLines());
		}
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

	/** Changes the size of the current Font by increment
	 * @param increment usually 1 or -1
	 */
	@Override
	public void adjustFontSize(final int increment) {
		screenBuffer.adjustFontSize(increment);
		// force repaint
		if(paintingArea!=null)
			screenBuffer.resize(paintingArea.getWidth(), paintingArea.getHeight());
	}

	private class ScreenBuffer {
		private final String fontName=Font.MONOSPACED;
		private final int fontStyle=Font.PLAIN;
		private int fontSize=15;

		/** Immutable structure used to hold Font related values. */
		private FontData fontData;

		/** Lock used to synchronize access to image. */
		private final Object imageLock=new Object();
		/** The image buffer, initial dummy */
		private BufferedImage image=new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		private Graphics2D g=image.createGraphics();

		/** Size in columns, not pixels. */
		private int sizeColumns=0;
		/** Size in lines, not pixels. */
		private int sizeLines=0;

		ScreenBuffer() {
			setFont(new Font(fontName, fontStyle, fontSize));
		}

		public void adjustFontSize(final int increment) {
			if(fontSize+increment>0) {
				fontSize+=increment;
				setFont(new Font(fontName, fontStyle, fontSize));
			}
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
				if(g instanceof Graphics2D)
				{
					final Graphics2D g2d = g;
					g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
				}
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

					// redraw background line by line
					g.setColor(COLOR_BACKGROUND);
					g.fillRect(0, linestartPx, image.getWidth(), fontData.lineHeight);
					g.setColor(Color.WHITE);

					// simply draw the string
					g.drawString(screenLine, 0, baselinePx);

					baselinePx+=fontData.lineHeight;
					linestartPx+=fontData.lineHeight;
					if(screenLineNo==sPos.getY()) {
						if(screenLine.length()>sPos.getX())
							charUnderCursor=screenLine.substring((int)sPos.getX(), (int)sPos.getX()+1);
						else
							charUnderCursor=" ";
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

			renderMessageLine(screenModel);
			renderCursor();

			// trigger repaint int AWT-Thread
			paintingArea.repaint();
		}

		/** If currentMessage is not empty then displays
		 * currentMessage in last line of screen.
		 * @param screenModel
		 */
		private void renderMessageLine(final ScreenModel screenModel) {
			final String lMsg=screenModel.getCurrentMessage();
			log.info("renderMessageLine(), line="+lMsg);
			if(lMsg!=null && lMsg.length()>0) {
				// find position of last line on screen
				final long screenLines=screenModel.getScreenLineCount();
				final long linePx=fontData.fontMetrics.getMaxAscent() + ((screenLines-1)*fontData.lineHeight);
				log.info("renderMessageLine(), linePx="+linePx);
				g.setColor(COLOR_BACKGROUND);
				g.fillRect(0, (int)linePx-fontData.lineHeight, image.getWidth(), fontData.lineHeight);
				g.setColor(Color.WHITE);
				g.drawString(lMsg, 0, linePx);
			}
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
}
