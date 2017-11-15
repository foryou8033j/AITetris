package AITetris.Model.NeoModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.SwingUtilities;

import AITetris.Model.Neo;
import AITetris.View.Board.GameBoard;
import AITetris.View.Board.Tetrimino.Shape;
import AITetris.View.Board.Tetrimino.Tetrominoes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 블럭 가중치에 따른 의사 결정 모델
 * 
 * @author Jeongsam
 *
 */
public class DecisionModel {

	private Neo neo;
	private GameBoard gameBoard;

	public boolean thinkEnd = false;
	public boolean moveEnd = false;

	public boolean decisionEnd = false;

	private int mostHigherWeightX = 0;
	private int mostHigherWeight = 0;
	private int mostHigherRotation = 0;

	private ControlModel controlModel;

	Shape tmpShape = null;
	int tmpX = 0;
	int tmpY = 0;

	ObservableList<WeightModel> weightModel = FXCollections.observableArrayList();

	public DecisionModel(Neo neo, GameBoard gameBoard) {
		this.neo = neo;
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

	@Deprecated
	public void checkBoard(int[][] weightBoard, int BoardWidth) {

		this.weightModel.clear();

		thinkEnd = false;
		decisionEnd = false;

		int tmpX = gameBoard.curX;
		int tmpY = gameBoard.curY;
		int minX = 0;

		// 블럭을 최좌측으로 밀착시킨다.

		if (tmpX < 0)
			tmpX = 0;

		int tmpXLocationBeforeMove = tmpX;

		while (tmpX < BoardWidth) {

			// 기준위치는 실제 블럭의 X좌표로 한다.
			if (tmpX < gameBoard.curX) {
				// 회전을 한 후 좌표 이동을 하여 충돌연산을 수행한다.

			} else {

			}

			// 세로 높이를 갱신한다.
			tmpY = gameBoard.curY;

			// 가상의 테트로미노를 생성한다.
			Shape tmpShape = new Shape();
			tmpShape.setShape(gameBoard.curPiece.getShape());

			while (tmpX > minX) {
				if (!gameBoard.tryMove(gameBoard.curPiece, tmpX - 1, gameBoard.curY))
					break;
				--tmpX;
			}
			minX++;

			// 회전에 따라 구현한다
			for (int rotation = 0; rotation < 4; rotation++) {

				// gameBoard.tryMove(gameBoard.curPiece.rotateRight(), gameBoard.curX,
				// gameBoard.curY);
				tmpShape = tmpShape.rotateRight();
				tmpX = tmpXLocationBeforeMove;
				tmpY = gameBoard.curY;

				// 블럭을 최하단으로 내린다고 가정한다.
				while (tmpY > 0) {
					if (!tryPieceMove(tmpShape, tmpX, tmpY - 1))
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
				System.out.print(tmpX + " : ");
				try {
					for (int i = 0; i < 4; i++) {
						System.out.print(weightBoard[tmpX + tmpShape.x(i)][tmpY - tmpShape.y(i)] + " ");
						weightSum += weightBoard[tmpX + tmpShape.x(i)][tmpY - tmpShape.y(i)];
						tmpWeightBoard[tmpX + tmpShape.x(i)][tmpY - tmpShape.y(i)] = -1;

						try {

							if (tmpWeightBoard[tmpX - 1 + tmpShape.x(i)][tmpY - tmpShape.y(i)] == -1)
								weightSum += 1;
							if (tmpWeightBoard[tmpX + tmpShape.x(i)][tmpY + 1 - tmpShape.y(i)] == -1)
								weightSum += 1;
							if (tmpWeightBoard[tmpX + 1 + tmpShape.x(i)][tmpY - tmpShape.y(i)] == -1)
								weightSum += 1;
							if (tmpWeightBoard[tmpX + tmpShape.x(i)][tmpY - 1 - tmpShape.y(i)] == -1)
								weightSum += 1;

						} catch (Exception e) {
							weightSum += 1;
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				this.weightModel.add(new WeightModel(tmpX, tmpY, weightSum, tmpShape));

				System.out.println(" = " + weightSum + " rotation " + rotation);

			}

			// 다음 위치를 연산하기 전 이동 할 수 없으면 반복을 중단한다.
			if (!tryPieceMove(gameBoard.curPiece, tmpX + 1, gameBoard.curY))
				break;

			++tmpX;
		}

		gameBoard.repaint();

		thinkEnd = true;

	}

	// 각 좌표에 대한 테트리미노의 가중치를 연산한다.
	public void checkBoardWeight(Graphics g, int[][] weightBoard) {

		this.weightModel.clear();

		thinkEnd = false;
		decisionEnd = false;

		// 원본 테트리미노의 형태와 좌표값을 받아 가상의 블럭을 생성한다.
		Shape tmpShape = gameBoard.curPiece;
		int tmpX = 0;
		int tmpY = gameBoard.curY;

		// 현재 좌표 기준 좌/우의 블럭 배치시 가중치를 연산한다.
		while (tmpX < gameBoard.BoardWidth) {

			// 가상 블럭이 현재 좌표 기준 좌측일때의 가중치 연산
			getTetriminoWeight(g, tmpShape, tmpX, weightBoard);

			tmpX++;

		}

		gameBoard.repaint();

		thinkEnd = true;

	}

	private void getTetriminoWeight(Graphics g, Shape shape, int x, int[][] boardWeight) {

		tmpShape = null;

		// 임시 블럭을 복사한다.
		try {
			tmpShape = (Shape) shape.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//g.setColor(new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
		for (int rotate = 0; rotate < 4; rotate++) {
			
			Color color = Color.BLACK;
			
			switch (rotate) {
			case 0:
				color = Color.RED;
				break;
			case 1:
				color = Color.BLUE;
				break;
			case 2:
				color = Color.GREEN;
				break;
			case 3:
				color = Color.YELLOW;
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
			tmpX = x;
			tmpY = gameBoard.curY;

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

			if (tmpShape.getShape() != Tetrominoes.NoShape) {
				for (int i = 0; i < 4; ++i) {

					int drawX = tmpX + tmpShape.x(i);
					int drawY = tmpY - tmpShape.y(i);

					g.setColor(g.getColor().darker());
					g.fillRect((10 + drawX * neo.squareWidth()) + 1, ((gameBoard.BoardHeight - drawY - 1) * neo.squareHeight()) + 1, neo.squareWidth() - 2, neo.squareHeight() - 2);
					
					int sideX = (10 + drawX * neo.squareWidth());
					int sideY = ((gameBoard.BoardHeight - drawY - 1) * neo.squareHeight());
					
					g.setColor(color);
					g.drawLine(sideX, sideY + neo.squareHeight() - 1, sideX, sideY);
					g.drawLine(sideX, sideY, sideX + neo.squareWidth() - 1, sideY);

					g.setColor(color);
					g.drawLine(sideX + 1, sideY + neo.squareHeight() - 1, sideX + neo.squareWidth() - 1, sideY + neo.squareHeight() - 1);
					g.drawLine(sideX + neo.squareWidth() - 1, sideY + neo.squareHeight() - 1, sideX + neo.squareWidth() - 1, sideY + 1);
					
				}
			}

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

				}
			});

			// 모든 셀의 가중치 합을 구한다.
			System.out.print(tmpX + " : ");
			try {

				// 블럭이 놓여진 위치의 가중치의 합산값을 구한다.
				for (int i = 0; i < 4; i++) {
					System.out.print(boardWeight[tmpX + tmpShape.x(i)][tmpY - tmpShape.y(i)] + " ");
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
				weightSum += removeLines * 100;

				// 블럭이 놓여 졌다고 가정할 때 놓여진 블럭이 빈공간을 만드는 갯수 만큼 가중치를 낮춘다.
				int voidCellBelowBlock = 0;
				int voidCellMadeBlock = 0;
				for (int i = 0; i < 4; i++) {

					int belowBlocks = 0;

					for (int h = tmpY - tmpShape.y(i) -1; h >= 0; --h) {

						if (belowBlocks > 2)
							break;
						
						if (tmpWeightBoard[tmpX + tmpShape.x(i)][h] != -1)
							voidCellBelowBlock++;
						else
							break;
						
						belowBlocks++;

					}
					
					weightSum -= voidCellBelowBlock * 70;
					if (voidCellBelowBlock > 0)
						voidCellMadeBlock++;
					System.out.println("Void Cell Vount " + voidCellBelowBlock);
				}

				weightSum -= voidCellMadeBlock *= 60;

				/*
				 * int voidCellCount = 0; //블럭이 놓여졌다고 가정할 때 빈공간의 개수가 많을수록 가중치를 낮춘다. for(int
				 * cellY = 0; cellY < gameBoard.BoardHeight ; cellY++) { for(int cellX = 0;
				 * cellX < gameBoard.BoardWidth; cellX++) { if(tmpWeightBoard[cellX][cellY] !=
				 * -1) voidCellCount++; } }
				 * 
				 * weightSum -= voidCellCount*8;
				 */

			} catch (Exception e) {
				e.printStackTrace();
			}

			int drawX = tmpX;
			int drawY = tmpY;

			Font tmpFont = g.getFont();
			Font small = new Font("Helvetica", Font.BOLD, 14);
			//FontMetrics metr = getFontMetrics(small);
			g.setFont(small);
			g.setColor(Color.BLACK);
			g.drawString(String.valueOf(weightSum), (10 + drawX * neo.squareWidth()) + 1, ((gameBoard.BoardHeight - drawY - 1) * neo.squareHeight()) + 1);
			
			g.setFont(tmpFont);
			//g.fillRect(, , neo.squareWidth() - 2, neo.squareHeight() - 2);
			
			
			// 현재 구해진 가중치값을 가중치모델에 추가한다.
			try {
				
				int mostHigherWeight = 0;
				
				for(WeightModel model:this.weightModel) {
					if(model.getWeight() > mostHigherWeight)
						mostHigherWeight = model.getWeight();
				}
				
				if(mostHigherWeight < weightSum)
					this.weightModel.add(new WeightModel(tmpX, gameBoard.curY, weightSum, (Shape) tmpShape.clone()));
				
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			System.out.println(" = " + weightSum + " rotation " + rotate);
			System.out.println("removeLines = " + removeLines);

		}

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
			}
		}

		// 최고 가중치 형태로 블럭을 회전한다.
		// 최고 가중치의 Shape 를 받아온다.
		try {
			gameBoard.curPiece = (Shape) weightModel.get(higherIndex).getShape().clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			weightModel.clear();
			moveEnd = false;
			e.printStackTrace();
			return false;
		}

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
		moveEnd = false;

		return true;
	}

}
