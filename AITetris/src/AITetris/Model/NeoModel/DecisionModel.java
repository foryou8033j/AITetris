package AITetris.Model.NeoModel;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.Observable;

import javax.swing.SwingUtilities;

import AITetris.View.Board.GameBoard;
import AITetris.View.Board.Tetrimino.Shape;
import AITetris.View.Board.Tetrimino.Tetrominoes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 블럭 가중치에 따른 결정 모델
 * 
 * @author Jeongsam
 *
 */
public class DecisionModel {

    private GameBoard gameBoard;

    public boolean thinkEnd = false;
    public boolean moveEnd = false;

    public boolean decisionEnd = false;

    private int mostHigherWeightX = 0;
    private int mostHigherWeight = 0;
    private int mostHigherRotation = 0;

    private ControlModel controlModel;

    ObservableList<WeightModel> weightModel = FXCollections.observableArrayList();

    public DecisionModel(GameBoard gameBoard) {
	this.gameBoard = gameBoard;

	controlModel = new ControlModel(gameBoard);

	clear();
    }

    public void clear() {

	System.out.println("Clear");

	mostHigherRotation = 0;
	mostHigherWeight = 0;
	mostHigherWeightX = 0;

	thinkEnd = true;
	moveEnd = false;
	decisionEnd = true;

	weightModel.clear();
    }

    public boolean tryPieceMove(Shape newGhostPiece, int newX, int newY) {
	for (int i = 0; i < 4; ++i) {
	    int x = newX + newGhostPiece.x(i);
	    int y = newY - newGhostPiece.y(i);
	    if (x < 0 || x >= gameBoard.BoardWidth || y < 0 || y >= gameBoard.BoardHeight)
		return false;
	    if (gameBoard.shapeAt(x, y) != Tetrominoes.NoShape)
		return false;
	}
	return true;
    }

    public void checkBoard(int[][] weightBoard, int BoardWidth) {

	this.weightModel.clear();

	thinkEnd = false;
	decisionEnd = false;

	int newX = gameBoard.curX;

	// 블럭을 최좌측으로 밀착시킨다.
	while (newX > 0) {
	    if (!gameBoard.tryMove(gameBoard.curPiece, newX - 1, gameBoard.curY))
		break;
	    --newX;
	}

	if (newX < 0)
	    newX = 0;

	while (newX < BoardWidth) {

	    int tmpY = gameBoard.curY;

	    Shape tmpShape = new Shape();
	    tmpShape.setShape(gameBoard.curPiece.getShape());

	    int tmpXbeforeRotation = newX;

	    // 회전에 따라 구현한다
	    for (int rotation = 0; rotation < 4; rotation++) {

		// gameBoard.tryMove(gameBoard.curPiece.rotateRight(), gameBoard.curX,
		// gameBoard.curY);
		tmpShape = tmpShape.rotateRight();
		newX = tmpXbeforeRotation;
		tmpY = gameBoard.curY;

		// 블럭을 최하단으로 내린다고 가정한다.
		while (tmpY > 0) {
		    if (!tryPieceMove(tmpShape, newX, tmpY - 1))
			break;
		    --tmpY;
		}

		int[][] tmpWeightBoard = new int[gameBoard.BoardWidth][gameBoard.BoardHeight];

		for (int i = 0; i < gameBoard.BoardHeight; i++) {
		    for (int j = 0; j < gameBoard.BoardWidth; j++) {
			tmpWeightBoard[j][i] = weightBoard[j][i];
		    }
		}

		int weightSum = 0;

		// 모든 셀의 가중치 합을 구한다.
		System.out.print(newX + " : ");
		try {
		    for (int i = 0; i < 4; i++) {
			System.out.print(weightBoard[newX + tmpShape.x(i)][tmpY - tmpShape.y(i)] + " ");
			weightSum += weightBoard[newX + tmpShape.x(i)][tmpY - tmpShape.y(i)];
			tmpWeightBoard[newX + tmpShape.x(i)][tmpY - tmpShape.y(i)] = -1;

			try {
			    
			    if (tmpWeightBoard[newX - 1 + tmpShape.x(i)][tmpY - tmpShape.y(i)] == -1)
				weightSum += 1;
			    if (tmpWeightBoard[newX + tmpShape.x(i)][tmpY + 1 - tmpShape.y(i)] == -1)
				weightSum += 1;
			    if (tmpWeightBoard[newX + 1 + tmpShape.x(i)][tmpY - tmpShape.y(i)] == -1)
				weightSum += 1;
			    if (tmpWeightBoard[newX + tmpShape.x(i)][tmpY - 1 - tmpShape.y(i)] == -1)
				weightSum += 1;
			    
			} catch (Exception e) {
			    weightSum += 1;
			}

		    }

		} catch (Exception e) {
		    e.printStackTrace();
		}

		// 가로 열을 연산하여 빈칸에 따른 가중치를 연산한다.
		/*
		 * for(int i=0; i<gameBoard.BoardWidth; i++) {
		 * 
		 * for (int j = 0; j < 4; j++) {
		 * 
		 * if(tmpShape.y(j) == 1) { if( tmpWeightBoard[i][tmpY - tmpShape.y(i)] == -1)
		 * weightSum -= 10; } } }
		 */

		this.weightModel.add(new WeightModel(newX, tmpY, weightSum, tmpShape));

		System.out.println(" = " + weightSum + " rotation " + rotation);

	    }

	    // 다음 위치를 연산하기 전 이동 할 수 없으면 반복을 중단한다.
	    if (!tryPieceMove(gameBoard.curPiece, newX + 1, gameBoard.curY))
		break;

	    ++newX;
	}

	gameBoard.repaint();

	thinkEnd = true;

    }

    public boolean decision(int[][] weightBoard) {

	moveEnd = true;

	int higherWeight = 0;
	int higherIndex = 0;

	int lessVoidCell = 0;
	int underLessVoidCell = 0;

	// 최고 가중치 모델을 찾는다
	for (int i = 0; i < weightModel.size(); i++) {
	    if (higherWeight <= weightModel.get(i).getWeight()) {
		higherWeight = weightModel.get(i).getWeight();
		higherIndex = i;

		/*
		 * int voidCell = 0; int underVoidCell = 0;
		 * 
		 * // 해당 가중치 열의 빈칸의 수를 구한다. for (int j=0; j < gameBoard.BoardWidth; j++) { if
		 * (weightBoard[j][weightModel.get(i).getY()] != -1) voidCell++; if
		 * (weightBoard[j][weightModel.get(i).getY() - 2] != -1) underVoidCell++; }
		 * 
		 * // 빈칸의 수가 더 적다면 해당 모델을 적용 if (lessVoidCell > voidCell) { lessVoidCell =
		 * voidCell; higherWeight = weightModel.get(i).getWeight(); higherIndex = i; }
		 */

		// 빈칸의 수가 더 적다면 해당 모델을 적용
		/*
		 * if (underLessVoidCell > underVoidCell) { underLessVoidCell = underVoidCell;
		 * higherWeight = weightModel.get(i).getWeight(); higherIndex = i; }
		 */

	    }

	}

	// 최고 가중치 형태로 블럭을 회전한다.
	// 최고 가중치의 Shape 를 받아온다.
	gameBoard.curPiece = weightModel.get(higherIndex).getShape();

	// Board를 벗어나는 검산 오류를 수정한다.
	int j = 0;
	while (j++ < 4) {
	    for (int i = 0; i < 4; i++) {
		int x = gameBoard.curX + gameBoard.curPiece.x(i);
		// int y = gameBoard.curY - gameBoard.curPiece.y(i);

		if (x <= 0) {
		    System.out.println("MOVE RIGHT");
		    gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX + 2, gameBoard.curY);
		} else if (x >= gameBoard.BoardWidth) {
		    System.out.println("MOVE LEFT");
		    gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX - 2, gameBoard.curY);
		}
	    }
	}

	while (true) {

	    System.out.println("current X : " + gameBoard.curX);
	    System.out.println("Higher X : " + higherIndex);
	    System.out.println("Higher Data : " + higherIndex + " " + weightModel.get(higherIndex).getWeight());

	    if (gameBoard.curX > weightModel.get(higherIndex).getX()) {
		System.out.println("MOVE LEFT");
		if (!controlModel.moveLeft())
		    break;

	    } else if (gameBoard.curX < weightModel.get(higherIndex).getX()) {
		System.out.println("MOVE RIGHT");
		if (!controlModel.moveRight())
		    break;
	    } else {
		break;
	    }

	}

	while (true) {
	    if (!controlModel.moveDown())
		break;
	}

	weightModel.clear();

	return true;
    }

}
