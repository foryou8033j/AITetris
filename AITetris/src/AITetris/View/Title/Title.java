package AITetris.View.Title;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import AITetris.Util.String.StringPadding;

/**
 * 타이틀 메뉴를 그려주는 패널 클래스
 * 
 * @author Jeongsam
 *
 */
public class Title extends JPanel implements ActionListener, KeyListener{

	private Container rootPanel;

	int curPoint = 0;
	int minPoint = 0;
	int maxPoint = 4;

	private Timer timer;

	public Title(Container rootPanel) {
		this.rootPanel = rootPanel;

		setFocusable(true);
		
		timer = new Timer(10, this);
		timer.start();
		/*
		 * PlayerMode playMode = PlayerMode.Duo;
		 * 
		 * GameBoard board = new GameBoard(playMode, Player.Player1, 20, 20, 400, 600);
		 * GameBoard board2 = new GameBoard(playMode, Player.Player2, 440, 20, 400,
		 * 600);
		 * 
		 * KeyHandler keyHandler = new KeyHandler(playMode, board, board2);
		 * 
		 * board.addKeyListener(keyHandler); board2.addKeyListener(keyHandler);
		 * 
		 * switch (playMode) { case Single: getContentPane().add(board); break;
		 * 
		 * case Duo: getContentPane().add(board); getContentPane().add(board2); break; }
		 */

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		drawMenues(g);

	}

	private void drawMenues(Graphics g) {

		drawStringCenterOfPanel(g, Color.BLACK, 64, "T E T R I S", 120);
		drawStringCenterOfPanel(g, Color.BLACK, 18, "소프트웨어 분석 및 설계 - 레벨3", 160);
		drawStringCenterOfPanel(g, Color.BLACK, 16, "20140636 서정삼", 190);

		Color color = Color.BLACK;

		color = (curPoint == 0 ? Color.RED : Color.BLACK);
		drawStringCenterOfPanel(g, color, 16, StringPadding.getCPad("Single", 14, " "), 360);

		color = (curPoint == 1 ? Color.RED : Color.BLACK);
		drawStringCenterOfPanel(g, color, 16, StringPadding.getRPad("Human VS Human", 15, " "), 400);

		color = (curPoint == 2 ? Color.RED : Color.BLACK);
		drawStringCenterOfPanel(g, color, 16, StringPadding.getRPad("Human VS Neo", 15, " "), 440);

		color = (curPoint == 3 ? Color.RED : Color.BLACK);
		drawStringCenterOfPanel(g, color, 16, StringPadding.getCPad("Help", 14, " "), 480);

		color = (curPoint == 4 ? Color.RED : Color.BLACK);
		drawStringCenterOfPanel(g, color, 16, StringPadding.getCPad("Exit", 14, " "), 520);

	}

	/**
	 * 문자열을 가로 기준 중앙에 그려준다.
	 * 
	 * @param g
	 *            그래픽스 모델
	 * @param color
	 *            색상
	 * @param size
	 *            폰트 사이즈
	 * @param str
	 *            문자열
	 * @param height
	 *            출력 높이
	 */
	private void drawStringCenterOfPanel(Graphics g, Color color, int size, String str, int height) {

		Font small = new Font("Helvetica", Font.BOLD, size);
		FontMetrics metr = getFontMetrics(small);

		g.setColor(color);
		g.setFont(small);

		g.drawString(str, (int) getSize().getWidth() / 2 - metr.stringWidth(str) / 2, height);

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			if (curPoint - 1 >= minPoint)
				curPoint--;
			break;

		case KeyEvent.VK_DOWN:
			if (curPoint + 1 <= maxPoint)
				curPoint++;
			break;

		case KeyEvent.VK_ENTER:

			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
