package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.PropertyHandler;
import model.GameObject;
import model.Ghost;
import model.Model;

public class BoardView extends JPanel {
	private Model m;
	private boolean isLoginScreen;
	private Image ii;
	private int data[];

	public BoardView(Model m) {
		this.m = m;
		this.isLoginScreen = true;
		this.data = PropertyHandler.getLevelData();

		addRandomFruits();
		setFocusable(true);
		setLayout(null);
	}

	private void addRandomFruits() {
		int nBlocks = PropertyHandler.getPropertyAsInt("view.nblock");
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		for (int i = 0; i < PropertyHandler.getPropertyAsInt("game.nfruits"); ++i) {
			Random rand = new Random();
			int pos[] = {0,0};
			do {
				pos[0] = rand.nextInt(nBlocks);
				pos[1] = rand.nextInt(nBlocks);
			} while ((this.data[pos[0] + nBlocks * pos[1]] & BlockElement.POINT.getValue()) == 0);
			this.setData(pos[0] + nBlocks * pos[1], this.data[pos[0] + nBlocks * pos[1]] & (BlockElement.POINT.getValue() - 1));
			this.setData(pos[0] + nBlocks * pos[1], this.data[pos[0] + nBlocks * pos[1]] | BlockElement.FRUIT.getValue());
			pos[0] *= blockSize;
			pos[1] *= blockSize;
			this.m.createFruit(pos);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		if (this.isLoginScreen) {
			drawLoginView();
			return;
		}

		removeAll();
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		int screenSize = PropertyHandler.getPropertyAsInt("view.nblock") * blockSize;
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, screenSize + 20, screenSize + 20);
		drawScore(g2d);
		drawMaze(g2d);
		drawPacman(g2d);
		drawGhost(g2d);

		g2d.drawImage(ii, 5, 5, this);
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	}

	private void drawGhost(Graphics2D g2d) {
		for (Ghost ghost : this.m.getGhosts()) {
			g2d.drawImage(ghost.getPng(), ghost.getPosition()[0] + 1, ghost.getPosition()[1] + 1, this);
		}
	}

	private void drawPacman(Graphics2D g2d) {
		g2d.drawImage(this.m.getPacman().getPng(), this.m.getPacman().getPosition()[0] + 1,
				this.m.getPacman().getPosition()[1] + 1, this);
	}

	private void drawMaze(Graphics2D g2d) {
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		int screenSize = PropertyHandler.getPropertyAsInt("view.nblock") * blockSize;

		int i = 0;
		for (int y = 0; y < screenSize; y += blockSize) {
			for (int x = 0; x < screenSize; x += blockSize) {
				g2d.setColor(new Color(5, 100, 5));
				g2d.setStroke(new BasicStroke(2));

				// Bitoperations to determine block type
				if ((data[i] & BlockElement.BORDER_LEFT.getValue()) != 0) {
					g2d.drawLine(x, y, x, y + blockSize - 1);
				}
				if ((data[i] & BlockElement.BORDER_TOP.getValue()) != 0) {
					g2d.drawLine(x, y, x + blockSize - 1, y);
				}
				if ((data[i] & BlockElement.BORDER_RIGHT.getValue()) != 0) {
					g2d.drawLine(x + blockSize - 1, y, x + blockSize - 1, y + blockSize - 1);
				}
				if ((data[i] & BlockElement.BORDER_BOTTOM.getValue()) != 0) {
					g2d.drawLine(x, y + blockSize - 1, x + blockSize - 1, y + blockSize - 1);
				}
				if ((data[i] & BlockElement.POINT.getValue()) != 0) {
					g2d.setColor(new Color(192, 192, 0));
					g2d.fillRect(x + blockSize / 2 - 1, y + blockSize / 2 - 1, 2, 2);

				}
				if ((data[i] & BlockElement.BORDER_BLOCK.getValue()) != 0) {
					g2d.fillRect(x, y, blockSize, blockSize);
				}
				
				if ((data[i] & BlockElement.FRUIT.getValue()) != 0) {
					final int xVal = x;
					final int yVal = y;
					GameObject fruit = this.m.getFruits().stream()
							.filter(f -> f.getPosition()[0] == xVal && f.getPosition()[1] == yVal)
							.findFirst().get();
					g2d.drawImage(fruit.getPng(), fruit.getPosition()[0] + 1, fruit.getPosition()[1] + 1, this);
				}
				++i;
			}
		}
	}

	private void drawScore(Graphics2D g) {
		g.setColor(new Color(96, 128, 255));
		g.setFont(new Font("Helvetica", Font.BOLD, 14));
		String s = "Score: " + this.m.calculateScore();

		int frameSizeX = PropertyHandler.getPropertyAsInt("frame.sizeX");
		int frameSizeY = PropertyHandler.getPropertyAsInt("frame.sizeY");
		g.drawString(s, (int) (frameSizeX - 100), (int) (frameSizeY * 0.90));
		g.drawString("User: " + PropertyHandler.getProperty("app.user"), 50, (int) (frameSizeY * 0.90));
		// TODO add lives here with g.drawImage
	}

	private void drawLoginView() {
		// UserData
		JLabel nameLabel = new JLabel("Player Name:");
		nameLabel.setSize(100, 20);
		nameLabel.setLocation(10, 10);

		JTextField playerName = new JTextField();
		playerName.setSize(100, 20);
		playerName.setLocation(nameLabel.getWidth() + nameLabel.getLocation().x + 10, nameLabel.getLocation().y);

		// Start Button
		JButton startGame = new JButton("Start Game");
		startGame.setSize(100, 20);
		startGame.setLocation(playerName.getLocation().x, playerName.getLocation().y + playerName.getHeight() + 10);
		startGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (playerName.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter a user Name", "Invalid playerName",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				PropertyHandler.setUserName(playerName.getText());
				isLoginScreen = false;
				repaint();
			}
		});

		add(nameLabel);
		add(playerName);
		add(startGame);
	}

	public boolean isGameActive() {
		return !this.isLoginScreen;
	}
	
	public int[] getData() {
		return data;
	}

	public void setData(int index, int data) {
		this.data[index] = data;
	}
}
