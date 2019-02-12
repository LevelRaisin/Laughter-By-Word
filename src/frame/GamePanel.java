package frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import mapping.Tile;

/**
 * <b>GamePanel</b> accomplishes two tasks. It does the processing of the game,
 * and the rendering.
 * <p>
 * <b>GamePanel</b> uses the interaction between {@link GameMouse} and what it
 * has stored to provide the {@link GameGraphics game} experience to the user.
 * It also contains the {@link Button} class.
 * <p>
 * Changes: The class now supports all aspects of combat, victory conditions,
 * and all menu screens (plus splash art).
 * <p>
 * Total Time Spent: 10 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	/**
	 * <b>Button</b> is the recreation of a JButton made to fit the needs of
	 * {@link GamePanel}. It interacts with {@link GameMouse} to do the majority
	 * of the processing. It also stores its own label, location, and has the
	 * method to draw itself.
	 * 
	 * @author Lev Raizman
	 * @version 2.0.0.0
	 *
	 */
	private class Button {
		/**
		 * The width of the current <b>button</b>.
		 */
		public int width;

		/**
		 * The height of the current <b>button</b>.
		 */
		public int height;

		/**
		 * The label of this <b>button</b>.
		 */
		private String name;

		/**
		 * The position of the top-left corner relative to the top left corner
		 * of the JPanel.
		 */
		protected Point pos;

		/**
		 * An array consisting of the two {@link java.awt.Image images} that
		 * represent the button. The first is the off state, and the second is
		 * the on-state.
		 */
		protected Image[] button;

		/**
		 * Constructs a <b>Button</b> with all required fields.
		 * 
		 * @param name
		 *            the label of the button.
		 * @param pos
		 *            the top-left corner of the button.
		 * @param button
		 *            the two {@link java.awt.Image images} representing the
		 *            states of the button.
		 */
		public Button(String name, Point pos, Image[] button) {
			this.name = name;
			this.pos = pos;
			this.button = button;
			width = button[0].getWidth(null);
			height = button[0].getHeight(null);
		}

		/**
		 * Draws the button to whatever contains g.
		 * 
		 * @param g
		 *            the {@link java.awt.Graphics Graphics} of the container
		 *            the button needs to be drawn to.
		 */
		public void draw(Graphics g) {
			if (buttonClicked())
				g.drawImage(button[1], pos.x, pos.y, null);
			else
				g.drawImage(button[0], pos.x, pos.y, null);
			g.setFont(new Font("Baskerville Old Face", Font.PLAIN, 40));
			g.setColor(new Color(0, 0, 0));
			g.drawString(name, pos.x + (width - g.getFontMetrics().stringWidth(name)) / 2, pos.y + height / 2 + 10);
		}

		/**
		 * @return Whether the <b>button</b> is currently being pressed.
		 */
		private boolean buttonClicked() {
			return (mouse.mousePos.x >= pos.x && mouse.mousePos.x <= pos.x + width)
					&& (mouse.mousePos.y >= pos.y && mouse.mousePos.y <= pos.y + height) && mouse.pressed;
		}

		/**
		 * This checks if the <b>button</b> was pressed by comparing it to the
		 * last click location.
		 * 
		 * @param check
		 *            the position to check
		 * @return Whether the click was inside the button.
		 */
		public boolean buttonAnalysis(Point check) {
			return (check.x >= pos.x && check.x <= pos.x + width) && (check.y >= pos.y && check.y <= pos.y + height);
		}
	}

	/**
	 * <b>MuteButton</b> is a special {@link Button} that is instantiated once
	 * in {@link GamePanel}, which allows the user to turn {@link GameMusic
	 * music} on and off.
	 * 
	 * @author Lev Raizman
	 * @version 2.0.0.0
	 *
	 */
	private class MuteButton extends Button {
		/**
		 * The state of the button. On is for music being on, off is for music
		 * being off.
		 */
		private boolean on;

		/**
		 * Constructs a <b>MuteButton</b>. Needs no parameters because it's only
		 * made once.
		 */
		public MuteButton() {
			super("", new Point(750, 0), muteButton);
			on = true;
		}

		@Override
		public void draw(Graphics g) {
			if (on)
				g.drawImage(button[0], pos.x, pos.y, null);
			else
				g.drawImage(button[1], pos.x, pos.y, null);
		}

		@Override
		public boolean buttonAnalysis(Point check) {
			if (super.buttonAnalysis(check)) {
				on = !on;
				if (on) {
					if (gameState == 0 || gameState == 2 || gameState == 3 || gameState == 4)
						GameMusic.playSound(0);
					else if (gameState == 8 || gameState == 11)
						GameMusic.playSound(4);
					else
						GameMusic.playSound(game.getLevel());
				}
			}
			if (!on)
				GameMusic.endMusic();
			return false;
		}
	}

	/**
	 * Amount of help screens there are.
	 */
	private final int MAX_HELP_SCREENS = 2;

	/**
	 * Which screen the game is currently in.
	 * <p>
	 * The screens are:
	 * <p>
	 * 0 - Main Menu
	 * <p>
	 * 1 - Adventure Game
	 * <p>
	 * 2 - Help
	 * <p>
	 * 3 - High Scores
	 * <p>
	 * 4 - Exit Screen
	 * <p>
	 * 5 - Removed
	 * <p>
	 * 6 - Regular Battle
	 * <p>
	 * 7 - Boss Battle
	 * <p>
	 * 8 - Game Over
	 * <p>
	 * 9 - Regular Message Screen
	 * <p>
	 * 10 - End Game Message Screen
	 * <p>
	 * 11 - Won Game
	 * <p>
	 * 12 - Exit
	 */
	private int gameState;

	/**
	 * Required to track position on help screens.
	 */
	private int screenState;

	/**
	 * All the backgrounds used throughout the game.
	 */
	private Image[] backgrounds;

	/**
	 * The images for the two states of the menu buttons, clicked and unclicked.
	 */
	private Image[] menuButton;

	/**
	 * The images for the two states of the four arrow buttons in the game
	 * screen.
	 */
	private Image[][] arrowButton;

	/**
	 * The images for the two states of the buttons available in combat.
	 */
	private Image[] battleButton;

	/**
	 * The images for the help screens.
	 */
	private Image[] help;

	/**
	 * The images for the two states of the mute button.
	 */
	private Image[] muteButton;

	/**
	 * The images for the two states of the buttons for exiting the game to main
	 * menu.
	 */
	private Image[] backButton;

	/**
	 * Provides interaction between the panel and the mouse.
	 */
	private GameMouse mouse;
	/**
	 * Allows the screen to be refreshed every set amount of time.
	 */
	private Timer gameTimer;

	/**
	 * Restricts the length of the battle.
	 */
	private Timer battleTimer;

	/**
	 * A 2D array of all the buttons. The first dimension is the screen as
	 * specified by gameState, the second is the button number.
	 */
	private Button[][] buttons;

	/**
	 * All the high scores at the start of the program.
	 */
	private int[] highScores;

	/**
	 * Does processing of everything related to interaction between the
	 * character and map.
	 */
	private GameGraphics game;

	/**
	 * The message to be displayed by the message screen.
	 */
	private String message;

	/**
	 * The character saying the message.
	 */
	private int messageCharacter;

	/**
	 * The characters for saying messages.
	 */
	private Image[] characters;

	/**
	 * The mute button for the game
	 */
	private MuteButton mute;

	/**
	 * Initializes everything required for the game, and the JPanel. Refer to
	 * variables for more information.
	 */
	public GamePanel() {
		super.setOpaque(false);
		gameState = -1;

		backgrounds = new Image[12];
		menuButton = new Image[2];
		arrowButton = new Image[4][2];
		battleButton = new Image[2];
		characters = new Image[2];
		muteButton = new Image[2];
		backButton = new Image[2];
		help = new Image[MAX_HELP_SCREENS];
		try {
			backgrounds[0] = ImageIO.read(new File("resources/graphics/backgrounds/menu.png"));
			backgrounds[1] = ImageIO.read(new File("resources/graphics/backgrounds/temple.png"));
			backgrounds[2] = ImageIO.read(new File("resources/graphics/backgrounds/forest.png"));
			backgrounds[3] = ImageIO.read(new File("resources/graphics/backgrounds/cave.png"));
			backgrounds[4] = ImageIO.read(new File("resources/graphics/backgrounds/supportbackground.png"));
			backgrounds[5] = ImageIO.read(new File("resources/graphics/backgrounds/letterbackground.png"));
			backgrounds[6] = ImageIO.read(new File("resources/graphics/backgrounds/inventory.png"));
			backgrounds[7] = ImageIO.read(new File("resources/graphics/backgrounds/loadscreen.png"));
			backgrounds[8] = ImageIO.read(new File("resources/graphics/backgrounds/smallinventory.png"));
			backgrounds[9] = ImageIO.read(new File("resources/graphics/backgrounds/lostgame.png"));
			backgrounds[10] = ImageIO.read(new File("resources/graphics/backgrounds/wongame.png"));
			backgrounds[11] = ImageIO.read(new File("resources/graphics/backgrounds/splashscreen.png"));

			menuButton[0] = ImageIO.read(new File("resources/graphics/button/offstate.png"));
			menuButton[1] = ImageIO.read(new File("resources/graphics/button/onstate.png"));

			arrowButton[0][0] = ImageIO.read(new File("resources/graphics/button/upoffstate.png"));
			arrowButton[0][1] = ImageIO.read(new File("resources/graphics/button/uponstate.png"));

			arrowButton[1][0] = ImageIO.read(new File("resources/graphics/button/downoffstate.png"));
			arrowButton[1][1] = ImageIO.read(new File("resources/graphics/button/downonstate.png"));

			arrowButton[2][0] = ImageIO.read(new File("resources/graphics/button/leftoffstate.png"));
			arrowButton[2][1] = ImageIO.read(new File("resources/graphics/button/leftonstate.png"));

			arrowButton[3][0] = ImageIO.read(new File("resources/graphics/button/rightoffstate.png"));
			arrowButton[3][1] = ImageIO.read(new File("resources/graphics/button/rightonstate.png"));

			battleButton[0] = ImageIO.read(new File("resources/graphics/button/battleoffstate.png"));
			battleButton[1] = ImageIO.read(new File("resources/graphics/button/battleonstate.png"));

			characters[0] = ImageIO.read(new File("resources/graphics/misc/mainChar.png"));
			characters[1] = ImageIO.read(new File("resources/graphics/misc/statueChar.png"));

			muteButton[0] = ImageIO.read(new File("resources/graphics/button/muteon.png"));
			muteButton[1] = ImageIO.read(new File("resources/graphics/button/muteoff.png"));

			backButton[0] = ImageIO.read(new File("resources/graphics/button/backoffstate.png"));
			backButton[1] = ImageIO.read(new File("resources/graphics/button/backonstate.png"));

			help[0] = ImageIO.read(new File("resources/graphics/misc/help1.png"));
			help[1] = ImageIO.read(new File("resources/graphics/misc/help0.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		buttons = new Button[][] {
				{ new Button("PLAY", new Point(200, 150), menuButton),
						new Button("HELP", new Point(200, 300), menuButton),
						new Button("HIGH SCORES", new Point(200, 450), menuButton),
						new Button("EXIT", new Point(200, 600), menuButton) },
				{ new Button("", new Point(100, 575), arrowButton[0]),
						new Button("", new Point(100, 725), arrowButton[1]),
						new Button("", new Point(25, 650), arrowButton[2]),
						new Button("", new Point(175, 650), arrowButton[3]),
						new Button("", new Point(750, 50), backButton) },
				{ new Button("Previous", new Point(0, 650), menuButton),
						new Button("Next", new Point(400, 650), menuButton) },
				{ new Button("Back", new Point(200, 600), menuButton) }, {},
				{ new Button("SAVE FILE 1", new Point(200, 150), menuButton),
						new Button("SAVE FILE 2", new Point(200, 300), menuButton),
						new Button("SAVE FILE 3", new Point(200, 450), menuButton),
						new Button("Back", new Point(200, 600), menuButton) },
				{ new Button("Enter", new Point(100, 627), battleButton),
						new Button("Clear", new Point(500, 627), battleButton),
						new Button("", new Point(750, 50), backButton) },
				{ new Button("Enter", new Point(100, 627), battleButton),
						new Button("Clear", new Point(500, 627), battleButton),
						new Button("", new Point(750, 50), backButton) },
				{}, {}, {}, {} };

		highScores = loadScores();

		mouse = new GameMouse();

		addMouseListener(mouse);
		addMouseMotionListener(mouse);

		int delay = 50; // milliseconds
		ActionListener taskPerformer1 = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				repaint();
			}
		};
		gameTimer = new Timer(delay, taskPerformer1);

		ActionListener taskPerformer2 = new ActionListener() {
			private int repeats = 0;

			@Override
			public void actionPerformed(ActionEvent evt) {
				if (repeats / 20 >= game.getBattleLength()) {
					battleTimer.stop();
					if (game.endBattle()) {
						if (Math.random() < 0.5) {
							game.awardItem();
						} else {
							game.awardKey();
						}
						repeats = 0;
						gameState = 1;
					} else {
						repeats = 0;
						GameMusic.endMusic();
						GameMusic.playSound(4);
						gameState = 8;
					}
				} else {
					Graphics g = getGraphics();
					g.setColor(new Color(255, 0, 0));
					g.fillRect(0, 550, (int) (800.0 / (game.getBattleLength() * 20) * repeats), 10);
				}

				repeats++;
			}
		};
		mute = new MuteButton();

		battleTimer = new Timer(50, taskPerformer2);

		gameTimer.start();

		GameMusic.playSound(0);
	}

	@Override
	public void paintComponent(Graphics g) {
		analyzeClick();
		switch (gameState) {
		case -1:
			renderSplashScreen(g);
			break;
		case 0:
			renderMenu(g);
			break;
		case 2:
			renderHelp(g);
			break;
		case 3:
			renderHighScores(g);
			break;
		case 4:
			renderExit(g);
			break;
		case 1:
			try {
				renderMap(g);
			} catch (NullPointerException npe) {
				npe.printStackTrace();
				System.exit(0);
			}
			break;
		case 6:
		case 7:
			renderBattle(g);
			break;
		case 8:
			renderGameOver(g);
			break;
		case 10:
		case 9:
			renderMap(g);
			renderMessage(g);
			break;
		case 11:
			renderGameWon(g);
			break;
		case 12:
			saveHighScores();
			System.exit(0);
			break;
		}
		renderButtons(g);
	}

	private void renderSplashScreen(Graphics g) {
		g.drawImage(backgrounds[11], 0, 0, null);
	}

	/**
	 * Renders the {@link mapping.TileMap map} during the game.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderMap(Graphics g) {
		renderItems(g);
		renderInventory(g);
		renderSmallInventory(g);
		g.drawImage(game.getMap(), this.getWidth() - Tile.TILE_SIZE * 11, 0, null);
	}

	/**
	 * Renders the {@link items.ItemInventory inventory} background and the
	 * {@link items.Item items} stored in it.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderInventory(Graphics g) {
		g.drawImage(backgrounds[6], 250, 550, null);
		g.drawImage(game.getItemInventory(), 250, 550, null);
	}

	/**
	 * Renders the {@link items.ItemInventory inventory} for the
	 * {@link items.GemInventory.Gem gems} and Spells.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderSmallInventory(Graphics g) {
		g.drawImage(backgrounds[8], 125, 245, null);
		g.drawImage(backgrounds[8], 125, 390, null);
		g.drawImage(game.getGemInventory(), 125, 245, null);
		g.drawImage(game.getSpellInventory(), 125, 390, null);
	}

	/**
	 * Renders a battle during the game.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderBattle(Graphics g) {
		int level = game.getLevel();
		renderItems(g);
		renderLetters(g);
		renderSmallInventory(g);
		g.drawImage(backgrounds[level], this.getWidth() - Tile.TILE_SIZE * 11, 0, null);
		g.drawImage(game.getEnemy(), 250, 0, null);
	}

	/**
	 * Renders the letters during a battle.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderLetters(Graphics g) {
		g.drawImage(backgrounds[5], 0, 550, null);
		g.setFont(new Font("Algerian", Font.PLAIN, 40));
		g.setColor(new Color(0, 0, 0));
		for (int i = 0; i < 26; i++) {
			g.drawString(String.valueOf((char) (i + 'A')), i % 13 * 60 + 25, i / 13 * 60 + 550 + 175);
		}
		String curWord = game.getCurWord();
		for (int i = 0; i < curWord.length(); i++) {
			g.drawString(curWord.substring(i, i + 1), (13 - curWord.length()) / 2 * 60 + 25 + i * 60, 550 + 60);
		}
		g.setFont(new Font("Algerian", Font.PLAIN, 12));
		for (int i = 0; i < 26; i++) {
			g.drawString(String.valueOf(game.getAvailable((char) (i + 'A'))), i % 13 * 60 + 54,
					i / 13 * 60 + 550 + 141);
		}
	}

	/**
	 * Renders a message said by a {@link characters.MainCharacter character}.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderMessage(Graphics g) {
		if (message.contains("You win!")) {
			if (message.indexOf('!') != message.length() - 1) {
				int points = Integer.parseInt(message.substring(message.indexOf('!') + 1));
				for (int i = 0; i < 10; i++) {
					if (points > highScores[i]) {
						for (int j = 10; j > i; j--) {
							highScores[j] = highScores[j - 1];
						}
						highScores[i] = points;
						message = "You win!";
						break;
					}
				}
			}
			g.setColor(new Color(200, 200, 200));
			g.fillRect(0, 550, 250, 250);
			g.setColor(new Color(70, 70, 70));
			g.fillRect(250, 550, 550, 250);
			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
			g.drawString("You fool! By bringing back the letters you have unleashed my powers!", 260, 600);
			g.drawString("Try putting the message together! Yes, it says \"Laughter By Word\"!", 260, 625);
			g.drawString("But let me give you the secret, missing letter, \"S\".", 260, 650);
			g.drawString("The message becomes \"Slaughter by Sword\"!", 260, 675);
			g.drawString("This whole quest was a bad pun!", 260, 700);
			g.drawString("Get Slaughtered by my Sword!", 260, 725);
			g.drawImage(characters[1], 0, 550, null);
			gameState = 10;
		} else {
			g.setColor(new Color(200, 200, 200));
			g.fillRect(0, 550, 250, 250);
			g.setColor(new Color(70, 70, 70));
			g.fillRect(250, 550, 550, 250);
			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
			g.drawString(message, 260, 600);
			g.drawImage(characters[messageCharacter], 0, 550, null);
		}
	}

	/**
	 * Renders the loading screen when {@link GameGraphics Graphics} are being
	 * set-up.
	 */
	private void renderLoadScreen() {
		Graphics g = this.getGraphics();
		g.drawImage(backgrounds[7], 0, 0, null);
		repaint();
	}

	/**
	 * Renders the game-over screen.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderGameOver(Graphics g) {
		g.drawImage(backgrounds[9], 0, 0, null);
	}

	/**
	 * Renders the game-won screen.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderGameWon(Graphics g) {
		g.drawImage(backgrounds[10], 0, 0, null);
	}

	/**
	 * Renders the layout for what surrounds the map/battle screen.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderItems(Graphics g) {
		g.drawImage(backgrounds[4], 0, 0, null);
		g.drawImage(game.getItemSetImage(), 0, 0, null);
	}

	/**
	 * Renders the Main Menu.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderMenu(Graphics g) {
		screenState = 0;
		g.drawImage(backgrounds[0], 0, 0, null);
	}

	/**
	 * Renders the Help Menu.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderHelp(Graphics g) {
		g.drawImage(backgrounds[0], 0, 0, null);
		g.drawImage(help[screenState], 100, 200, null);
	}

	/**
	 * Renders the High Scores.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderHighScores(Graphics g) {
		g.drawImage(backgrounds[0], 0, 0, null);
		g.setFont(new Font("Castellar", Font.PLAIN, 40));
		g.setColor(new Color(255, 255, 255));
		for (int i = 0; i < 10; i++) {
			g.drawString((i + 1) + ". " + highScores[i],
					(800 - g.getFontMetrics().stringWidth((i + 1) + ". " + highScores[i])) / 2, 110 + 50 * i);
		}
	}

	/**
	 * Renders the exit screen.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderExit(Graphics g) {
		g.drawImage(backgrounds[0], 0, 0, null);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Calibri", Font.PLAIN, 20));
		g.drawString("Thanks to the following sources for insipiration with visuals, audio, and gameplay: ", 10, 150);
		g.drawString("https://retrogamecrunch.com/journal/62/the-art-of-shuten-complete ", 10, 170);
		g.drawString("https://opengameart.org/content/pixel-characters-tiles-ai", 10, 190);
		g.drawString("http://community.playstarbound.com/resources/ningen-race-mod.191/update?update=7955", 10, 210);
		g.drawString("http://www.petsionary.com/wp-content/uploads/ca/cats-laughing-cat-funny-cats-hd.jpg", 10, 230);
		g.drawString("http://gakusangi.deviantart.com/art/The-Legend-of-Zelda-Pixel-Shields-296579820", 10, 250);
		g.drawString("http://pixeljoint.com/pixelart/60572.htm", 10, 270);
		g.drawString("https://www.pinterest.com/pin/347762402451756871/", 10, 290);
		g.drawString("https://www.gamedevmarket.net/asset/pixel-forest-parallax-bg-6982/", 10, 310);
		g.drawString("http://polytopia.wikia.com/wiki/The_Battle_of_Polytopia_Wikia", 10, 330);
		g.drawString("http://www-01.sil.org/linguistics/wordlists/english/wordlist/wordsEn.txt", 10, 350);
		g.setFont(new Font("Calibri", Font.PLAIN, 30));
		g.drawString("Thanks to the following people who helped with testing: ", 10, 480);
		g.drawString("Agelina Lam", 10, 520);
		g.drawString("Ruven Raizman", 10, 560);
		g.drawString("Michael Raizman", 10, 600);
		g.drawString("Vedansh Kaushik", 10, 640);
		g.drawString("Created by: Daniel Zybine & Lev Raizman", 10, 720);
		g.drawString("Click anywhere on the screen to exit...",
				(800 - g.getFontMetrics().stringWidth("Click anywhere on the screen to exit...")) / 2, 770);
	}

	/**
	 * This method is called before every rendering of the screen. It processes
	 * any changes that need to screens based on mouse input.
	 */
	private void analyzeClick() {
		Point temp = mouse.getClickLoc();
		if (temp == null) {
			if (gameState == 1) {
				if (mouse.mousePos.x >= 250 && mouse.mousePos.x <= 800
						&& ((mouse.mousePos.y >= 550 && mouse.mousePos.y <= 650)
								|| (mouse.mousePos.y >= 700 && mouse.mousePos.y <= 800))) {
					int pos = 11 * ((mouse.mousePos.y - 550) / 125) + (mouse.mousePos.x - 250) / 50;
					this.getGraphics().drawImage(game.getItemDescription(pos), mouse.mousePos.x - 250,
							mouse.mousePos.y - 100, null);
				} else if (mouse.mousePos.x >= 125 && mouse.mousePos.x <= 245 && mouse.mousePos.y >= 245
						&& mouse.mousePos.y <= 365) {
					int pos = 3 * ((mouse.mousePos.y - 245) / 40) + (mouse.mousePos.x - 125) / 40;
					this.getGraphics().drawImage(game.getGemDescription(pos), mouse.mousePos.x - 40,
							mouse.mousePos.y - 40, null);
				}
			}
			return;
		}
		switch (gameState) {
		case -1:
			gameState = 0;
			break;
		case 0:
			if (buttons[0][0].buttonAnalysis(temp)) {
				renderLoadScreen();
				loadGame();
			}
			for (int i = 1; i < 4; i++) {
				if (buttons[0][i].buttonAnalysis(temp)) {
					gameState = i + 1;
					break;
				}
			}
			break;
		case 2:
			if (buttons[2][0].buttonAnalysis(temp))
				if (screenState == 0)
					gameState = 0;
				else
					screenState--;
			else if (buttons[2][1].buttonAnalysis(temp))
				if (screenState + 1 == MAX_HELP_SCREENS)
					gameState = 0;
				else
					screenState++;
			break;
		case 3:
			if (buttons[3][0].buttonAnalysis(temp))
				gameState = 0;
			break;
		case 4:
			gameState = 12;
			return;
		case 1:
			Point dir = null;
			if (buttons[1][0].buttonAnalysis(temp)) { // UP
				dir = (new Point(0, -1));
			} else if (buttons[1][1].buttonAnalysis(temp)) {// DOWN
				dir = (new Point(0, 1));
			} else if (buttons[1][2].buttonAnalysis(temp)) { // LEFT
				dir = (new Point(-1, 0));
			} else if (buttons[1][3].buttonAnalysis(temp)) {// RIGHT
				dir = (new Point(1, 0));
			} else if (buttons[1][4].buttonAnalysis(temp)) {// RIGHT
				GameMusic.endMusic();
				GameMusic.playSound(4);
				gameState = 8;
			} else if (temp.x >= 250 && temp.x <= 800
					&& ((temp.y >= 550 && temp.y <= 650) || (temp.y >= 700 && temp.y <= 800))) {
				int pos = 11 * ((temp.y - 550) / 125) + (temp.x - 250) / 50;
				game.switchItem(pos);
			} else if (temp.x >= 27 && temp.x <= 77 && temp.y >= 95 && temp.y <= 195) {
				game.unequipItem(true);
			} else if (temp.x >= 133 && temp.x <= 183 && temp.y >= 95 && temp.y <= 195) {
				game.unequipItem(false);
			} else if (temp.x >= 125 && temp.x <= 245 && temp.y >= 245 && temp.y <= 365) {
				int pos = 3 * ((temp.y - 245) / 40) + (temp.x - 125) / 40;
				game.setGem(pos);
			} else if (temp.x >= 125 && temp.x <= 245 && temp.y >= 395 && temp.y <= 515) {
				int pos = 3 * ((temp.y - 395) / 40) + (temp.x - 125) / 40;
				game.swapSpells(pos);
			}

			message = game.move(dir);

			if (message != null)
				if (message.equals("")) {
					battleTimer.start();
					game.initiateBattle();
					gameState = 6;
				} else if (message.equals("I have encountered a monster unlike any I've seen before!")) {
					battleTimer.start();
					game.initiateBossBattle();
					gameState = 7;
				} else if (message.contains("BRING")) {
					gameState = 9;
					messageCharacter = 1;
				} else {
					gameState = 9;
					messageCharacter = 0;
				}
			break;
		case 6:
		case 7:
			if (temp.y >= 680) {
				int xTile = (temp.x - 10) / 60;
				int yTile = (temp.y - 680) / 60;
				if (xTile >= 0 && xTile <= 13 && (yTile == 0 || yTile == 1)) {
					char clicked = (char) ('A' + 13 * yTile + xTile);
					if (clicked >= 'A' && clicked <= 'Z') {
						game.incrementCurWord(clicked);
					}
				}
			} else if (buttons[6][0].buttonAnalysis(temp)) {
				game.finalizeCurWord();
			} else if (buttons[6][1].buttonAnalysis(temp)) {
				game.clearCurWord();
			} else if (buttons[6][2].buttonAnalysis(temp)) {
				GameMusic.endMusic();
				GameMusic.playSound(4);
				gameState = 8;
			} else if (temp.x >= 125 && temp.x <= 245 && temp.y >= 395 && temp.y <= 515) {
				int pos = 3 * ((temp.y - 395) / 40) + (temp.x - 125) / 40;
				game.useSpell(pos);
			}
			break;
		case 11:
		case 8:
			gameState = 0;
			GameMusic.endMusic();
			GameMusic.playSound(0);
			break;
		case 9:
			gameState = 1;
			break;
		case 10:
			gameState = 11;
			GameMusic.endMusic();
			GameMusic.playSound(4);
			break;
		}
		if (!(gameState == 6 || gameState == 7)) {
			battleTimer.stop();
		}
		mute.buttonAnalysis(temp);
	}

	/**
	 * Renders all {@link Button buttons} required.
	 * 
	 * @param g
	 *            the {@link java.awt.Graphics Graphics} of the container that
	 *            needs rendering.
	 */
	private void renderButtons(Graphics g) {
		if (gameState < 0)
			return;
		for (Button b : buttons[gameState]) {
			b.draw(g);
		}
		mute.draw(g);
	}

	/**
	 * Loads the game.
	 */
	private void loadGame() {
		gameState = 1;
		game = new GameGraphics();
		GameMusic.endMusic();
		GameMusic.playSound(game.getLevel());
	}

	/**
	 * Used to load high scores into the high score array.
	 * 
	 * @return The contents of the high score file.
	 */
	private int[] loadScores() {
		File scores = new File(System.getProperty("user.home") + "/Laughter by Word/high scores.txt");
		int[] out = new int[10];

		if (scores.exists()) {
			Scanner in;
			try {
				in = new Scanner(scores);
				for (int i = 0; i < 10; i++) {
					out[i] = in.nextInt();
				}
				in.close();
			} catch (FileNotFoundException e) {
			}
		} else {
			new File(scores.getParent()).mkdirs();
			try {
				PrintWriter writer = new PrintWriter(scores);
				for (int i = 0; i < 10; i++) {
					writer.print(0 + " ");
					out[i] = 0;
				}
				writer.close();
			} catch (IOException e) {
				// do something
			}
		}

		return out;
	}

	/**
	 * Saves the high scores to the high score file.
	 */
	private void saveHighScores() {
		try {
			PrintWriter writer = new PrintWriter(
					new File(System.getProperty("user.home") + "/Laughter by Word/high scores.txt"));
			for (int i = 0; i < 10; i++) {
				writer.print(highScores[i] + " ");
			}
			writer.close();
		} catch (IOException e) {
		}
	}
}
