package AITetris.Model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

import AITetris.Model.NeoModel.CognitionModel;
import AITetris.Model.NeoModel.DecisionModel;
import AITetris.View.Board.GameBoard;
import AITetris.View.Board.Tetrimino.Tetrominoes;

public class Neo extends JPanel implements ActionListener {

<<<<<<< HEAD
    private NeoType type;
=======
	private int BoardWidth;
	private int BoardHeight;
>>>>>>> refs/heads/0.2v

    private int BoardWidth;
    private int BoardHeight;

    private GameBoard gameBoard;

    private Tetrominoes[] board;
    private Timer timer;

    // ObservableList<WeightModel> weightModel =
    // FXCollections.observableArrayList();

    private int voidBoardCount = 0;

<<<<<<< HEAD
    private int weightModel[][];
=======
	private CognitionModel cognitionModel;
	private DecisionModel decisionModel;
	
	private Graphics g;
>>>>>>> refs/heads/0.2v

<<<<<<< HEAD
    private CognitionModel cognitionModel;
    private DecisionModel decisionModel;
=======
	public Neo(GameBoard gameBoard) {

		setLayout(null);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
		setFocusable(false);

		this.gameBoard = gameBoard;
		this.board = gameBoard.getBoard();
>>>>>>> refs/heads/0.2v

    private DeepLearningModel deepLearningModel = null;

<<<<<<< HEAD
    public Neo(GameBoard gameBoard, NeoType neoType) {
=======
		weightModel = new int[BoardWidth][BoardHeight];
>>>>>>> refs/heads/0.2v

<<<<<<< HEAD
	setLayout(null);
	setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
	setFocusable(false);

	this.type = neoType;

	this.gameBoard = gameBoard;
	this.board = gameBoard.getBoard();

	BoardWidth = gameBoard.BoardWidth;
	BoardHeight = gameBoard.BoardHeight;

	weightModel = new int[BoardWidth][BoardHeight];

	cognitionModel = new CognitionModel();
	decisionModel = new DecisionModel(gameBoard);

	if (neoType.equals(NeoType.LEARNING)) {

	    try {
		deepLearningModel = new DeepLearningModel(this, gameBoard);
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	} else {
=======
		cognitionModel = new CognitionModel();
		decisionModel = new DecisionModel(this, gameBoard);

		timer = new Timer(1, this);
		timer.start();
>>>>>>> refs/heads/0.2v

	}

	timer = new Timer(1, this);
	timer.start();

    }

    private Tetrominoes shapeAt(int x, int y) {
	return board[(y * BoardWidth) + x];
    }

    // 블럭의 단일 세로 크기 반환
    int squareWidth() {
	return (int) (getSize().getWidth() - 100) / BoardWidth;
    }

    // 블럭의 단일 세로 크기 반환
    int squareHeight() {
	return (int) getSize().getHeight() / BoardHeight;
    }

    private void clearWeightModel() {
	for (int i = 0; i < BoardHeight; ++i) {
	    for (int j = 0; j < BoardWidth; ++j) {
		weightModel[j][i] = 0;
	    }
	}
    }

<<<<<<< HEAD
    public int[][] getModel() {
=======
	// 블럭의 단일 세로 크기 반환
	public int squareWidth() {
		return (int) (getSize().getWidth() - 100) / BoardWidth;
	}
>>>>>>> refs/heads/0.2v

<<<<<<< HEAD
	this.board = gameBoard.getBoard();
=======
	// 블럭의 단일 세로 크기 반환
	public int squareHeight() {
		return (int) getSize().getHeight() / BoardHeight;
	}
>>>>>>> refs/heads/0.2v

	clearWeightModel();

	int tmpVoidBoardCount = 0;

<<<<<<< HEAD
	for (int i = 0; i < BoardHeight; ++i) {
	    for (int j = 0; j < BoardWidth; ++j) {
		if (shapeAt(j, i) == Tetrominoes.NoShape) {
		    weightModel[j][i] = 0; // 블럭이 비어있는 경우 0으로 처리한다
		} else {
		    weightModel[j][i] = 1; // 블럭이 이미 있는 경우 1로 처리한다.
		    tmpVoidBoardCount++;
=======
		decisionModel.clear();
		clearWeightModel();

		int tmpVoidBoardCount = 0;

		for (int i = 0; i < BoardHeight; ++i) {
			for (int j = 0; j < BoardWidth; ++j) {
				if (shapeAt(j, i) == Tetrominoes.NoShape) {
					weightModel[j][i] = 0; // 블럭이 비어있는 경우 0으로 처리한다
				} else {
					weightModel[j][i] = -1; // 블럭이 이미 있는 경우 -1로 가중치를 계산하지 않는다
					tmpVoidBoardCount++;
				}

			}
>>>>>>> refs/heads/0.2v
		}

	    }
	}

	// 빈 칸의 개수가 변한 경우 블럭의 하강이 완료 된 것으로 네오는 인식한다.
	if (tmpVoidBoardCount != voidBoardCount) {
	    voidBoardCount = tmpVoidBoardCount;

	    new Thread(() -> {
		try {
		    deepLearningModel.initLearn();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }).start();

	}

	return weightModel;

<<<<<<< HEAD
    }

    public int[][] getWeightModel() {
	this.board = gameBoard.getBoard();

	decisionModel.clear();
	clearWeightModel();

	int tmpVoidBoardCount = 0;

	for (int i = 0; i < BoardHeight; ++i) {
	    for (int j = 0; j < BoardWidth; ++j) {
		if (shapeAt(j, i) == Tetrominoes.NoShape) {
		    weightModel[j][i] = 0; // 블럭이 비어있는 경우 0으로 처리한다
		} else {
		    weightModel[j][i] = -1; // 블럭이 이미 있는 경우 -1로 가중치를 계산하지 않는다
		    tmpVoidBoardCount++;
=======
		getBoard();
		drawPeace(g);
		
		
		if (decisionModel != null) {
			if (decisionModel.decisionEnd) {
				if (decisionModel.thinkEnd)
					//getBoard();
					decisionModel.checkBoardWeight(g, weightModel);
					// decisionModel.checkBoard(weightModel, BoardWidth);

				if (decisionModel.thinkEnd && !decisionModel.moveEnd) {
					decisionModel.decision(weightModel);
				}
			}
>>>>>>> refs/heads/0.2v
		}
<<<<<<< HEAD

	    }
=======
		
		drawWeight(g);
		
		repaint();

>>>>>>> refs/heads/0.2v
	}

	// 빈 칸의 개수가 변한 경우 블럭의 하강이 완료 된 것으로 네오는 인식한다.
	if (tmpVoidBoardCount != voidBoardCount) {
	    voidBoardCount = tmpVoidBoardCount;
	    decisionModel.clear();
	}

<<<<<<< HEAD
	// 가중치 연산
	weightModel = cognitionModel.recognitionWeight(weightModel, BoardWidth, BoardHeight);
=======
	// 단일 블럭을 그린다
	public  void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
>>>>>>> refs/heads/0.2v

	return weightModel;

    }

    @Override
    public void paint(Graphics g) {
	super.paint(g);

	getWeightModel();
	drawPeace(g);
	drawWeight(g);
	repaint();

	if (type.equals(NeoType.LOGIC)) {

	} else {

	    if (deepLearningModel != null)
		deepLearningModel.paint(g);
	}
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

<<<<<<< HEAD
    }

    // 단일 블럭을 그린다
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {

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

	// getBoard();

	if (decisionModel.decisionEnd) {
	    if (decisionModel.thinkEnd)
		decisionModel.checkBoard(weightModel, BoardWidth);

	    if (decisionModel.thinkEnd && !decisionModel.moveEnd) {
		decisionModel.decision(weightModel);
	    }
	}

	if (this.type.equals(NeoType.LOGIC)) {
	    if (decisionModel == null)
		return;

	} else {
	    getModel();
	}
=======
	public void actionPerformed(ActionEvent e) {
>>>>>>> refs/heads/0.2v

		// getBoard();

		

	}

}
