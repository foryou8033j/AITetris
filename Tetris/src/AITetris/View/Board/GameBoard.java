package AITetris.View.Board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.sun.java.swing.SwingUtilities3;

import AITetris.View.Player;
import AITetris.View.PlayerMode;
import AITetris.View.Board.Tetrimino.Shape;
import AITetris.View.Board.Tetrimino.Tetrominoes;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;

/**
 * 테트리스의 게임 보드를 보여주는 패널
 * 
 * @author Jeongsam
 *
 **/
public class GameBoard extends JPanel implements ActionListener {

	public final int BoardWidth = 12;
	public final int BoardHeight = 24;

	private Timer timer;

	public boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	boolean isGhost = false;
	boolean isOver = false;
	boolean isWin = false;
	boolean isDraw = false;
	boolean isCompetition = false;
	boolean isCrazyKeyboard = false;
	boolean isAttack = false;

	private int numLinesRemoved = 0;
	private int numTetrominoDropCount = 0;
	private int numCountGhostUse = 0;
	private int attackType = 0;
	private String attackMessage = "";

	public int curX = 0;
	public int curY = 0;
	public int ghostCurX = 0;
	public int ghostCurY = 0;

	public int point = 0;
	public int ghostUsed = 0;

	public Shape curPiece;
	Shape nextPiece;
	Shape ghostPiece;
	Shape tempPiece;

	private Tetrominoes[] board;

	private InfoBoard infoBoard;

	long curTime;
	long defTime;
	long pauseTime;
	
	private String name = "NULL";

	PlayerMode playMode;
	Player player;

	public GameBoard(boolean isCompetition, PlayerMode playMode, Player player, int x, int y, int width, int height) {

		this.playMode = playMode;
		this.player = player;
		this.isCompetition = isCompetition;

		setLayout(null);
		setBounds(x, y, width, height);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		setFocusable(true);

		board = new Tetrominoes[BoardWidth * BoardHeight];

		curPiece = new Shape();
		nextPiece = new Shape();
		ghostPiece = new Shape();
		tempPiece = new Shape();

		infoBoard = new InfoBoard(this);
		infoBoard.setBounds(width - 100, 0, 100, height);
		add(infoBoard);

		timer = new Timer(400, this);
		// timer.start();
		
		clearBoard();
		
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void actionPerformed(ActionEvent e) {

		curTime = System.currentTimeMillis();
		infoBoard.leftTime = (defTime - curTime);

		if ((float) (defTime - curTime) / 1000 < 0) {
			curPiece.setShape(Tetrominoes.NoShape);
			isStarted = false;
			isOver = true;
			timer.stop();
		}

		// 난이도 조절부
		if ((float) (defTime - curTime) / 1000 < 300)
			timer.setDelay(300);
		if ((float) (defTime - curTime) / 1000 < 180)
			timer.setDelay(200);

		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			oneLineDown();
		}

		repaint();
		infoBoard.repaint();
	}

	// 블럭의 단일 가로 크기 반환
	private int squareWidth() {
		return (int) (getSize().getWidth() - 100) / BoardWidth;
	}

	// 블럭의 단일 세로 크기 반환
	private int squareHeight() {
		return (int) getSize().getHeight() / BoardHeight;
	}

	public Tetrominoes shapeAt(int x, int y) {
		return board[(y * BoardWidth) + x];
	}

	public void start() {
		if (isPaused || isStarted)
			return;

		else {

			curTime = System.currentTimeMillis();
			defTime = System.currentTimeMillis() + 600000;

			isStarted = true;
			isOver = false;
			isFallingFinished = false;
			isGhost = false;
			isWin = false;
			isAttack = false;
			isCrazyKeyboard = false;
			isDraw = false;
			numCountGhostUse = 0;
			numLinesRemoved = 0;
			numTetrominoDropCount = 0;
			point = 0;
			ghostUsed = 0;

			clearBoard();

			tempPiece.setShape(Tetrominoes.NoShape);
			nextPiece.setRandomShape();

			timer.setDelay(400);
			timer.start();

			newPiece();
		}

	}

	public void pause() {
		if (!isStarted)
			return;

		isPaused = !isPaused;

		if (isPaused) {
			pauseTime = System.currentTimeMillis();
			timer.stop();
		} else {
			timer.start();
			defTime += (System.currentTimeMillis() - pauseTime);
		}

		repaint();
	}

	public void exchangePiece() {

		if (!isStarted || isPaused)
			return;

		if (tempPiece.getShape() == Tetrominoes.NoShape) {
			tempPiece.setShape(curPiece.getShape());
			newPiece();
		} else {
			if (tryExchange(tempPiece)) {
				Shape tempShape = new Shape();
				tempShape.setShape(tempPiece.getShape());
				tempPiece.setShape(curPiece.getShape());
				curPiece.setShape(tempShape.getShape());
			}

		}

		infoBoard.repaint();

	}

	public boolean tryRotate() {

		if(!tryMove(curPiece.rotateRight(), curX, curY)) {
			
			if(curX < BoardWidth/2) {
				while(!tryMove(curPiece.rotateRight(), curX++, curY));
			}else {
				while(!tryMove(curPiece.rotateRight(), curX--, curY));
			}
			
		}else {
			if (isGhost)
				tryGhostPieceMove(ghostPiece.rotateRight(), ghostCurX, ghostCurY);
		}
		return true;
	}

	private boolean tryExchange(Shape newPiece) {

		for (int i = 0; i < 4; ++i) {
			int x = curX + newPiece.x(i);
			int y = curY + newPiece.y(i);
			if (x < 0) {
				curX += newPiece.x(i) * -1;
				curX += 1;
			}
			if (x >= BoardWidth) {
				curX -= newPiece.x(i);
				curX -= 1;
			}
			if (y < 0 || y >= BoardHeight)
				return false;
			if (shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}

		return true;
	}

	public void reset() {
		start();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

		// 보드에 저장된 블럭을 그린다
		for (int i = 0; i < BoardHeight; ++i) {
			for (int j = 0; j < BoardWidth; ++j) {
				Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
				if (shape != Tetrominoes.NoShape)
					drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
			}
		}

		if (isGhost)
			drawGhostPiece(g);

		// 현재 블럭을 그린다
		if (curPiece.getShape() != Tetrominoes.NoShape) {
			for (int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = curY - curPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
						curPiece.getShape());
			}
		}

		// UI Draw 관련

		if (!isStarted && !isOver)
			drawHelpScreen(g);

		if (isOver)
			drawGameOverScreen(g);

		if (isPaused && isStarted)
			drawPauseScreen(g);

		// 3개의 Tetromino 가 Drop 하기 전에는 HelpScreen 을 그려준다
		if (numTetrominoDropCount < 3 && !isPaused && !isOver)
			drawHelpScreen(g);

		if (player.equals(Player.Neo) && !isOver)
			drawStringCenterOfPanel(g, Color.GRAY, 16, "AI. NEO 가 플레이합니다", 260);

		drawMessageFollowingAttack(g);

		// Limit Line Draw
		g.setColor(Color.RED);
		g.drawLine(0, BoardHeight, (int) getSize().getWidth() - 100, BoardHeight);

		infoBoard.repaint();

	}

	/**
	 * 게임 종료 화면을 그려준다
	 * 
	 * @param g
	 */
	private void drawGameOverScreen(Graphics g) {

		Font small = new Font("Helvetica", Font.BOLD, 20);
		FontMetrics metr = getFontMetrics(small);

		int line = 150;

		if (playMode.equals(PlayerMode.Duo) || playMode.equals(PlayerMode.AI)) {
			if(isDraw && !isWin) {
			
				drawStringCenterOfPanel(g, Color.RED, 36, "DRAW!", line - metr.getHeight() * 2);
				
			}
			if (isWin && !isDraw) {
				if (player.equals(Player.Neo))
					drawStringCenterOfPanel(g, Color.RED, 36, "NEO WIN!", line - metr.getHeight() * 2);
				else
					drawStringCenterOfPanel(g, Color.RED, 36, "YOU WIN!", line - metr.getHeight() * 2);
			}
			else if(!isWin && !isDraw){

				if (player.equals(Player.Neo))
					drawStringCenterOfPanel(g, Color.RED, 36, "NEO LOSE", line - metr.getHeight() * 2);
				else
					drawStringCenterOfPanel(g, Color.RED, 36, "YOU LOSE", line - metr.getHeight() * 2);
			}

		}

		if ((float) (defTime - curTime) / 1000 < 0)
			drawStringCenterOfPanel(g, Color.RED, 24, "TIME OVER", line += metr.getHeight());
		else
			drawStringCenterOfPanel(g, Color.RED, 24, "GAME OVER", line);

		drawStringCenterOfPanel(g, Color.BLACK, 16, "Press 'ENTER' To Restart", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "Press 'Q' Back To Title", line += metr.getHeight());

		line += 80;

		drawStringCenterOfPanel(g, Color.BLACK, 16, "Points", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, String.valueOf(point), line += metr.getHeight());

		line += 20;

		drawStringCenterOfPanel(g, Color.BLACK, 16, "Penalty", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.RED, 16, String.valueOf(ghostUsed), line += metr.getHeight());

		line += 20;

		drawStringCenterOfPanel(g, Color.BLACK, 16, "Point Result", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLUE, 16, String.valueOf(point - ghostUsed), line += metr.getHeight());

		quit();

	}

	private void quit() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	}

	/**
	 * 도움말 화면을 그려준다
	 * 
	 * @param g
	 */
	private void drawHelpScreen(Graphics g) {

		Font small = new Font("Helvetica", Font.BOLD, 20);
		FontMetrics metr = getFontMetrics(small);

		int line = 260;
		Color color = Color.GRAY;

		if (!isStarted) {

			if (player.equals(player.Neo)) {
				drawStringCenterOfPanel(g, Color.RED, 24, "WAIT FOR", 100 + metr.getHeight());
				drawStringCenterOfPanel(g, Color.RED, 24, "START ORDER", 100 + metr.getHeight() * 2);
			} else {
				drawStringCenterOfPanel(g, Color.RED, 24, "WAIT START", 100 + metr.getHeight());
				drawStringCenterOfPanel(g, Color.BLACK, 16, "Press 'ENTER' To Start", 100 + metr.getHeight() * 2);
				drawStringCenterOfPanel(g, Color.BLACK, 16, "Press 'Q' Back To Title", 100 + metr.getHeight() * 3);
			}

			color = Color.BLACK;
		}

		if (playMode.equals(PlayerMode.Single)) {

			if (player.equals(Player.Player1)) {
				drawStringCenterOfPanel(g, color, 16, "[방향키 ▲] 블럭 회전", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[방향키 ◀ ▶] 블럭 이동", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[방향키 ▼] 블럭 내리기", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[SPACE BAR] 블럭 한번에 내리기", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[SHIFT] 블럭 저장", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[ENTER] 다시 시작", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[ESC][P] 일시 중지", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[G] 고스트 블럭 모드", line += metr.getHeight());
			}

		} else if (playMode.equals(PlayerMode.Duo)) {

			if (player.equals(Player.Player1)) {
				drawStringCenterOfPanel(g, color, 16, "[I] 블럭 회전", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[J][K] 블럭 이동", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[K] 블럭 내리기", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[SPACE BAR] 블럭 한번에 내리기", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[SHIFT] 블럭 저장", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[ENTER] 다시 시작", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[ESC][P] 일시 중지", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[G] 고스트 블럭 모드", line += metr.getHeight());
			} else {
				drawStringCenterOfPanel(g, color, 16, "[8] 블럭 회전", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[4][6] 블럭 이동", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[5] 블럭 내리기", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[방향키 ▼] 블럭 한번에 내리기", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[방향키 ▲] 블럭 저장", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[ENTER] 다시 시작", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[ESC][P] 일시 중지", line += metr.getHeight());
				drawStringCenterOfPanel(g, color, 16, "[BACK SPACE ←] 고스트 블럭 모드", line += metr.getHeight());
			}

		}

	}

	/**
	 * 일시 정지 화면을 화면에 그려준다.
	 * 
	 * @param g
	 */
	private void drawPauseScreen(Graphics g) {
		Font small = new Font("Helvetica", Font.BOLD, 20);
		FontMetrics metr = getFontMetrics(small);

		int line = 150;

		drawStringCenterOfPanel(g, Color.RED, 24, "PAUSE", line += metr.getHeight());

		drawStringCenterOfPanel(g, Color.BLACK, 16, "Press 'ESC' or 'P' To Restart", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "Press 'Q' Back To Title", line += metr.getHeight());

		line += 80;

		drawStringCenterOfPanel(g, Color.BLACK, 16, "Points", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, String.valueOf(point), line += metr.getHeight());

		line += 20;

		drawStringCenterOfPanel(g, Color.BLACK, 16, "Penalty", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.RED, 16, String.valueOf(ghostUsed), line += metr.getHeight());

		line += 20;

		drawStringCenterOfPanel(g, Color.BLACK, 16, "Point Result", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLUE, 16, String.valueOf(point - ghostUsed), line += metr.getHeight());

	}

	public void dropDown() {

		if (!isStarted || isPaused)
			return;

		int newY = curY;
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY - 1))
				break;
			--newY;
		}
		pieceDropped();
	}

	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}

	private void clearBoard() {
		for (int i = 0; i < BoardHeight * BoardWidth; ++i)
			board[i] = Tetrominoes.NoShape;
	}

	private void pieceDropped() {

		for (int i = 0; i < 4; ++i) {
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BoardWidth) + x] = curPiece.getShape();
		}
		removeFullLines();

		if (!isFallingFinished)
			newPiece();
	}

	private void newPiece() {
		numTetrominoDropCount++;

		curPiece.setShape(nextPiece);

		nextPiece.setRandomShape();

		curX = BoardWidth / 2 + 1;
		curY = BoardHeight - 1 + curPiece.minY();

		// TODO 게임 종료 조건 수정 필요
		if (!tryMove(curPiece, curX, curY)) {
			doQuitGame();
		}
	}

	public void doQuitGame() {
		curPiece.setShape(Tetrominoes.NoShape);
		isStarted = false;
		isOver = true;
		timer.stop();
		repaint();
	}

	public boolean tryMove(Shape newPiece, int newX, int newY) {

		if (!isStarted || isPaused)
			return false;

		for (int i = 0; i < 4; ++i) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			if (shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}

	private void drawGhostPiece(Graphics g) {

		ghostPiece = curPiece;

		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

		int newY = curY;
		while (newY > 0) {
			if (!tryGhostPieceMove(ghostPiece, curX, newY - 1))
				break;
			--newY;
		}

		if (ghostPiece.getShape() != Tetrominoes.NoShape) {
			for (int i = 0; i < 4; ++i) {
				int x = ghostCurX + ghostPiece.x(i);
				int y = ghostCurY - ghostPiece.y(i);
				drawGhostSquare(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
						ghostPiece.getShape());
			}
		}

	}

	public boolean tryGhostPieceMove(Shape newGhostPiece, int newX, int newY) {
		for (int i = 0; i < 4; ++i) {
			int x = newX + ghostPiece.x(i);
			int y = newY - ghostPiece.y(i);
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			if (shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}

		ghostPiece = newGhostPiece;
		ghostCurX = newX;
		ghostCurY = newY;
		repaint();
		return true;
	}

	private void drawGhostSquare(Graphics g, int x, int y, Tetrominoes shape) {

		Color color = Color.BLACK;

		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);

		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
	}

	private void removeFullLines() {
		int numFullLines = 0;

		for (int i = BoardHeight - 1; i >= 0; --i) {
			boolean lineisFull = true;

			for (int j = 0; j < BoardWidth; ++j) {
				if (shapeAt(j, i) == Tetrominoes.NoShape) {
					lineisFull = false;
					break;
				}
			}

			if (lineisFull) {
				numFullLines++;
				for (int k = i; k < BoardHeight - 1; ++k) {
					for (int j = 0; j < BoardWidth; ++j)
						board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
				}
			}
		}

		if (numFullLines > 0) {
			numLinesRemoved += numFullLines;
			point = getPointAdd(numFullLines);
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NoShape);
			repaint();
		}
	}

	private int getPointAdd(int numFullLines) {
		int sum = point;

		for (int i = 0; i < numFullLines; i++) {
			if (isGhost)
				ghostUsed += numFullLines * 50;
			sum += numFullLines * 100;
		}

		return sum;
	}

	public int getPoint() {
		return point - ghostUsed;
	}

	public boolean isCompetition() {
		return isCompetition;
	}

	public int getNumLineRemoved() {

		if (numLinesRemoved > 1) {
			new Thread(() -> {

				try {
					isAttack = true;
					Thread.sleep(2000);
					isAttack = false;

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
		}

		int tmp = numLinesRemoved;
		numLinesRemoved = 0;
		return tmp;
	}

	private void drawMessageFollowingAttack(Graphics g) {

		// 공격을 했을 경우
		if (isAttack) {
			drawStringCenterOfPanel(g, Color.RED, 38, "공격!", 220);
		}

		// 공격을 받았을 경우
		if (attackType == 0)
			return;
		else {

			drawStringCenterOfPanel(g, Color.RED, 38, "공격 받음!", 220);
			drawStringCenterOfPanel(g, Color.RED, 24, attackMessage, 220 + 26);

		}

	}

	public void attack(int attackPower) {

		if (attackPower < 2)
			return;

		attackType = new Random().nextInt(5) + 1;

		new Thread(() -> {
			try {
				Thread.sleep(3000);
				attackType = 0;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}).start();

		switch (attackType) {
		case 0:
			break;
		case 1: // 공격불가
			attackMessage = "빗나감!";
			break;
		case 2:
			attackMessage = "한 줄 증가!";
			int passCell = new Random().nextInt(BoardWidth);

			for (int y = BoardHeight - 2; y >= 0; y--) {
				for (int x = 0; x < BoardWidth; x++) {
					board[((y + 1) * BoardWidth) + x] = board[(y) * BoardWidth + x];
					board[(y) * BoardWidth + x] = Tetrominoes.NoShape;
				}
			}
			for (int x = 0; x < BoardWidth; x++) {

				if (passCell == x)
					continue;

				board[x] = Tetrominoes.DotShape;
			}

			repaint();

			break;
		case 3:
			attackMessage = "블럭 바꾸기!";
			curPiece.setRandomShape();

			break;
		case 4:
			attackMessage = "블럭에 구멍 송송";
			for (int i = 0; i < BoardWidth * BoardHeight; i++) {
				if (new Random().nextBoolean())
					board[i] = Tetrominoes.NoShape;
			}
			break;

		case 5:
			attackMessage = "키보드가 말을 안들어여!";

			new Thread(() -> {
				isCrazyKeyboard = true;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isCrazyKeyboard = false;
			}).start();

			break;
		}

	}

	public void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
				new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
				new Color(218, 170, 0), Color.GRAY };

		Color color = colors[shape.ordinal()];

		if (isOver)
			color = Color.LIGHT_GRAY;

		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);

		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
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

		Font small = new Font("Malgun Gothic", Font.BOLD, size);
		FontMetrics metr = getFontMetrics(small);

		g.setColor(color);
		g.setFont(small);

		g.drawString(str, (int) (getSize().getWidth() - 100) / 2 - metr.stringWidth(str) / 2, height);

	}

	public Tetrominoes[] getBoard() {
		return board;
	}

}
