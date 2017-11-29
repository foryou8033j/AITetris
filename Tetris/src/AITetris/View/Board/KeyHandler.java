package AITetris.View.Board;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import AITetris.Tetris;
import AITetris.View.Player;
import AITetris.View.PlayerMode;

/**
 * 테르리스보드의 키 입력을 관리한다
 * @author Jeongsam
 *
 */
public class KeyHandler extends KeyAdapter {

    private Tetris tetris;

    private GameBoard leftBoard;
    private GameBoard rightBoard;

    private PlayerMode playMode;

    public KeyHandler(Tetris tetris, PlayerMode playMode, GameBoard leftBoard, GameBoard rightBoard) {

	this.tetris = tetris;

	this.leftBoard = leftBoard;
	this.rightBoard = rightBoard;

	this.playMode = playMode;
    }

    private void showTitlePanel(Player player) {

	if (!leftBoard.isStarted || leftBoard.isPaused || leftBoard.isOver) {
	    tetris.initTitle();
	}
    }

    private void reset(Player player) {
	if (player.equals(Player.Player1))
	    leftBoard.reset();
	if (player.equals(Player.Player2) || player.equals(Player.Neo))
	    rightBoard.reset();
    }

    private void pause(Player player) {
	if (player.equals(Player.Player1))
	    leftBoard.pause();
	if (player.equals(Player.Player2) || player.equals(Player.Neo))
	    rightBoard.pause();
    }

    private void rotatePeace(Player player) {

	if (player.equals(Player.Player1)) {
	    leftBoard.tryRotate();
	}
	if (player.equals(Player.Player2)) {
	    rightBoard.tryRotate();
	}

    }

    private void moveLeft(Player player) {

	if (player.equals(Player.Player1)) {
	    if (leftBoard.isCrazyKeyboard)
		leftBoard.tryMove(leftBoard.curPiece, leftBoard.curX + 1, leftBoard.curY);
	    else
		leftBoard.tryMove(leftBoard.curPiece, leftBoard.curX - 1, leftBoard.curY);
	}

	if (player.equals(Player.Player2)) {
	    if (rightBoard.isCrazyKeyboard)
		rightBoard.tryMove(rightBoard.curPiece, rightBoard.curX + 1, rightBoard.curY);
	    else
		rightBoard.tryMove(rightBoard.curPiece, rightBoard.curX - 1, rightBoard.curY);
	}

    }

    private void moveRight(Player player) {
	if (player.equals(Player.Player1)) {
	    if (leftBoard.isCrazyKeyboard)
		leftBoard.tryMove(leftBoard.curPiece, leftBoard.curX - 1, leftBoard.curY);
	    else
		leftBoard.tryMove(leftBoard.curPiece, leftBoard.curX + 1, leftBoard.curY);
	}

	if (player.equals(Player.Player2)) {
	    if (rightBoard.isCrazyKeyboard)
		rightBoard.tryMove(rightBoard.curPiece, rightBoard.curX - 1, rightBoard.curY);
	    else
		rightBoard.tryMove(rightBoard.curPiece, rightBoard.curX + 1, rightBoard.curY);
	}

    }

    private void moveDown(Player player) {

	if (player.equals(Player.Player1)) {
	    if (leftBoard.isCrazyKeyboard)
		leftBoard.dropDown();
	    else
		leftBoard.tryMove(leftBoard.curPiece, leftBoard.curX, leftBoard.curY - 1);
	}

	if (player.equals(Player.Player2)) {
	    if (rightBoard.isCrazyKeyboard)
		rightBoard.dropDown();
	    else
		rightBoard.tryMove(rightBoard.curPiece, rightBoard.curX, rightBoard.curY - 1);
	}

    }

    private void moveHardDown(Player player) {

	if (player.equals(Player.Player1)) {
	    leftBoard.dropDown();
	}

	if (player.equals(Player.Player2)) {
	    rightBoard.dropDown();
	}

    }

    private void exchangePeace(Player player) {

	if (player.equals(Player.Player1)) {
	    leftBoard.exchangePiece();
	}
	if (player.equals(Player.Player2)) {
	    rightBoard.exchangePiece();
	}

    }

    private void ghostMode(Player player) {

	if (playMode.equals(PlayerMode.Single)) {

	    if (leftBoard.isGhost) {
		leftBoard.isGhost = false;
	    } else {
		leftBoard.ghostUsed += 300;
		leftBoard.isGhost = true;
	    }

	    return;
	} else if (leftBoard.isOver)
	    return;

	if (leftBoard.isOver || rightBoard.isOver)
	    return;

	if (player.equals(Player.Player1)) {
	    if (leftBoard.isGhost) {
		leftBoard.isGhost = false;
	    } else {
		leftBoard.ghostUsed += 200;
		leftBoard.isGhost = true;
	    }
	}

	if (player.equals(Player.Player2)) {
	    if (rightBoard.isGhost) {
		rightBoard.isGhost = false;
	    } else {
		rightBoard.ghostUsed += 200;
		rightBoard.isGhost = true;
	    }
	}

    }

    //랭킹 등록을 시도한다
    private void rank() {

	//1인용 모드일때는 승패 여부가 없으므로 랭킹 등록을 시도한다.
	if (playMode.equals(PlayerMode.Single) && leftBoard.isOver) {
	    leftBoard.joinRank();
	    return;
	}

	
	//2인용, 인공지능 모드 일때는 승리자만 랭킹 등록이 가능하다
	if (!playMode.equals(PlayerMode.Single) && (leftBoard.isOver || rightBoard.isOver)) {

	    if (leftBoard.isWin)
		leftBoard.joinRank();
	    else if (!rightBoard.player.equals(Player.Neo) && rightBoard.isWin)
		rightBoard.joinRank();

	}

    }

    public void keyPressed(KeyEvent e) {

	//1인용 또는 인공지능 모드일 때의 키입력을 관리한다
	if (playMode.equals(PlayerMode.Single) || playMode.equals(PlayerMode.AI)) {
	    switch (e.getKeyCode()) {

	    case KeyEvent.VK_ENTER:
		reset(Player.Player1);

		if (playMode.equals(PlayerMode.AI))
		    reset(Player.Neo);

		break;

	    case KeyEvent.VK_ESCAPE:
	    case KeyEvent.VK_P:
		pause(Player.Player1);

		if (playMode.equals(PlayerMode.AI))
		    pause(Player.Neo);

		break;

	    case KeyEvent.VK_UP:
		rotatePeace(Player.Player1);
		break;
	    case KeyEvent.VK_DOWN:
		moveDown(Player.Player1);
		break;

	    case KeyEvent.VK_LEFT:
		moveLeft(Player.Player1);
		break;
	    case KeyEvent.VK_RIGHT:
		moveRight(Player.Player1);
		break;

	    case KeyEvent.VK_SHIFT:
		exchangePeace(Player.Player1);
		break;

	    case KeyEvent.VK_SPACE:
		moveHardDown(Player.Player1);
		break;

	    case KeyEvent.VK_G:
		ghostMode(Player.Player1);
		rank();
		break;

	    case KeyEvent.VK_Q:
		showTitlePanel(Player.Player1);
		break;

	    }
	}

	// 2인용일때의 키 입력을 관리한다
	if (playMode.equals(PlayerMode.Duo)) {

	    switch (e.getKeyCode()) {

	    // 공통 키 세팅
	    case KeyEvent.VK_ENTER:
		reset(Player.Player1);
		reset(Player.Player2);
		break;

	    case KeyEvent.VK_ESCAPE:
	    case KeyEvent.VK_P:
		pause(Player.Player1);
		pause(Player.Player2);
		break;

	    // 1p 키 세팅
	    case KeyEvent.VK_I:
		rotatePeace(Player.Player1);
		break;
	    case KeyEvent.VK_J:
		moveLeft(Player.Player1);
		break;
	    case KeyEvent.VK_K:
		moveDown(Player.Player1);
		break;
	    case KeyEvent.VK_L:
		moveRight(Player.Player1);
		break;
	    case KeyEvent.VK_SPACE:
		moveHardDown(Player.Player1);
		break;
	    case KeyEvent.VK_SHIFT:
		exchangePeace(Player.Player1);
		break;
	    case KeyEvent.VK_G:
		ghostMode(Player.Player1);
		rank();
		break;

	    // 2p 키 세팅

	    case KeyEvent.VK_NUMPAD8:
		rotatePeace(Player.Player2);
		break;
	    case KeyEvent.VK_NUMPAD4:
		moveLeft(Player.Player2);
		break;
	    case KeyEvent.VK_NUMPAD5:
		moveDown(Player.Player2);
		break;
	    case KeyEvent.VK_NUMPAD6:
		moveRight(Player.Player2);
		break;

	    case KeyEvent.VK_DOWN:
		moveHardDown(Player.Player2);
		break;
	    case KeyEvent.VK_UP:
		exchangePeace(Player.Player2);
		break;
	    case KeyEvent.VK_BACK_SPACE:
		ghostMode(Player.Player2);
		break;

	    case KeyEvent.VK_Q:
		showTitlePanel(Player.Player1);
		break;

	    }

	}

    }

}
