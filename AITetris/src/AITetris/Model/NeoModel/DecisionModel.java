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
	
	public void checkBoard(int[][] weightModel, int BoardWidth) {

		this.weightModel.clear();
		
		thinkEnd = false;
		decisionEnd = false;

		int newX = gameBoard.curX;
		
		while (newX > 0) {
			if (!gameBoard.tryMove(gameBoard.curPiece, newX - 1, gameBoard.curY))
				break;
			--newX;
		}

		while (newX <= BoardWidth) {
			
			int tmpY = gameBoard.curY;
			
			Shape tmpShape = new Shape();
			tmpShape.setShape(gameBoard.curPiece.getShape());
			
			
			
			//회전에 따라 구현한다
			for(int rotation=0; rotation<4; rotation++) {
				
				//gameBoard.tryMove(gameBoard.curPiece.rotateRight(), gameBoard.curX, gameBoard.curY);
				tmpShape = tmpShape.rotateRight();
				
				//블럭을 최하단으로 내린다고 가정한다.
				while (tmpY > 0) {
				    if (!tryPieceMove(tmpShape, newX, tmpY - 1))
				    	break;
				    --tmpY;
				}
				
				int weightSum = 0;

				System.out.print(newX + " : ");
				try {
					for (int i = 0; i < 4; i++) {
						System.out.print(weightModel[newX + tmpShape.x(i)][tmpY + tmpShape.y(i)] + " ");
						weightSum += weightModel[newX + tmpShape.x(i)][tmpY + tmpShape.y(i)];
					}
						
				}catch (Exception e) {
					
				}
				
				this.weightModel.add(new WeightModel(newX, tmpY, weightSum, tmpShape));
				
				System.out.println(" = " + weightSum + " rotation " + rotation);
				
			}
			
			//다음 위치를 연산하기 전 이동 할 수 없으면 반복을 중단한다.
			if (!tryPieceMove(gameBoard.curPiece, newX + 1, gameBoard.curY))
				break;
			
			newX++;
		}

		gameBoard.repaint();
		
		thinkEnd = true;

	}

	public boolean decision() {
		
		moveEnd = true;

		int higherWeight = 0;
		int higherIndex = 0;
		
		//최고 가중치 모델을 찾는다
		for(int i=0; i<weightModel.size(); i++) {
			if(higherWeight < weightModel.get(i).getWeight()) {
				higherWeight = weightModel.get(i).getWeight();
				higherIndex = i;
			}
			
		}
		
		//최고 가중치 형태로 블럭을 회전한다.
		gameBoard.curPiece = weightModel.get(higherIndex).getShape();
		/*try {
			for(int i=weightModel.get(higherIndex).getRotation(); i>=0; --i)
				gameBoard.curPiece = gameBoard.curPiece.rotateLeft();
				//gameBoard.tryMove(gameBoard.curPiece.rotateLeft(), gameBoard.curX, gameBoard.curY);
		}catch (Exception e) {
			
		}*/
		
		while(true) {
			
			System.out.println("current X : " + gameBoard.curX);
			System.out.println("Higher X : " + higherIndex);
			System.out.println("Higher Data : " + higherIndex + " " + weightModel.get(higherIndex).getWeight());
			
			if(gameBoard.curX > weightModel.get(higherIndex).getX()) {
				System.out.println("MOVE LEFT");
				if(!controlModel.moveLeft())
					break;
				
			}else if(gameBoard.curX < weightModel.get(higherIndex).getX()) {
				System.out.println("MOVE RIGHT");
				if(!controlModel.moveRight())
					break;
			}else {
				break;
			}
			
		}
		
		while(true) {
			if(!controlModel.moveDown())
				break;
		}

		weightModel.clear();
		
		return true;
	}

}
