package AITetris.Model.NeoModel;

import AITetris.View.Board.GameBoard;
import AITetris.View.Board.Tetrimino.Shape;

/**
 * 블럭 가중치에 따른 결정 모델
 * @author Jeongsam
 *
 */
public class DecisionModel {

    private GameBoard gameBoard;
    
    public boolean currentThinking = false;
    public boolean currentMoving = false;
    
    private int mostHigherWeightX = 0;
    private int mostHigherWeight = 0;
    private int mostHigherRotation = 0;
    
    private ControlModel controlModel;
    
    public DecisionModel(GameBoard gameBoard) {
	this.gameBoard = gameBoard;
	
	controlModel = new ControlModel(gameBoard);
    }
    
    public void clear() {
	
	System.out.println("Clear");
	
	mostHigherRotation = 0;
	mostHigherWeight = 0;
	mostHigherWeightX = 0;
	
	currentThinking = false;
	currentMoving = false;
    }
    
    public void checkBoard(int[][] weightModel, int BoardWidth) {
	
	currentThinking = true;
	
	for(int i=0; i<8; i++) {
	    
	    int tmp = checkXCord(weightModel, i);
	    
	    if(tmp > mostHigherWeight) {
		mostHigherWeight = tmp;
		mostHigherWeightX = i;
	    }
	    
	}
	
    }
    
    private int checkXCord(int[][] weightModel, int x) {
	
	int weight = 0;
	
	int newY = gameBoard.curY;
	while (newY > 0) {
	    if (!gameBoard.tryGhostPieceMove(gameBoard.curPiece, gameBoard.curX, newY - 1))
		break;
	    --newY;
	}
	
	int curX = gameBoard.ghostCurX;
	int curY = gameBoard.ghostCurY;
	
	for (int i=0; i<4; i++) {
	    weight += weightModel[x + gameBoard.curPiece.x(i)][curY + gameBoard.curPiece.y(i)];
	}
	
	System.out.println(weight);
	
	return weight;
	
    }
    
    public boolean decision() {
	
	
	
	if(gameBoard.curX < mostHigherWeightX) {
	    currentMoving = true;
	    controlModel.moveRight();
	}else if (gameBoard.curX > mostHigherWeightX){
	    currentMoving = true;
	    controlModel.moveLeft();
	}else{
	    currentMoving = false;
	}
	
	return false;
    }
    
}
