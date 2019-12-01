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

    private GameBoard gameBoard;	//게임 데이터와 연동하기 위한 객체

    private ControlModel controlModel;	//조작 데이터와 연동하기 위한 객체
    
    public boolean moveEnd = false;	//이동이 끝났는지 확인하기위한 변수
    
    private WeightModel selectedModel = null;	//선택된 가중치 모델을 저장하기 위한 객체

    public DecisionModel(GameBoard gameBoard, ControlModel controlModel) {
	this.gameBoard = gameBoard;
	this.controlModel = controlModel;

	System.out.println("결정 모델 초기화");
	
	clear();
    }

    public void clear() {
	moveEnd = false;
    }

    //결정 메소드
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

	//전달 된 모델이 없으면 동작하지 않는다
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

	// Board를 벗어나는 검산 오류를 수정한다. (오류 수정)
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

	// NEO 가 블록 이동하는 속도를 결정한다
	int moveDelay = 50;
	
	new Timer(moveDelay, new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		if (gameBoard.curX > selectedModel.getX()) {	//목표 X 좌표에 도달하도록 왼쪽으로 이동한다.
		    if (!controlModel.moveLeft()) {
			Timer t = (Timer) e.getSource();
			t.stop();
		    }

		} else if (gameBoard.curX < selectedModel.getX()) { //목표 X 좌표에 도달하도록 오른쪽으로 이동한다.
		    if (!controlModel.moveRight()) {
			Timer t = (Timer) e.getSource();
			t.stop();
		    }

		} else {

		    if (!controlModel.moveDown()) {	//목표 X 좌표에 도달하면 하강한 후 중지한다.
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
