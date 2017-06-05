package com.happypeople.vi.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.happypeople.vi.LinesModel;
import com.happypeople.vi.LinesModel.LinesModelChangedEvent;
import com.happypeople.vi.View;
import com.happypeople.vi.ViewModel.CursorPositionChangedEvent;
import com.happypeople.vi.ViewModel.FirstLineChangedEvent;

public class AwtView implements View {
	
	/** Blinking frequency of cursor */
	private final static long C_BLINK_MILLIES=500;
	private final static Color C_COLOR=Color.RED;
	private final static int C_WIDTH=13;

	private final LinesModel linesModel;
	private final Component paintingArea;
	
	/** current cursor position X */
	private int cPosX=0;
	/** current cursor position Y */
	private int cPosY=0;
	/** Timestamp blinking cursor was set to visible state (happens on every cursor movement) */
	private long cTime=System.currentTimeMillis();
	/** Timestamp until that the cursor thread should wait before triggering a repaint. */
	private long nextWakeup=cTime+C_BLINK_MILLIES;
	
	public AwtView(LinesModel linesModel) {
		this.linesModel=linesModel;

		final Frame frame=new Frame("vi");
		
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// TODO ask to save current file if needed
				frame.dispose();
			}

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}

			public void windowIconified(WindowEvent e) {
				// TODO stop blinking cursor
			}

			public void windowDeiconified(WindowEvent e) {
				// TODO start blinking cursor
			}

			public void windowActivated(WindowEvent e) {
				// TODO start cursor blinking
			}

			public void windowDeactivated(WindowEvent e) {
				// TODO stop blinking cursor
			}
		});

		frame.add(paintingArea=new AwtViewPanel());

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
						paintingArea.repaint();
					}
				}
			}
		}.start();
		
		frame.setSize(400, 400);
		frame.setLocation(100, 100);
		frame.setResizable(true);
		frame.setVisible(true);
	}
	
	/** This is the actual painting surface
	 */
	private class AwtViewPanel extends Component {
		private final Font font=new Font("monospaced", Font.PLAIN, 15);
		public void paint(Graphics g) {
			g.setFont(font);
			final FontMetrics fm=g.getFontMetrics(font);
			int lineNo=0;
			int baselinePx=0;
			int lineHeight=fm.getHeight()+fm.getDescent();
			
			// TODO optimize to draw only what is inside g.getClipBounds();

			while(baselinePx<getHeight() && lineNo<linesModel.getSize()) {
				baselinePx+=lineHeight;
				final String str=linesModel.get(lineNo);

				// TODO wrap lines longer than getWidth()
				//fm.stringWidth(str);

				// redraw background
				g.setColor(Color.BLACK);
				g.fillRect(0, baselinePx-lineHeight, getWidth(), lineHeight);
				g.setColor(Color.WHITE);

				if(cPosY==lineNo && (((System.currentTimeMillis()-cTime)/C_BLINK_MILLIES)&0x1)==0) { // cursor visible blink phase
					// split the line in the part before the cursor, the cursor, and the part after the cursor
					int cursorXoffset=0;
					if(cPosX>0) { // left of cursor
						final String leftStr=str.substring(0, cPosX);
						cursorXoffset=fm.stringWidth(leftStr);
						g.drawString(leftStr, 0, baselinePx-fm.getDescent());
					}
					final String cursorChar=str.substring(cPosX, cPosX+1);
					final int cursorWidth=fm.stringWidth(cursorChar);

					// draw the Cursor, that is the Cursor background,
					g.setColor(C_COLOR);
					g.fillRect(cursorXoffset, baselinePx-lineHeight, cursorWidth, lineHeight);
					// ...and the character in the cursor
					g.setColor(Color.WHITE);
					g.drawString(cursorChar, cursorXoffset, baselinePx-fm.getDescent());

					// draw the part right of the cursor
					if(cPosX<str.length()-1) {
						final String rightStr=str.substring(cPosX+1);
						g.drawString(rightStr, cursorXoffset+cursorWidth, baselinePx-fm.getDescent());
					}
					//g.setColor(Color.red);
					//g.drawRect(cPosX*15, baselinePx-lineHeight, 15, lineHeight);
					//System.out.println("drawing cursor at: "+(cPosX*15)+", "+(baselinePx-lineHeight)+", 15, "+lineHeight);
				} else {
					// simply draw the line string
					g.drawString(str, 0, baselinePx-fm.getDescent());
				}

				lineNo++;
			}
		}
	}

	private void resetCTime() {
		cTime=System.currentTimeMillis();
		nextWakeup=cTime+C_BLINK_MILLIES;
	}

	public void changedEvent(LinesModelChangedEvent evt) {
		resetCTime();
		paintingArea.repaint();
	}

	public void cursorPositionChanged(CursorPositionChangedEvent evt) {
		resetCTime();
		cPosX=evt.getX();
		cPosY=evt.getY();
		paintingArea.repaint();
	}

	public void firstLineChanged(FirstLineChangedEvent evt) {
		resetCTime();
		paintingArea.repaint();
	}

	public int getLines() {
		// TODO implement
		return 0;
	}

	public int getRows() {
		// TODO implement
		return 0;
	}

}
