package AITetris.Model.NeoModel;

import AITetris.View.Board.GameBoard;

public class ControlModel {

    private GameBoard gameBoard;
    
    public ControlModel(GameBoard gameBoard) {
	this.gameBoard = gameBoard;
	
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
    
    public void moveHardDown() {
	gameBoard.dropDown();
    }
    
}
