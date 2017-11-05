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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

import AITetris.View.Player;
import AITetris.View.PlayerMode;
import AITetris.View.Board.Tetrimino.Shape;
import AITetris.View.Board.Tetrimino.Tetrominoes;

/**
 * 테트리스의 게임 보드를 보여주는 패널
 * 
 * @author Jeongsam
 *
 **/
public class GameBoard extends JPanel implements ActionListener {

    int width;
    int heigh;

    final int BoardWidth = 12;
    final int BoardHeight = 24;

    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    public boolean isGhost = false;
    boolean isOver = false;
    int numLinesRemoved = 0;
    int numTetrominoDropCount = 0;
    int numCountGhostUse = 0;
    int curX = 0;
    int curY = 0;
    int ghostCurX = 0;
    int ghostCurY = 0;

    Shape curPiece;
    Shape ghostPiece;

    Tetrominoes[] board;

    InfoBoard infoBoard;

    long curTime;
    long defTime;
    long pauseTime;

    PlayerMode playMode;
    Player player;

    public GameBoard(PlayerMode playMode, Player player, int x, int y, int width, int height) {

	this.playMode = playMode;
	this.player = player;

	setLayout(null);
	setBoundProperty(x, y, width, height);

	infoBoard = new InfoBoard(this);
	add(infoBoard);

	infoBoard.setBounds(width - 100, 0, 100, height);

	setBorder(BorderFactory.createLineBorder(Color.black));

	setFocusable(true);

	curPiece = new Shape();
	ghostPiece = new Shape();

	timer = new Timer(400, this);
	timer.start();

	board = new Tetrominoes[BoardWidth * BoardHeight];

	clearBoard();

    }

    public void actionPerformed(ActionEvent e) {

	curTime = System.currentTimeMillis();
	infoBoard.leftTime = (defTime - curTime);
	if ((float) (defTime - curTime) / 1000 < 0) {
	    curPiece.setShape(Tetrominoes.NoShape);
	    timer.stop();
	    isStarted = false;
	    isOver = true;
	    repaint();
	}

	if ((float) (defTime - curTime) / 1000 < 300)
	    timer.setDelay(300);
	if ((float) (defTime - curTime) / 1000 < 180)
	    timer.setDelay(200);

	infoBoard.repaint();

	if (isFallingFinished) {
	    isFallingFinished = false;

	    newPiece();
	} else {
	    oneLineDown();
	}
    }

    public void setBoundProperty(int x, int y, int width, int height) {
	this.width = width;
	this.heigh = height;
	super.setBounds(x, y, width, height);
    }

    int squareWidth() {
	return (int) (getSize().getWidth() - 100) / BoardWidth;
    }

    int squareHeight() {
	return (int) getSize().getHeight() / BoardHeight;
    }

    Tetrominoes shapeAt(int x, int y) {
	return board[(y * BoardWidth) + x];
    }

    public void start() {
	if (isPaused)
	    return;

	curTime = System.currentTimeMillis();
	defTime = System.currentTimeMillis() + 600000;

	isStarted = true;
	isOver = false;
	isFallingFinished = false;
	isGhost = false;
	numCountGhostUse = 0;
	numLinesRemoved = 0;
	numTetrominoDropCount = 0;
	infoBoard.point = numLinesRemoved;
	clearBoard();
	infoBoard.cleanPieces();

	newPiece();
	timer.setDelay(400);
	timer.start();
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

	if (infoBoard.tempPiece.getShape() == Tetrominoes.NoShape) {
	    infoBoard.tempPiece.setShape(curPiece.getShape());
	    newPiece();
	} else {
	    if (tryExchange(infoBoard.tempPiece)) {
		Shape tempShape = new Shape();
		tempShape.setShape(infoBoard.tempPiece.getShape());
		infoBoard.tempPiece.setShape(curPiece.getShape());
		curPiece.setShape(tempShape.getShape());
	    }

	}

	infoBoard.repaint();

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

    public void paint(Graphics g) {
	super.paint(g);

	Dimension size = getSize();
	int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

	for (int i = 0; i < BoardHeight; ++i) {
	    for (int j = 0; j < BoardWidth; ++j) {
		Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
		if (shape != Tetrominoes.NoShape)
		    drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
	    }
	}

	if (isGhost) {
	    infoBoard.isGhost = true;
	    drawGhostPiece(g);
	} else
	    infoBoard.isGhost = false;

	if (curPiece.getShape() != Tetrominoes.NoShape) {
	    for (int i = 0; i < 4; ++i) {
		int x = curX + curPiece.x(i);
		int y = curY - curPiece.y(i);
		drawSquare(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
			curPiece.getShape());
	    }
	}

	if (isOver)
	    drawGameOverScreen(g);

	if (isPaused)
	    drawPauseScreen(g);

	if (numTetrominoDropCount < 3 && !isPaused && !isOver)
	    drawHelpScreen(g);

	g.setColor(Color.RED);
	g.drawLine(0, BoardHeight, (int) getSize().getWidth(), BoardHeight);

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

	if ((float) (defTime - curTime) / 1000 < 0)
	    drawStringCenterOfPanel(g, Color.RED, 24, "TIME OVER", line += metr.getHeight());
	else
	    drawStringCenterOfPanel(g, Color.RED, 24, "GAME OVER", line);

	drawStringCenterOfPanel(g, Color.BLACK, 16, "Press 'R' To Restart", line += metr.getHeight());

	line += 80;

	drawStringCenterOfPanel(g, Color.BLACK, 16, "Points", line += metr.getHeight());
	drawStringCenterOfPanel(g, Color.BLACK, 16, String.valueOf(infoBoard.point), line += metr.getHeight());

	line += 20;

	drawStringCenterOfPanel(g, Color.BLACK, 16, "Penalty", line += metr.getHeight());
	drawStringCenterOfPanel(g, Color.RED, 16, String.valueOf(infoBoard.ghostUsed), line += metr.getHeight());

	line += 20;

	drawStringCenterOfPanel(g, Color.BLACK, 16, "Point Result", line += metr.getHeight());
	drawStringCenterOfPanel(g, Color.BLUE, 16, String.valueOf(infoBoard.point - infoBoard.ghostUsed),
		line += metr.getHeight());

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

	int line = 180;

	if (playMode.equals(PlayerMode.Single) || playMode.equals(PlayerMode.AI)) {
	    drawStringCenterOfPanel(g, Color.BLACK, 16, "[방향키 ▲] 블럭 회전", line += metr.getHeight());
	    drawStringCenterOfPanel(g, Color.BLACK, 16, "[방향키 ◀ ▶] 블럭 이동", line += metr.getHeight());
	    drawStringCenterOfPanel(g, Color.BLACK, 16, "[방향키 ▼] 블럭 내리기", line += metr.getHeight());
	    drawStringCenterOfPanel(g, Color.BLACK, 16, "[SPACE BAR] 블럭 한번에 내리기", line += metr.getHeight());
	    drawStringCenterOfPanel(g, Color.BLACK, 16, "[SHIFT] 블럭 저장", line += metr.getHeight());
	    drawStringCenterOfPanel(g, Color.BLACK, 16, "[ENTER] 다시 시작", line += metr.getHeight());
	    drawStringCenterOfPanel(g, Color.BLACK, 16, "[ESC][P] 일시 중지", line += metr.getHeight());
	    drawStringCenterOfPanel(g, Color.BLACK, 16, "[G] 고스트 블럭 모드", line += metr.getHeight());
	} else if (playMode.equals(PlayerMode.Duo)) {

	    if (player.equals(Player.Player1)) {
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[I] 블럭 회전", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[J][K] 블럭 이동", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[K] 블럭 내리기", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[SPACE BAR] 블럭 한번에 내리기", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[SHIFT] 블럭 저장", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[ENTER] 다시 시작", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[ESC][P] 일시 중지", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[G] 고스트 블럭 모드", line += metr.getHeight());
	    } else {
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[8] 블럭 회전", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[4][6] 블럭 이동", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[5] 블럭 내리기", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[방향키 ▼] 블럭 한번에 내리기", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[방향키 ▲] 블럭 저장", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[ENTER] 다시 시작", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[ESC][P] 일시 중지", line += metr.getHeight());
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[BACK SPACE ←] 고스트 블럭 모드", line += metr.getHeight());
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

	line += 80;

	drawStringCenterOfPanel(g, Color.BLACK, 16, "Points", line += metr.getHeight());
	drawStringCenterOfPanel(g, Color.BLACK, 16, String.valueOf(infoBoard.point), line += metr.getHeight());

	line += 20;

	drawStringCenterOfPanel(g, Color.BLACK, 16, "Penalty", line += metr.getHeight());
	drawStringCenterOfPanel(g, Color.RED, 16, String.valueOf(infoBoard.ghostUsed), line += metr.getHeight());

	line += 20;

	drawStringCenterOfPanel(g, Color.BLACK, 16, "Point Result", line += metr.getHeight());
	drawStringCenterOfPanel(g, Color.BLUE, 16, String.valueOf(infoBoard.point - infoBoard.ghostUsed),
		line += metr.getHeight());

    }

    public void dropDown() {
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

	curPiece.setShape(infoBoard.nextPiece.getShape());
	infoBoard.nextPiece.setRandomShape();
	infoBoard.repaint();

	curX = BoardWidth / 2 + 1;
	curY = BoardHeight - 1 + curPiece.minY();

	if (!tryMove(curPiece, curX, curY)) {
	    curPiece.setShape(Tetrominoes.NoShape);
	    timer.stop();
	    isStarted = false;
	    isOver = true;
	    repaint();
	}
    }

    public boolean tryMove(Shape newPiece, int newX, int newY) {

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
	    infoBoard.point = getPointAdd(numFullLines);
	    isFallingFinished = true;
	    curPiece.setShape(Tetrominoes.NoShape);
	    repaint();
	}
    }

    private int getPointAdd(int numFullLines) {
	int sum = infoBoard.point;

	for (int i = 0; i < numFullLines; i++) {
	    if (isGhost)
		infoBoard.ghostUsed += numFullLines * 50;
	    sum += numFullLines * 100;
	}

	return sum;
    }

    private int getPoint() {
	return infoBoard.point - infoBoard.ghostUsed;
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
	Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
		new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
		new Color(218, 170, 0) };

	Color color = colors[shape.ordinal()];

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

	Font small = new Font("Helvetica", Font.BOLD, size);
	FontMetrics metr = getFontMetrics(small);

	g.setColor(color);
	g.setFont(small);

	g.drawString(str, (int) (getSize().getWidth() - 100) / 2 - metr.stringWidth(str) / 2, height);

    }

}
