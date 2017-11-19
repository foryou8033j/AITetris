package AITetris.Model.NeoModel;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import AITetris.Model.Neo;
import AITetris.View.Board.GameBoard;
import AITetris.View.Board.Tetrimino.Shape;
import AITetris.View.Board.Tetrimino.Tetrominoes;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 블럭 가중치에 따른 의사 결정 모델
 * 
 * @author Jeongsam
 *
 */
public class DecisionModel {

    private GameBoard gameBoard;

    private ControlModel controlModel;
    
    public boolean moveEnd = false;
    
    private WeightModel selectedModel = null;

    public DecisionModel(GameBoard gameBoard, ControlModel controlModel) {
	this.gameBoard = gameBoard;
	this.controlModel = controlModel;

	System.out.println("결정 모델 초기화");
	
	clear();
    }

    public void clear() {
	moveEnd = false;
    }

    public boolean decision(ObservableList<WeightModel> weightModel) {

	moveEnd = false;

	int higherWeight = 0;

	selectedModel = null;

	// 최고 가중치 모델을 찾는다
	for (int i = 0; i < weightModel.size(); i++) {
	    if (higherWeight <= weightModel.get(i).getWeight()) {
		higherWeight = weightModel.get(i).getWeight();
		selectedModel = weightModel.get(i);
	    }
	}

	if (weightModel.size() == 0 || selectedModel == null) {
	    weightModel.clear();
	    moveEnd = false;
	    return true;
	}

	// 최고 가중치 형태로 블럭을 회전한다.
	// 최고 가중치의 Shape 를 받아온다.
	try {
	    gameBoard.curPiece = (Shape) selectedModel.getShape().clone();
	} catch (CloneNotSupportedException e) {
	    weightModel.clear();
	    moveEnd = false;
	    e.printStackTrace();
	    return true;
	}

	// Board를 벗어나는 검산 오류를 수정한다.
	/*int j = 0;
	while (j++ < 4) {
	    for (int i = 0; i < 4; i++) {
		int x = gameBoard.curX + gameBoard.curPiece.x(i);
		// int y = gameBoard.curY - gameBoard.curPiece.y(i);

		if (x <= 0) {
		    gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX + 2, gameBoard.curY);
		} else if (x >= gameBoard.BoardWidth) {
		    gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX - 2, gameBoard.curY);
		}
	    }
	}*/

	new Timer(50, new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		if (gameBoard.curX > selectedModel.getX()) {
		    if (!controlModel.moveLeft()) {
			Timer t = (Timer) e.getSource();
			t.stop();
		    }

		} else if (gameBoard.curX < selectedModel.getX()) {
		    if (!controlModel.moveRight()) {
			Timer t = (Timer) e.getSource();
			t.stop();
		    }

		} else {

		    if (!controlModel.moveDown()) {
			Timer t = (Timer) e.getSource();
			t.stop();
			weightModel.clear();
			moveEnd = true;
		    }

		}
	    }
	}).start();

	if (moveEnd)
	    return true;

	return false;
    }

}
