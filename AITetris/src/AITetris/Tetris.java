package AITetris;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import AITetris.View.Player;
import AITetris.View.PlayerMode;
import AITetris.View.Board.GameBoard;
import AITetris.View.Board.KeyHandler;

/**
 * 테트리스의 기본 프레임을 띄워준다.
 * 
 * @author Jeongsam
 *
 */
public class Tetris extends JFrame {

    public Tetris() {

	super();
	setTitle("AITetris");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(20, 20, 1330, 680);

	PlayerMode playMode = PlayerMode.Duo;

	GameBoard board = new GameBoard(playMode, Player.Player1, 20, 20, 400, 600);
	GameBoard board2 = new GameBoard(playMode, Player.Player2, 440, 20, 400, 600);

	KeyHandler keyHandler = new KeyHandler(playMode, board, board2);

	board.addKeyListener(keyHandler);
	board2.addKeyListener(keyHandler);
	
	getContentPane().setLayout(null);

	switch (playMode) {
	case Single:
	    getContentPane().add(board);
	    break;

	case Duo:
	    getContentPane().add(board);
	    getContentPane().add(board2);
	    break;
	}

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
	} catch (Exception e) {

	}

    }

}
