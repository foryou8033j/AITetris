package AITetris;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import AITetris.View.Player;
import AITetris.View.PlayerMode;
import AITetris.View.Board.GameBoard;
import AITetris.View.Board.KeyHandler;
import AITetris.View.Title.Title;

/**
 * 테트리스의 기본 프레임을 띄워준다.
 * 
 * @author Jeongsam
 *
 */
public class Tetris extends JFrame {

    final int B_WIDTH_SIZE = 400;
    final int B_HEIGHT_SIZE = 600;
    
    private Title title;

    public Tetris() {

	super();
	setTitle("AITetris");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(20, 20, 1330, 680);
	setLocationByPlatform(true);
	setFocusable(false);

	getContentPane().setLayout(null);

	initTitle();

	show();
    }

    public void initTitle() {

	title = new Title(this);
	title.setBounds(20, 20, 1270, 600);

	getContentPane().add(title);

	title.requestFocus();

    }

    public void initSingle() {

	title.clearPanel();

	PlayerMode playMode = PlayerMode.Single;

	GameBoard board = new GameBoard(playMode, Player.Player1, (int) (getSize().getWidth()/2) - (B_WIDTH_SIZE/2), 20, B_WIDTH_SIZE, B_HEIGHT_SIZE);

	KeyHandler keyHandler = new KeyHandler(playMode, board, null);

	board.addKeyListener(keyHandler);

	getContentPane().add(board);

	board.requestFocus();
    }

    public void initDuo() {

	title.clearPanel();

	PlayerMode playMode = PlayerMode.Duo;

	GameBoard board = new GameBoard(playMode, Player.Player1, 20, 20, B_WIDTH_SIZE, B_HEIGHT_SIZE);
	GameBoard board2 = new GameBoard(playMode, Player.Player2, 20 + B_WIDTH_SIZE + 20, 20, B_WIDTH_SIZE, B_HEIGHT_SIZE);

	KeyHandler keyHandler = new KeyHandler(playMode, board, board2);

	board.addKeyListener(keyHandler);
	board2.addKeyListener(keyHandler);

	getContentPane().add(board);
	getContentPane().add(board2);

	board.requestFocus();
	board2.requestFocus();
    }

    public void initNeo() {

	title.clearPanel();

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
