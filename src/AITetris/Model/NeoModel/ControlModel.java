package AITetris.Model.NeoModel;

import AITetris.View.Board.GameBoard;

/**
 * 원격 컨트롤 모델
 * @author Jeongsam
 *
 */
public class ControlModel {

    private GameBoard gameBoard;
    
    public ControlModel(GameBoard gameBoard) {
	this.gameBoard = gameBoard;
	
	System.out.println("조작 모델 초기화");
    }
    
    public boolean  moveLeft() {
    	return gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX - 1, gameBoard.curY);
    }
    
    public boolean moveRight() {
    	return gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX + 1, gameBoard.curY);
    }
    
    public boolean moveDown() {
    	return gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX, gameBoard.curY-1);
    }
    
    public boolean doRotate() {
	return gameBoard.tryMove(gameBoard.curPiece.rotateRight(), gameBoard.curX, gameBoard.curY);
    }
    
    public void moveHardDown() {
	gameBoard.dropDown();
    }
    
}
