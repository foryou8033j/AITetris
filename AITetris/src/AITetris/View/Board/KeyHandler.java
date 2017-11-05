package AITetris.View.Board;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import AITetris.View.Player;
import AITetris.View.PlayerMode;

public class KeyHandler extends KeyAdapter {


    private GameBoard board;
    private GameBoard board2;

    private PlayerMode playMode;

    public KeyHandler(PlayerMode playMode, GameBoard gameBoard, GameBoard gameBoard2) {
	this.board = gameBoard;
	this.board2 = gameBoard2;

	this.playMode = playMode;
    }

    private void reset(Player player) {
	if (player.equals(Player.Player1))
	    board.reset();
	if (player.equals(Player.Player2))
	    board2.reset();
    }

    private void pause(Player player) {
	if (player.equals(Player.Player1))
	    board.pause();
	if (player.equals(Player.Player2))
	    board2.pause();
    }

    private void rotatePeace(Player player) {

	if (player.equals(Player.Player1)) {
	    board.tryMove(board.curPiece.rotateRight(), board.curX, board.curY);
	    if (board.isGhost)
		board.tryGhostPieceMove(board.ghostPiece.rotateRight(), board.ghostCurX, board.ghostCurY);
	}
	if (player.equals(Player.Player2)) {
	    board2.tryMove(board2.curPiece.rotateRight(), board2.curX, board2.curY);
	    if (board2.isGhost)
		board2.tryGhostPieceMove(board2.ghostPiece.rotateRight(), board2.ghostCurX, board2.ghostCurY);
	}

    }

    private void moveLeft(Player player) {

	if (player.equals(Player.Player1))
	    board.tryMove(board.curPiece, board.curX - 1, board.curY);
	if (player.equals(Player.Player2))
	    board2.tryMove(board2.curPiece, board2.curX - 1, board2.curY);

    }

    private void moveRight(Player player) {
	if (player.equals(Player.Player1))
	    board.tryMove(board.curPiece, board.curX + 1, board.curY);
	if (player.equals(Player.Player2))
	    board2.tryMove(board2.curPiece, board2.curX + 1, board2.curY);

    }

    private void moveDown(Player player) {

	if (player.equals(Player.Player1))
	    board.tryMove(board.curPiece, board.curX, board.curY - 1);
	if (player.equals(Player.Player2))
	    board2.tryMove(board2.curPiece, board2.curX, board2.curY - 1);

    }

    private void moveHardDown(Player player) {

	if (player.equals(Player.Player1))
	    board.dropDown();
	if (player.equals(Player.Player2))
	    board2.dropDown();

    }

    private void exchangePeace(Player player) {

	if (player.equals(Player.Player1))
	    board.exchangePiece();
	if (player.equals(Player.Player2))
	    board2.exchangePiece();

    }

    private void ghostMode(Player player) {
	if (player.equals(Player.Player1)) {
	    if (board.isGhost)
		board.isGhost = false;
	    else {
		board.infoBoard.ghostUsed += 300;
		board.isGhost = true;
	    }
	}

	if (player.equals(Player.Player2)) {
	    if (board2.isGhost)
		board2.isGhost = false;
	    else {
		board2.infoBoard.ghostUsed += 300;
		board2.isGhost = true;
	    }
	}

    }

    public void keyPressed(KeyEvent e) {

	System.out.println(e.getKeyCode());

	if (playMode.equals(PlayerMode.Single)) {
	    switch (e.getKeyCode()) {

	    case KeyEvent.VK_ENTER:
		reset(Player.Player1);
		break;

	    case KeyEvent.VK_ESCAPE:
	    case KeyEvent.VK_P:
		pause(Player.Player1);
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
		break;

	    }
	}

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

	    }

	}

    }

}
