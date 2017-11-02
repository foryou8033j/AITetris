package AITetris;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import AITetris.View.Board.Board;

public class Tetris extends JFrame{

	public Tetris() {
		
		super();
		setTitle("AITetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(20, 20, 400, 600);
		
		Board board = new Board();
		board.setBounds(0, 0, 200, 400);
		
		board.getHelpPanel().setBounds(200, 0, 100, 400);
		
		getContentPane().setLayout(null);
		getContentPane().add(board);
		getContentPane().add(board.getHelpPanel());
		
		show();
	}
	
	public static void main(String[] args) {
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					new Tetris();
				}
			});
		}catch (Exception e) {
			
		}
		
		
		
		
	}
	
}
