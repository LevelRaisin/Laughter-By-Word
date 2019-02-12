package frame;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * The <b>GameMouse</b> class is used to provide interaction with the mouse,
 * without creating unnecessary clutter in the {@link GamePanel} class.
 * <p>
 * Changes: The class now interacts with a dragged mouse as well.
 * <p>
 * Total Time Spent: 0.5 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 */
public class GameMouse implements MouseListener, MouseMotionListener {
	/**
	 * The current position of the <b>GameMouse</b>.
	 */
	public Point mousePos;

	/**
	 * The state of Button1 (left-click).
	 */
	public boolean pressed;
	/**
	 * The location of the last click.
	 */
	private Point clickLoc;

	/**
	 * Initializes all the parameters of <b>GameMouse</b>.
	 */
	public GameMouse() {
		mousePos = new Point(0, 0);
		pressed = false;
		clickLoc = null;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		mousePos = arg0.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mousePos = arg0.getPoint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		clickLoc = e.getPoint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		pressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
		if (clickLoc == null) {
			clickLoc = e.getPoint();
		}
	}

	/**
	 * This method allows a ditinguishment between the current position of the
	 * mouse, and the position of a click. To ensure this, it provides a single
	 * time access to the click, meaning that the click is set to null once it
	 * has been read.
	 * 
	 * @return The position of the last click, if it has not been read yet. If
	 *         it has not happened or has been read, returns {@code null}.
	 */
	public Point getClickLoc() {
		Point out = null;
		if (clickLoc != null)
			out = new Point(clickLoc);
		clickLoc = null;
		return out;
	}
}
