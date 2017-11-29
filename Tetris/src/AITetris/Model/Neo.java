package AITetris.Model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

import AITetris.Tetris;
import AITetris.Model.NeoModel.CognitionModel;
import AITetris.Model.NeoModel.ControlModel;
import AITetris.Model.NeoModel.DecisionModel;
import AITetris.View.Board.GameBoard;
import AITetris.View.Board.Tetrimino.Tetrominoes;

/**
 * 인공지능 Neo 클래스, 하위 클래스 객체를 생성, 관리, 동작한다
 * @author Jeongsam
 *
 */
public class Neo extends JPanel implements ActionListener {

    private int BoardWidth;
    private int BoardHeight;

    private GameBoard gameBoard; // 게임 데이터를 저장한다.

    private Tetrominoes[] board; // 게임 데이터로부터 보드 정보를 저장한다.
    private Timer timer; // AI 의 동작 속도를 관리한다
    private boolean isDelayEnd = true; // 동작 지연 완료를 관리한다

    private int voidBoardCount = 0; // 빈 셀 공간을 저장한다

    private int weightModel[][]; // 기본 가중치를 저장한다

    private CognitionModel cognitionModel; // 인지 모델을 초기화한다
    private DecisionModel decisionModel; // 결정 모델을 초기화한다
    private ControlModel controlModel; // 조작 모델을 초기화한다

    public Properties properties;

    public Neo(Properties properties, GameBoard gameBoard) {

	setLayout(null);
	setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
	setFocusable(false);

	this.properties = properties;
	this.gameBoard = gameBoard;
	this.board = gameBoard.getBoard();

	BoardWidth = gameBoard.BoardWidth;
	BoardHeight = gameBoard.BoardHeight;

	weightModel = new int[BoardWidth][BoardHeight];

	controlModel = new ControlModel(gameBoard);
	cognitionModel = new CognitionModel(gameBoard, this);
	decisionModel = new DecisionModel(gameBoard, controlModel);

	timer = new Timer(200, this);
	timer.start();

    }

    private Tetrominoes shapeAt(int x, int y) {
	return board[(y * BoardWidth) + x];
    }

    // 블럭의 단일 세로 크기 반환
    public int squareWidth() {
	return (int) (getSize().getWidth() - 100) / BoardWidth;
    }

    // 블럭의 단일 세로 크기 반환
    public int squareHeight() {
	return (int) getSize().getHeight() / BoardHeight;
    }

    private void clearWeightModel() {
	for (int i = 0; i < BoardHeight; ++i) {
	    for (int j = 0; j < BoardWidth; ++j) {
		weightModel[j][i] = 0;
	    }
	}
    }

    /**
     * GameBoard로 부터 현재 Board 데이터를 받아 블록의 존재 유무를 판별한다
     * 기본 가중치를 연산하여 board에 적용한다
     * @return
     */
    private int[][] getBoard() {
	this.board = gameBoard.getBoard();

	decisionModel.clear();
	clearWeightModel();

	int tmpVoidBoardCount = 0;

	//블록 존재 유무 확인
	for (int i = 0; i < BoardHeight; ++i) {
	    for (int j = 0; j < BoardWidth; ++j) {
		if (shapeAt(j, i) == Tetrominoes.NoShape) {
		    weightModel[j][i] = 0; // 블럭이 비어있는 경우 0으로 처리한다
		} else {
		    weightModel[j][i] = -1; // 블럭이 이미 있는 경우 -1로 가중치를 계산하지 않는다
		    tmpVoidBoardCount++;
		}

	    }
	}

	// 빈 칸의 개수가 변한 경우 블럭의 하강이 완료 된 것으로 네오는 인식한다.
	if (tmpVoidBoardCount != voidBoardCount) {
	    voidBoardCount = tmpVoidBoardCount;
	    decisionModel.clear();
	}

	// 기본 가중치 연산
	weightModel = cognitionModel.recognitionWeight(weightModel, BoardWidth, BoardHeight);

	return weightModel;
    }

    @Override
    public void paint(Graphics g) {
	super.paint(g);

	drawPeace(g);

	//지속적인 가중치 연산을 위해 딜레이 여부와 관계없이 연산한다
	cognitionModel.checkBoardWeight(g, getBoard());

	if (isDelayEnd) {
	    isDelayEnd = false;
	    //연산 완료된 WeightModel을 결정 Model에 보낸다
	    if (decisionModel.decision(cognitionModel.checkBoardWeight(g, getBoard())))
		isDelayEnd = true;
	}

	drawWeight(g);
	repaint();
    }

    // 블럭을 그린다
    private void drawPeace(Graphics g) {
	// 보드에 저장된 블럭을 그린다
	for (int i = 0; i < BoardHeight; ++i) {
	    for (int j = 0; j < BoardWidth; ++j) {
		Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);

		int x = 10 + j * squareWidth();
		int y = i * squareHeight();

		drawSquare(g, x, y, shape);
	    }
	}

	// 현재 하강중인 블럭을 그린다
	// 현재 블럭을 그린다
	if (gameBoard.curPiece.getShape() != Tetrominoes.NoShape) {
	    for (int i = 0; i < 4; ++i) {
		int x = gameBoard.curX + gameBoard.curPiece.x(i);
		int y = gameBoard.curY - gameBoard.curPiece.y(i);
		drawSquare(g, 10 + x * squareWidth(), (BoardHeight - y - 1) * squareHeight(),
			gameBoard.curPiece.getShape());
	    }
	}

    }

    // 단일 블럭을 그린다
    public void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {

	if (shape != Tetrominoes.NoShape) {

	    g.setColor(Color.LIGHT_GRAY);
	    g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
	}

    }

    /**
     * 각 위치의 가중치를 시각화한다.
     * 
     * @param g
     *            그래픽스 모델
     * @param x
     *            좌표
     * @param y
     *            좌표
     */
    private void drawWeight(Graphics g) {

	g.setColor(Color.BLACK);

	for (int i = 0; i < BoardHeight; ++i) {
	    for (int j = 0; j < BoardWidth; ++j) {

		int x = 8 + j * squareWidth();
		int y = 575 - i * squareHeight();

		g.drawString(String.valueOf(weightModel[j][i]), (x + 1) + squareWidth() / 2 - 3,
			4 + (y + 1) + squareHeight() / 2);

	    }
	}

    }

    public void actionPerformed(ActionEvent e) {

	isDelayEnd = !isDelayEnd;

    }

}
