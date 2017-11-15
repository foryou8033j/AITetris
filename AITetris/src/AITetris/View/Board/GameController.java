package AITetris.View.Board;

import java.awt.AWTException;
import java.awt.Robot;

public class GameController extends Thread{

	private GameBoard gameBoard1;
	private GameBoard gameBoard2;
	
	public GameController(GameBoard gameBoard1, GameBoard gameBoard2) {
		
		this.gameBoard1 = gameBoard1;
		this.gameBoard2 = gameBoard2;
		
		
	}
	
	@Override
	public void run() {
		super.run();
		
		while(true) {
			
			try {
				new Robot().delay(50);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(gameBoard1.isOver || gameBoard2.isOver) {
				
				if(gameBoard1.getPoint() > gameBoard2.getPoint()) {
					gameBoard1.isWin = true;
				}else {
					gameBoard2.isWin = true;
				}
				gameBoard1.doQuitGame();
				gameBoard2.doQuitGame();
				break;
			}
			
			
		}
		
	}
	
}
