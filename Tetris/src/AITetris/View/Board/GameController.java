package AITetris.View.Board;

import java.awt.AWTException;
import java.awt.Robot;

import AITetris.View.PlayerMode;

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
			
			
			
			
			//하나의 플레이어가 중단되었을 때 다른 플레이어의 게임도 중간시키고
			//승리 여부를 결정한다.
			if(gameBoard1.isOver || gameBoard2.isOver) {
				
				if(gameBoard1.getPoint() > gameBoard2.getPoint()) {
					gameBoard1.isWin = true;
				}else {
					gameBoard2.isWin = true;
				}
				gameBoard1.doQuitGame();
				gameBoard2.doQuitGame();
				break;
			}else{
				
				//게임 모드가 대전모드 일 경우
				if(gameBoard1.isCompetition()){
					
					int firstPlayerRemovedLine = gameBoard1.getNumLineRemoved();
					int secondPlayerRemovedLine = gameBoard2.getNumLineRemoved();
					
					if(firstPlayerRemovedLine > 0) {
						
						gameBoard2.attack(firstPlayerRemovedLine);
						
					}else if(secondPlayerRemovedLine > 0) {
						
						gameBoard1.attack(secondPlayerRemovedLine);
						
					}
					
				}
			}
			
			
		}
		
	}
	
}
