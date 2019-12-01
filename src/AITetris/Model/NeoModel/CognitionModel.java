package AITetris.Model.NeoModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import AITetris.Model.Neo;
import AITetris.Model.Properties;
import AITetris.View.Board.GameBoard;
import AITetris.View.Board.Tetrimino.Shape;
import AITetris.View.Board.Tetrimino.Tetrominoes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 블럭의 배치 중요도 연산 모델
 * 
 * @author Jeongsam
 *
 */
public class CognitionModel {

    GameBoard gameBoard = null; // 게임데이터를 저장한다
    Neo neo = null; // 각 모듈과의 연동을 위한 Neo 객체를 저장한다.

    public CognitionModel(GameBoard gameBoard, Neo neo) {
	this.gameBoard = gameBoard;
	this.neo = neo;

	System.out.println("인지 모델 초기화");

    }

    // 가중치를 연산한다.
    public int[][] recognitionWeight(int[][] weightModel, int width, int height) {

	// 가장 낮은 층의 블럭이 0이다, 한칸씩 높여가며 가중치를 낮춘다.
	int weightMult = 0;

	for (int j = 0; j < width; ++j) {
	    for (int i = height - 1; i >= 0; --i) {
		if (weightModel[j][i] == -1)
		    continue;

		// 한칸이 내려갈 수록 가중치를 추가한다.
		weightModel[j][i] = weightMult += neo.properties.blockDownWeight;
		if (i == 0) // 최하단 블록은 가중치를 추가한다.
		    weightModel[j][i] += neo.properties.bottomCellsWeight;

	    }

	    // 새로운 열에서 가중치값을 초기화한다.
	    weightMult = 0;
	}

	for (int j = 0; j < width; ++j) {
	    for (int i = height - 1; i >= 0; --i) {
		if (weightModel[j][i] != -1) {

		    try {
			// 블럭 사이에 빈 공간이 끼인 경우 가중치를 추가한다.
			if (weightModel[j - 1][i] == -1)
			    weightModel[j][i] += neo.properties.voidCellbetweenBlocksWeight;

			if (weightModel[j - 2][i] == -1)
			    weightModel[j][i] += neo.properties.voidCellbetweenBlocksWeight;

			if (weightModel[j + 1][i] == -1)
			    weightModel[j][i] += neo.properties.voidCellbetweenBlocksWeight;

			if (weightModel[j + 2][i] == -1)
			    weightModel[j][i] += neo.properties.voidCellbetweenBlocksWeight;

			if (weightModel[j][i + 1] == -1)
			    weightModel[j][i] += neo.properties.voidCellbetweenBlocksWeight;

			if (weightModel[j][i + 2] == -1)
			    weightModel[j][i] += neo.properties.voidCellbetweenBlocksWeight;

		    } catch (Exception e) {
			weightModel[j][i] += neo.properties.voidCellStickyWallWeight;
		    }

		}

	    }

	    // 새로운 열에서 가중치값을 초기화한다.
	}

	return weightModel;
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

    // 각 좌표에 대한 테트리미노의 가중치를 연산한다.
    public ObservableList<WeightModel> checkBoardWeight(Graphics g, int[][] weightBoard) {

	ObservableList<WeightModel> weightModel = FXCollections.observableArrayList();
	weightModel.clear();

	// 원본 테트리미노의 형태와 좌표값을 받아 가상의 블럭을 생성한다.
	Shape tmpShape = gameBoard.curPiece;
	int tmpX = 0;

	// 현재 좌표 기준 좌/우의 블럭 배치시 가중치를 연산한다.
	while (tmpX < gameBoard.BoardWidth) {

	    // 가상 블럭이 현재 좌표 기준 좌측일때의 가중치 연산
	    getTetriminoWeight(g, tmpShape, tmpX, weightBoard, weightModel);

	    tmpX++;

	}

	gameBoard.repaint();

	return weightModel;

    }

    /**
     * 기본 가중치를 연산한다
     * @param g
     * @param shape
     * @param x
     * @param boardWeight
     * @param weightModel
     */
    private void getTetriminoWeight(Graphics g, Shape shape, int x, int[][] boardWeight, ObservableList<WeightModel> weightModel) {

	Shape tmpShape;

	tmpShape = null;

	// 임시 블럭을 복사한다.
	try {
	    tmpShape = (Shape) shape.clone();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	// g.setColor(new Color(new Random().nextInt(255), new Random().nextInt(255),
	// new Random().nextInt(255)));
	for (int rotate = 0; rotate < 4; rotate++) {

	    Color color = Color.BLACK;

	    switch (rotate) {
	    case 0:
		color = new Color(196, 211, 56);
		break;
	    case 1:
		color = new Color(219, 169, 148);
		break;
	    case 2:
		color = new Color(137, 177, 84);
		break;
	    case 3:
		color = new Color(4, 155, 173);
		break;
	    }

	    g.setColor(color);

	    // 블럭이 놓였다고 가정할때의 가중치 재연산을 위해 기존 가중치값을 복사한다.
	    int[][] tmpWeightBoard = new int[gameBoard.BoardWidth][gameBoard.BoardHeight];

	    for (int i = 0; i < gameBoard.BoardHeight; i++) {
		for (int j = 0; j < gameBoard.BoardWidth; j++) {
		    tmpWeightBoard[j][i] = boardWeight[j][i];
		}
	    }

	    int removeLines = 0;
	    int weightSum = 0;
	    int tmpX = x;
	    int tmpY = gameBoard.curY;

	    // 블럭을 회전시킨다.
	    tmpShape = tmpShape.rotateRight();

	    // 회전 후 해당 좌표에 블럭이 위치 할 수 없으면 다음 연산을 수행한다.
	    if (!tryPieceMove(tmpShape, x, tmpY))
		continue;

	    // 블럭을 최하단으로 내린다고 가정한다.
	    while (tmpY > 0) {
		if (!tryPieceMove(tmpShape, x, tmpY - 1))
		    break;
		--tmpY;
	    }

	    // 블록이 하강할 예상 지점을 그려준다
	    if (tmpShape.getShape() != Tetrominoes.NoShape) {
		for (int i = 0; i < 4; ++i) {

		    int drawX = tmpX + tmpShape.x(i);
		    int drawY = tmpY - tmpShape.y(i);

		    g.setColor(g.getColor().darker());
		    g.fillRect((10 + drawX * neo.squareWidth()) + 1,
			    ((gameBoard.BoardHeight - drawY - 1) * neo.squareHeight()) + 1, neo.squareWidth() - 2,
			    neo.squareHeight() - 2);

		    int sideX = (10 + drawX * neo.squareWidth());
		    int sideY = ((gameBoard.BoardHeight - drawY - 1) * neo.squareHeight());

		    g.setColor(color);
		    g.drawLine(sideX, sideY + neo.squareHeight() - 1, sideX, sideY);
		    g.drawLine(sideX, sideY, sideX + neo.squareWidth() - 1, sideY);

		    g.setColor(color);
		    g.drawLine(sideX + 1, sideY + neo.squareHeight() - 1, sideX + neo.squareWidth() - 1,
			    sideY + neo.squareHeight() - 1);
		    g.drawLine(sideX + neo.squareWidth() - 1, sideY + neo.squareHeight() - 1,
			    sideX + neo.squareWidth() - 1, sideY + 1);

		}
	    }

	    // 모든 셀의 가중치 합을 구한다.
	    try {

		// 블럭이 놓여진 위치의 가중치의 합산값을 구한다.
		for (int i = 0; i < 4; i++) {
		    // System.out.print(boardWeight[tmpX + tmpShape.x(i)][tmpY - tmpShape.y(i)] + "");
		    weightSum += boardWeight[tmpX + tmpShape.x(i)][tmpY - tmpShape.y(i)];
		    tmpWeightBoard[tmpX + tmpShape.x(i)][tmpY - tmpShape.y(i)] = -1;
		}

		removeLines = 0;

		// 블럭이 놓여졌다고 가정할 때 제거되는 라인의 합산값을 가중치에 더한다.
		for (int i = 0; i < 4; i++) {

		    int existCells = 0;
		    for (int cellX = 0; cellX < gameBoard.BoardWidth; cellX++) {
			if (tmpWeightBoard[cellX][tmpY - tmpShape.y(i)] == -1)
			    existCells++;
		    }
		    if (existCells == gameBoard.BoardWidth)
			removeLines++;
		}
		weightSum += removeLines * neo.properties.lineRemoveWeight; // 150

		// 블럭이 놓여 졌다고 가정할 때 놓여진 블럭이 빈공간을 만드는 갯수 만큼 가중치를 낮춘다.
		int voidCellBelowBlock = 0;
		int voidCellMadeBlock = 0;
		for (int i = 0; i < 4; i++) {

		    int belowBlocks = 0;

		    for (int h = tmpY - tmpShape.y(i) - 1; h >= 0; --h) {

			if (belowBlocks > 1)
			    break;

			if (tmpWeightBoard[tmpX + tmpShape.x(i)][h] != -1)
			    voidCellBelowBlock++;
			else
			    break;

			belowBlocks++;

		    }

		    weightSum += voidCellBelowBlock * neo.properties.belowVoidCellWeight;
		    if (voidCellBelowBlock > 0)
			voidCellMadeBlock++;
		    // System.out.println("Void Cell Vount " + voidCellBelowBlock);
		}

		weightSum += voidCellMadeBlock * neo.properties.createVoidCellWeight;

	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    int drawX = tmpX;
	    int drawY = tmpY;

	    Font tmpFont = g.getFont();
	    Font small = new Font("Helvetica", Font.BOLD, 14);
	    // FontMetrics metr = getFontMetrics(small);
	    g.setFont(small);
	    g.setColor(Color.BLACK);
	    g.drawString(String.valueOf(weightSum), (10 + drawX * neo.squareWidth()) + 1,
		    ((gameBoard.BoardHeight - drawY - 1) * neo.squareHeight()) + 1);

	    g.setFont(tmpFont);
	    // g.fillRect(, , neo.squareWidth() - 2, neo.squareHeight() - 2);

	    // 현재 구해진 가중치값을 가중치모델에 추가한다.
	    try {

		int mostHigherWeight = 0;

		for (WeightModel model : weightModel) {
		    if (model.getWeight() > mostHigherWeight)
			mostHigherWeight = model.getWeight();
		}

		if (mostHigherWeight < weightSum)
		    weightModel.add(new WeightModel(tmpX, gameBoard.curY, weightSum, (Shape) tmpShape.clone()));

	    } catch (CloneNotSupportedException e) {
		e.printStackTrace();
	    }

	}

    }

}
