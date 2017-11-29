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
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import AITetris.Tetris;
import AITetris.Model.Properties;
import AITetris.Model.Rank;
import AITetris.Util.RankingAscending;
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

    public final int BoardWidth = 12; // 블럭의 가로 개수를 정의한다
    public final int BoardHeight = 24; // 블럭의 세로 개수를 정의한다.

    private Timer timer; // 블럭 하강 속도를 관리한다.

    public boolean isFallingFinished = false; // 블럭의 하강 완료 여부를 관리한다.
    boolean isStarted = false; // 게임 시작 여부 관리
    boolean isPaused = false; // 일시 중지 여부 관리
    boolean isGhost = false; // 고스트 모드 사용 여부 관리
    boolean isOver = false; // 게임 종료 여부 관리
    boolean isWin = false; // 승리 판정 여부 관리
    boolean isDraw = false; // 무승부 판정 여부 관리
    boolean isRanked = false; // 랭킹 등록 여부 관리
    boolean isCompetition = false; // 대전모드 인지 확인한다
    boolean isCrazyKeyboard = false; // 키보드 비정상 공격을 받았는지 확인한다
    boolean isAttack = false; // 현재 공격받았는지 확인한다

    private int numLinesRemoved = 0; // 제거 된 라인의 개수를 저장한다
    private int numTetrominoDropCount = 0; // 하강한 테트리미노의 개수를 저장한다.
    private int numCountGhostUse = 0; // 사용한 고스트블럭 개수를 저장한다
    private int attackType = 0; // 수행한 공격 형태를 저장한다
    private String attackMessage = ""; // 공격 메세지 저장용 임시 변수

    public int curX = 0; // 현재 블럭의 X 좌표
    public int curY = 0; // 현재 블럭의 Y 좌표
    public int ghostCurX = 0; // 고스트블럭의 X좌표
    public int ghostCurY = 0; // 고스트블럭의 Y좌표

    public int point = 0; // 현재 점수
    public int ghostUsed = 0; // 고스트를 이용해 제거한 라인 개수

    public Shape curPiece; // 현재 블록 형태
    Shape nextPiece;
    Shape ghostPiece;
    Shape tempPiece;

    private Tetrominoes[] board; // 하강한 블록이 저장되는 배열

    private Tetris tetris;
    private InfoBoard infoBoard;

    long curTime; // 게임 시간 정보 관리
    long defTime;
    long pauseTime;

    PlayerMode playMode; // 플레이 형태
    Player player; // 플레이어 종류

    public GameBoard(Tetris tetris, boolean isCompetition, PlayerMode playMode, Player player, int x, int y, int width,
	    int height) {

	this.tetris = tetris;

	this.playMode = playMode;
	this.player = player;
	this.isCompetition = isCompetition;

	// 레이아웃을 그려준다
	setLayout(null);
	setBounds(x, y, width, height);
	setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
	setFocusable(true);

	board = new Tetrominoes[BoardWidth * BoardHeight];

	// Shape 객체 초기화
	curPiece = new Shape();
	nextPiece = new Shape();
	ghostPiece = new Shape();
	tempPiece = new Shape();

	// InforBoard 객체 초기화
	infoBoard = new InfoBoard(this);
	infoBoard.setBounds(width - 100, 0, 100, height);
	add(infoBoard);

	timer = new Timer(300, this);
	// timer.start();

	clearBoard();

    }

    public void actionPerformed(ActionEvent e) {

	curTime = System.currentTimeMillis();
	infoBoard.leftTime = (defTime - curTime);

	// 시관 초과하였을 경우 동악
	if ((float) (defTime - curTime) / Properties.time < 0) {
	    curPiece.setShape(Tetrominoes.NoShape);
	    isStarted = false;
	    isOver = true;
	    timer.stop();
	}

	// 난이도 조절부
	if ((float) (defTime - curTime) / 1000 < 300) {
	    timer.setDelay(250);
	    tetris.getMusicPlayer().changeMusic();
	}

	if ((float) (defTime - curTime) / 1000 < 180) {
	    tetris.getMusicPlayer().changeMusic();
	    timer.setDelay(200);
	}

	// 하강이 끝나면 새로운 블록을 생성한다
	if (isFallingFinished) {
	    isFallingFinished = false;
	    newPiece();
	} else {
	    // 하강이 끝나지 않았다면 현재 하강중인 테트리미노를 한칸 내린다
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

    /**
     * 좌표상 board 1차원 배열에 위치한 테트리미노의 정보를 반환한다
     * 
     * @param x
     *            좌표
     * @param y
     *            좌표
     * @return {@link Tetrominoes}
     */
    public Tetrominoes shapeAt(int x, int y) {
	return board[(y * BoardWidth) + x];
    }

    /**
     * 게임 정보를 초기화하고, Timer를 시작하여 게임을 동작한다
     */
    public void start() {
	if (isPaused || isStarted)
	    return;

	else {

	    // 시간 초기화
	    curTime = System.currentTimeMillis();
	    defTime = System.currentTimeMillis() + Properties.time;

	    // 기본값 초기화
	    isRanked = false;
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

	    // Board 초기화
	    clearBoard();

	    // Shape 객체 초기화
	    tempPiece.setShape(Tetrominoes.NoShape);
	    nextPiece.setRandomShape();

	    // Timer 시작 (ActionListener 가 동작한다)
	    timer.setDelay(300);
	    timer.start();

	    // 새로운 Shape 생성
	    newPiece();
	}

    }

    /**
     * 일시 중지를 동작한다, 이미 일시중지 중인 경우 일시정지를 해제한다
     */
    public void pause() {
	if (!isStarted)
	    return;

	isPaused = !isPaused;

	if (isPaused) {
	    tetris.getMusicPlayer().pause();
	    pauseTime = System.currentTimeMillis(); // 중지한 시간 저장
	    timer.stop();
	} else {
	    tetris.getMusicPlayer().play();
	    timer.start();
	    defTime += (System.currentTimeMillis() - pauseTime); // 중지 되었던 시간만큼 목표시간을 추가한다
	}

	repaint();
    }

    /**
     * 임시 블록과 교환한다, 없는 경우 새로 추가한다.
     */
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

    /**
     * 블록을 회전한다, 회전 할 수 없는 경우 회전 가능한 위치까지 이동한다
     * 
     * @return 회전 가능 여부
     */
    public boolean tryRotate() {

	if (!tryMove(curPiece.rotateRight(), curX, curY)) {

	    // 회전이 불가능한 경우, 회전 가능한 위치까지 이동
	    if (curX < BoardWidth / 2)
		while (!tryMove(curPiece.rotateRight(), curX++, curY))
		    ;
	    else
		while (!tryMove(curPiece.rotateRight(), curX--, curY))
		    ;

	} else {
	    if (isGhost)
		tryGhostPieceMove(ghostPiece.rotateRight(), ghostCurX, ghostCurY);
	}
	return true;
    }

    /**
     * 블록 교환이 가능한지 여부를 검사한다, 교환 하려는 위치에 다른 블록이 위치하지 않았는지 검사한다.
     * 
     * @param newPiece
     * @return
     */
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

    /**
     * 게임을 초기화한다
     */
    public void reset() {
	start();
    }

    @Override
    public void paint(Graphics g) {
	super.paint(g);

	// 배경 라인을 그려준다.
	drawGridLine(g);

	// 테트리미노들을 그려준다.
	drawShapes(g);

	// UI Draw 관련
	// 3개의 Tetromino 가 Drop 하기 전에는 HelpScreen 을 그려준다
	if (numTetrominoDropCount < 3 && !isStarted && !isOver)
	    drawHelpScreen(g); // 도움말을 그려준다
	
	if (isOver)
	    drawGameOverScreen(g);	//게임 종료 화면을 그려준다

	if (isPaused && isStarted)
	    drawPauseScreen(g);		//일시중지 화면을 그려준다

	// 네오 플레이 문구를 그려준다.
	if (player.equals(Player.Neo) && !isOver)
	    drawStringCenterOfPanel(g, Color.GRAY, 16, "AI. NEO 가 플레이합니다", 260);

	//공격 메세지를 그려준다
	drawMessageFollowingAttack(g);

	// 넘지말아야 할 선을 그려준다
	//TODO  이 위치는 살짝 애매함, 수정 필요 171108
	g.setColor(Color.RED);
	g.drawLine(0, BoardHeight, (int) getSize().getWidth() - 100, BoardHeight);

	g.setColor(Color.BLACK);

	infoBoard.repaint();

    }

    /**
     * 배경 가이드 라인을 그려준다.
     * 
     * @param g
     */
    private void drawGridLine(Graphics g) {

	Dimension size = getSize();
	int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

	Color color = Color.LIGHT_GRAY;
	g.setColor(color);

	// 각 칸의 격자를 그린다.
	// 세로 라인을 그린다.
	for (int i = 1; i < BoardWidth; ++i) {
	    g.drawLine(i * squareWidth(), 1, i * squareWidth(), BoardHeight * squareHeight() - 2);
	}

	// 가로 라인을 그린다
	for (int i = 1; i < BoardHeight; ++i) {
	    g.drawLine(1, i * squareHeight(), BoardWidth * squareWidth(), i * squareHeight());
	}

    }

    /**
     * 블록을 그려준다
     * @param g
     */
    private void drawShapes(Graphics g) {
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
	    if (isDraw && !isWin) {

		drawStringCenterOfPanel(g, Color.RED, 36, "DRAW!", line - metr.getHeight() * 2);

	    }
	    if (isWin && !isDraw) {
		if (player.equals(Player.Neo))
		    drawStringCenterOfPanel(g, Color.RED, 36, "NEO WIN!", line - metr.getHeight() * 2);
		else
		    drawStringCenterOfPanel(g, Color.RED, 36, "YOU WIN!", line - metr.getHeight() * 2);
	    } else if (!isWin && !isDraw) {

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

	drawStringCenterOfPanel(g, Color.BLACK, 16, "[ENTER] 다시시작", line += metr.getHeight());
	drawStringCenterOfPanel(g, Color.BLACK, 16, "[Q] 타이틀로 돌아가기", line += metr.getHeight());

	if ((!isRanked && !player.equals(Player.Neo)) && isWin || (!isRanked && playMode.equals(PlayerMode.Single)))
	    drawStringCenterOfPanel(g, Color.BLACK, 16, "[G] 랭킹 등록", line += metr.getHeight());

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

    /**
     * 종료한다
     */
    @Deprecated
    private void quit() {
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    }

    /**
     * 도움말 화면을 그려준다
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
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[ENTER] 를 눌러 게임 시작", 100 + metr.getHeight() * 2);
		drawStringCenterOfPanel(g, Color.BLACK, 16, "[Q] 타이틀로 돌아가기", 100 + metr.getHeight() * 3);
	    }

	    color = Color.BLACK;
	}

	// 1인용 키, AI 키 도움말
	if (playMode.equals(PlayerMode.Single) || playMode.equals(PlayerMode.AI)
		|| playMode.equals(PlayerMode.AI_Competition)) {
	    
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

	    //2인용 키 도움말
	} else if (playMode.equals(PlayerMode.Duo) || playMode.equals(PlayerMode.Duo_Competition)) {

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

	drawStringCenterOfPanel(g, Color.BLACK, 16, "[ESC][P] 게임 복귀", line += metr.getHeight());
	drawStringCenterOfPanel(g, Color.BLACK, 16, "[Q] 타이틀로 돌아가기", line += metr.getHeight());

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

    /**
     * 현재 하강중일 블록을 바닥까지 내린다
     */
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

    /**
     * 현재 하강중일 블록을 한칸 내린다
     */
    private void oneLineDown() {
	if (!tryMove(curPiece, curX, curY - 1))
	    pieceDropped();
    }

    /**
     * 블록 데이터를 초기화한다
     */
    private void clearBoard() {
	for (int i = 0; i < BoardHeight * BoardWidth; ++i)
	    board[i] = Tetrominoes.NoShape;
    }

    /**
     * 블록 하강이 완료 하였을때 조건을 검사한다
     */
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

    /**
     * 새로운 블록을 생성한다
     */
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

    /**
     * 게임을 종료한다
     */
    public void doQuitGame() {
	curPiece.setShape(Tetrominoes.NoShape);
	isStarted = false;
	isOver = true;
	timer.stop();
	repaint();
    }

    /**
     * 블록의 이동이 가능한지 검사한다
     * @param newPiece
     * @param newX
     * @param newY
     * @return
     */
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

    /**
     * 고스트 블록을 그려준다
     * @param g
     */
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

    /**
     * 고스트블록이 이동이 가능한지 검사한다
     * @param newGhostPiece
     * @param newX
     * @param newY
     * @return
     */
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

    /**
     * 고스트 블록을 그려준다, 고스트블록은 단색이다
     * @param g
     * @param x
     * @param y
     * @param shape
     */
    private void drawGhostSquare(Graphics g, int x, int y, Tetrominoes shape) {

	Color color = Color.LIGHT_GRAY;

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
     * 전체 보드를 검사하고 가득찬 라인을 제거한다.
     * 제거 된 라인 개수에 따라 점수를 부여한다
     */
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

    /**
     * 제거된 라인의 수 만큼 점수를 추가한다
     * @param numFullLines
     * @return
     */
    private int getPointAdd(int numFullLines) {
	int sum = point;

	for (int i = 0; i < numFullLines; i++) {
	    if (isGhost)
		ghostUsed += numFullLines * 50;
	    sum += numFullLines * 100;
	}

	return sum;
    }

    /**
     * 점수 반환
     * @return
     */
    public int getPoint() {
	return point - ghostUsed;
    }

    /**
     * 대전 모드인지 확인한다
     * @return
     */
    public boolean isCompetition() {
	return isCompetition;
    }

    /**
     * 제거 된 라인 수를 반환한다
     * @return
     */
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

    /**
     * 공격 당했 / 공격했을 경우의 메세지를 그려준다
     * @param g
     */
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

    /**
     * 공격 당했을 때 현재 GameBoard에 적용되는 효과를 적용한다
     * @param attackPower
     */
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

    /**
     * 블록을 그려준다
     * @param g
     * @param x
     * @param y
     * @param shape
     */
    public void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
	Color colors[] = { 
		new Color(0, 0, 0), 
		new Color(204, 102, 102), 
		new Color(102, 204, 102),
		new Color(102, 102, 204), 
		new Color(204, 204, 102), 
		new Color(204, 102, 204), 
		new Color(102, 204, 204),
		new Color(218, 170, 0), 
		Color.GRAY };

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
     * @param g 그래픽스 모델
     * @param color 색상
     * @param size 폰트 사이즈
     * @param str 문자열
     * @param height 출력 높이
     */
    private void drawStringCenterOfPanel(Graphics g, Color color, int size, String str, int height) {

	Font small = new Font("Malgun Gothic", Font.BOLD, size);
	FontMetrics metr = getFontMetrics(small);

	g.setColor(color);
	g.setFont(small);

	g.drawString(str, (int) (getSize().getWidth() - 100) / 2 - metr.stringWidth(str) / 2, height);

    }

    /**
     * 현재 Board 데이터를 반환한다
     * @return
     */
    public Tetrominoes[] getBoard() {
	return board;
    }

    /**
     * 랭킹을 등록한다
     */
    //TODO 랭킹 등록 후 새로운 Dialog가 뜨는데 특정 경우에 키입력이 멈추는 문제있음 (171125)
    public void joinRank() {

	if (isRanked)
	    return;

	/*
	 * List<Rank> list = tetris.getDB().selectAll();
	 * 
	 * RankingAscending ascending = new RankingAscending(); Collections.sort(list,
	 * ascending);
	 * 
	 * 
	 * int size = (list.size() > 10) ? 10 : list.size();
	 * 
	 * for(int i=size-1; i>=0; i--) { if (list.get(i).getPoint() > getPoint()) {
	 * JOptionPane.showConfirmDialog(this, "점수가 낮아 랭킹에 등록 할 수 없습니다.", "등록 실패",
	 * JOptionPane.WARNING_MESSAGE, JOptionPane.CLOSED_OPTION); isRanked = true;
	 * return; } }
	 */

	while (!isRanked) {
	    String name = JOptionPane.showInputDialog(this, "등록 할 이름을 입력 해 주세요.", player.name(),
		    JOptionPane.INFORMATION_MESSAGE);

	    if (!name.equals("")) {
		tetris.getDB().insertRank(new Rank(name, playMode.toString(), getPoint()));

		isRanked = true;
		requestFocus();
		return;

	    } else {
		JOptionPane.showConfirmDialog(this, "올바른 이름을 입력 해 주세요.", "등록 실패", JOptionPane.WARNING_MESSAGE,
			JOptionPane.OK_OPTION);
	    }
	}

    }

}
