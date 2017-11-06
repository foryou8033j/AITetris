package AITetris.Model.NeoModel;

import AITetris.View.Board.GameBoard;

public class ControlModel {

    private GameBoard gameBoard;
    
    public ControlModel(GameBoard gameBoard) {
	this.gameBoard = gameBoard;
	
    }
    
    public void moveLeft() {
	gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX-1, gameBoard.curY);
    }
    
    public void moveRight() {
	gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX+1, gameBoard.curY);
    }
    
    public void moveDown() {
	gameBoard.tryMove(gameBoard.curPiece, gameBoard.curX, gameBoard.curY-1);
    }
    
    public void moveHardDown() {
	gameBoard.dropDown();
    }
    
}
