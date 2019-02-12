package frame;

import javax.swing.JFrame;

/**
 * <b>GameFrame</b> is a class used to store the {@link GamePanel} to provide
 * the user with access to the game. It does not do any processing.
 * <p>
 * Changes: The class now supports an 800 (usable) x 800 (usable) screen instead
 * of a 800 x 800 (usable) frame.
 * <p>
 * Total Time Spent: 0.5 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 * @see GamePanel
 */
@SuppressWarnings("serial")
public class GameFrame extends JFrame {
	/**
	 * This constructor initializes everything required for the window, and adds
	 * the {@link GamePanel}, effectively starting the game. The screen is set
	 * to 800x800 usable space rather than 800x800 size.
	 */
	public GameFrame() {
		super("Laughter by Word");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(new GamePanel());
		this.setResizable(false);
		this.pack();
		this.setSize(800, 800);
		this.setSize(800 + this.getPreferredSize().width - this.getContentPane().getPreferredSize().width,
				800 + this.getPreferredSize().height - this.getContentPane().getPreferredSize().height);
		this.setVisible(true);
	}

	/**
	 * Entry point of the program, which initializes the only frame, the
	 * <b>GameFrame</b>.
	 * 
	 * @param args
	 *            Unused; required by JVM.
	 */
	public static void main(String[] args) {
		new GameFrame();
	}
}
