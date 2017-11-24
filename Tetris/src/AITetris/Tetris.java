package AITetris;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import AITetris.Model.Neo;
import AITetris.Model.Properties;
import AITetris.Util.RankDBController;
import AITetris.View.Player;
import AITetris.View.PlayerMode;
import AITetris.View.Board.GameBoard;
import AITetris.View.Board.GameController;
import AITetris.View.Board.KeyHandler;
import AITetris.View.Option.OptionPanel;
import AITetris.View.Ranking.RankingPanel;
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
	private Properties properties = null;
	private RankDBController rankDBController = null;

	public Tetris() {

		super();

		properties = new Properties();
		rankDBController = new RankDBController();

		setTitle("AITetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(5, 5, 1330, 680);
		setLocationByPlatform(true);
		setFocusable(false);
		setResizable(false);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getContentPane().setLayout(null);

		initTitle();

		show();

	}

	private void clearContentPanel() {
		getContentPane().removeAll();
		getContentPane().repaint();
	}

	public void initTitle() {

		clearContentPanel();

		title = new Title(this);

		title.setBounds(20, 20, 1270, 600);

		getContentPane().add(title);

		title.requestFocus();

		getContentPane().repaint();

	}

	public void initSingle() {

		title.clearPanel();

		PlayerMode playMode = PlayerMode.Single;

		GameBoard board = new GameBoard(this, false, playMode, Player.Player1,
				(int) (getSize().getWidth() / 2) - (B_WIDTH_SIZE / 2), 20, B_WIDTH_SIZE, B_HEIGHT_SIZE);

		KeyHandler keyHandler = new KeyHandler(this, playMode, board, null);

		board.addKeyListener(keyHandler);

		getContentPane().add(board);

		board.requestFocus();
	}

	public void initDuo(boolean competitionMode) {

		title.clearPanel();

		PlayerMode playMode = PlayerMode.Duo;

		GameBoard board = new GameBoard(this, competitionMode, playMode, Player.Player1, 20 + B_WIDTH_SIZE / 2, 20,
				B_WIDTH_SIZE, B_HEIGHT_SIZE);
		GameBoard board2 = new GameBoard(this, competitionMode, playMode, Player.Player2,
				20 + B_WIDTH_SIZE / 2 + B_WIDTH_SIZE + 20, 20, B_WIDTH_SIZE, B_HEIGHT_SIZE);

		KeyHandler keyHandler = new KeyHandler(this, playMode, board, board2);

		board.addKeyListener(keyHandler);
		board2.addKeyListener(keyHandler);

		new GameController(board, board2).start();

		getContentPane().add(board);
		getContentPane().add(board2);

		board.requestFocus();
		board2.requestFocus();
	}

	public void initNeo(boolean competitionMode) {

		title.clearPanel();

		PlayerMode playMode = PlayerMode.AI;

		GameBoard board = new GameBoard(this, competitionMode, playMode, Player.Player1, 20, 20, B_WIDTH_SIZE,
				B_HEIGHT_SIZE);
		GameBoard board2 = new GameBoard(this, competitionMode, playMode, Player.Neo, 20 + B_WIDTH_SIZE + 20, 20,
				B_WIDTH_SIZE, B_HEIGHT_SIZE);

		KeyHandler keyHandler = new KeyHandler(this, playMode, board, board2);

		board.addKeyListener(keyHandler);
		board2.addKeyListener(keyHandler);

		new GameController(board, board2).start();

		getContentPane().add(board);
		getContentPane().add(board2);

		board.requestFocus();
		board2.requestFocus();

		Neo neo = new Neo(properties, board2);
		neo.setBounds(20 * 3 + B_WIDTH_SIZE * 2, 20, (int) getSize().getWidth() - B_WIDTH_SIZE * 2 - 90, B_HEIGHT_SIZE);
		getContentPane().add(neo);

	}

	public void initOption() {

		title.clearPanel();

		OptionPanel optionPanel = new OptionPanel(this);

		optionPanel.setBounds(20, 20, 1270, 600);

		getContentPane().add(optionPanel);

		optionPanel.requestFocus();

	}

	public void initRanking() {

		title.clearPanel();

		RankingPanel rankingPanel = new RankingPanel(this);

		rankingPanel.setBounds(20, 20, 1270, 600);

		getContentPane().add(rankingPanel);

		rankingPanel.requestFocus();

	}

	/**
	 * 속성 모델을 반환한다.
	 * 
	 * @return {@link Properties}
	 */
	public Properties getProperties() {
		return properties;
	}

	public RankDBController getDB() {
		return rankDBController;
	}

	public static void main(String[] args) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					new Tetris();
				}
			});
		} catch (Exception e) {

		}

	}

}
