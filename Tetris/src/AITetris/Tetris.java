package AITetris;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import AITetris.Model.Neo;
import AITetris.Model.Properties;
import AITetris.Util.MusicPlayer;
import AITetris.Util.MusicPlayer.Music;
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
 * 테트리스의 기본 프레임을 띄워주고, 각 화면으로의 이동을 관리한다.
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
    private MusicPlayer musicPlayer = null;

    public Tetris() {

	super();

	properties = new Properties();
	rankDBController = new RankDBController();
	musicPlayer = new MusicPlayer(properties);
	musicPlayer.play();

	setTitle("AITetris");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(5, 5, 1330, 680);
	setLocationByPlatform(true);
	setFocusable(false);
	setResizable(false);

	//Swing 특유의 딱딱한 UI 대신 운영체제의 UI를 따르도록 한다.
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

	//Title을 보여준다.
	initTitle();

	show();

    }

    private void clearContentPanel() {
	getContentPane().removeAll();
	getContentPane().repaint();
    }

    /**
     * 타이틀 화면을 보여준다.
     */
    public void initTitle() {

	clearContentPanel();

	title = new Title(this);

	title.setBounds(20, 20, getSize().width - 60, getSize().height - 80);

	getContentPane().add(title);

	title.requestFocus();

	getContentPane().repaint();

	musicPlayer.changeMusic(Music.tetris_title);

    }

    /**
     * 1인용 플레이 화면을 보여준다.
     */
    public void initSingle() {

	title.clearPanel();

	PlayerMode playMode = PlayerMode.Single;

	GameBoard board = new GameBoard(this, false, playMode, Player.Player1,
		(int) (getSize().getWidth() / 2) - (B_WIDTH_SIZE / 2), 20, B_WIDTH_SIZE, B_HEIGHT_SIZE);

	KeyHandler keyHandler = new KeyHandler(this, playMode, board, null);

	board.addKeyListener(keyHandler);

	getContentPane().add(board);

	board.requestFocus();

	musicPlayer.changeMusic();
    }

    /**
     * 2인용 화면을 보여준다.
     * @param competitionMode	대전 모드 사용 유무
     */
    public void initDuo(boolean competitionMode) {

	title.clearPanel();

	PlayerMode playMode = PlayerMode.Duo;

	GameBoard board = new GameBoard(this, competitionMode, playMode, Player.Player1, 20 + B_WIDTH_SIZE / 2, 20,
		B_WIDTH_SIZE, B_HEIGHT_SIZE);
	GameBoard board2 = new GameBoard(this, competitionMode, playMode, Player.Player2,
		20 + B_WIDTH_SIZE / 2 + B_WIDTH_SIZE + 20, 20, B_WIDTH_SIZE, B_HEIGHT_SIZE);

	//Key 관리용 클래스
	KeyHandler keyHandler = new KeyHandler(this, playMode, board, board2);

	//KeyListener에 두개의 사용자 키를 동시에 관리 할 수 있는 KeyHandler 클래스를 연동한다.
	board.addKeyListener(keyHandler);
	board2.addKeyListener(keyHandler);

	//두개의 게임 보드의 연결점, 게임 공격, 승리판정 여부를 관리한다.
	new GameController(board, board2).start();

	getContentPane().add(board);
	getContentPane().add(board2);

	//Pane을 추가한 후에 Focus를 요청해야 KeyListener 가 동작한다.
	board.requestFocus();
	board2.requestFocus();

	//음악 변경
	musicPlayer.changeMusic();
    }

    /**
     * 인공 지능 화면을 보여준다
     * @param competitionMode	대전 모드 사용 유무
     */
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

	musicPlayer.changeMusic();

    }

    /**
     * 옵션 화면을 보여준다.
     */
    public void initOption() {

	title.clearPanel();

	OptionPanel optionPanel = new OptionPanel(this);

	optionPanel.setBounds(20, 20, getSize().width - 60, getSize().height - 80);

	getContentPane().add(optionPanel);

	optionPanel.requestFocus();

    }

    /**
     * 랭킹 화면을 보여준다.
     */
    public void initRanking() {

	title.clearPanel();

	RankingPanel rankingPanel = new RankingPanel(this);

	rankingPanel.setBounds(20, 20, getSize().width - 60, getSize().height - 80);

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

    /**
     * DB 컨트롤러를 반환한다.
     * @return {@link RankDBController}
     */
    public RankDBController getDB() {
	return rankDBController;
    }

    /**
     * 음악 플레이어를 반환한다.
     * @return {@link MusicPlayer}
     */
    public MusicPlayer getMusicPlayer() {
	return musicPlayer;
    }

    /***********************************************************************/
    
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
