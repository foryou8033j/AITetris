package AITetris.View.Board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import AITetris.View.Board.Tetrimino.Shape;
import AITetris.View.Board.Tetrimino.Tetrominoes;

/**
 * 현재 진행중인 테트리스의 정보를 보여주는 패널
 * 
 * @author Jeongsam
 *
 */
public class InfoBoard extends JPanel {

    final int BoardWidth = 5;
    final int BoardHeight = 30;

    public Shape nextPiece;
    public Shape tempPiece;

    int nextPieceX = 0;
    int nextPieceY = 0;

    int tempPieceX = 0;
    int tempPieceY = 0;

    int squareWidth() {
	return (int) getSize().getWidth() / BoardWidth;
    }

    int squareHeight() {
	return (int) getSize().getHeight() / BoardHeight;
    }

    public int point = 0;
    public int ghostUsed = 0;
    public boolean isGhost = false;

    public long leftTime;

    public InfoBoard(GameBoard gameBoard) {

	leftTime = 0;
	nextPiece = new Shape();
	tempPiece = new Shape();

	nextPieceX = BoardWidth / 2;
	nextPieceY = BoardHeight - 1 + nextPiece.minY() - 3;

	tempPieceX = BoardWidth / 2;
	tempPieceY = BoardHeight - 1 + nextPiece.minY() - 10;

	// setBorder(new BevelBorder(BevelBorder.RAISED));
	setBorder(BorderFactory.createLineBorder(Color.black));

	newPiece();
    }

    public void cleanPieces() {
	point = 0;
	ghostUsed = 0;

	nextPiece.setShape(Tetrominoes.NoShape);
	tempPiece.setShape(Tetrominoes.NoShape);
	newPiece();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        drawHeadText(g);
        drawPeaces(g);
        
	g.setColor(Color.BLACK);
	g.drawLine(0, getSize().height / 2, getSize().width, getSize().height / 2);

    }
    
    /**
     * 인포 메세지를 그린다.
     * @param g		그래픽스 모델
     */
    private void drawHeadText(Graphics g) {
	Font small = new Font("Helvetica", Font.BOLD, 20);
	FontMetrics metr = getFontMetrics(small);

	drawStringCenterOfPanel(g, Color.BLACK, 14, "NEXT", metr.getHeight());
	
	drawStringCenterOfPanel(g, Color.BLACK, 14, "TEMP", getSize().height/3 - metr.getHeight());
	
	
	drawStringCenterOfPanel(g, Color.BLACK, 14, "Points", getSize().height/2 + metr.getHeight());
	drawStringCenterOfPanel(g, Color.BLACK, 14, String.valueOf(point), getSize().height/2 + metr.getHeight()*2);

	drawStringCenterOfPanel(g, Color.BLACK, 14, "Penalty", getSize().height/2 +getSize().height/6);
	drawStringCenterOfPanel(g, Color.RED, 14, String.valueOf(ghostUsed), getSize().height/2 +getSize().height/6 + metr.getHeight());
	
	drawStringCenterOfPanel(g, Color.BLACK, 14, "GHOST", getSize().height/2 +getSize().height/3);
	drawStringCenterOfPanel(g, Color.BLACK, 14, "MODE", getSize().height/2 +getSize().height/3 + metr.getHeight());
	
	if (isGhost)
	    drawStringCenterOfPanel(g, Color.RED, 12, "ON", getSize().height/2 + getSize().height/3 + metr.getHeight() * 2);
	else
	    drawStringCenterOfPanel(g, Color.BLUE, 12, "OFF", getSize().height/2 + getSize().height/3 + metr.getHeight()*2);
	    
    }
    
    /**
     * 테트리미노를 그린다
     * @param g
     */
    private void drawPeaces(Graphics g) {
	
	//그려질 영역 정의
	Dimension size = getSize();
	int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

	//다음 피스 그리기
	if (nextPiece.getShape() != Tetrominoes.NoShape) {
	    for (int i = 0; i < 4; ++i) {
		int x = nextPieceX + nextPiece.x(i);
		int y = nextPieceY - nextPiece.y(i);
		drawSquare(g, x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),nextPiece.getShape());
	    }
	}
	
	
	//임시 피스 그리기
	if (tempPiece.getShape() != Tetrominoes.NoShape) {
	    for (int i = 0; i < 4; ++i) {
		int x = tempPieceX + tempPiece.x(i);
		int y = tempPieceY - tempPiece.y(i);
		drawSquare(g, x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),tempPiece.getShape());
	    }
	} else {
	    drawStringCenterOfPanel(g, Color.BLACK, 12, "x", getSize().height/3);
	}
	
    }

    private void newPiece() {
	nextPiece.setRandomShape();
    }

    /**
     * 테트리미노의 블럭 규격을 받아 그린다.
     * @param g 	그래픽스 모델
     * @param x		그려질 시작 x 좌표
     * @param y		그려질 시작 y 좌표
     * @param shape	테트리미노 모델
     */
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
	Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
		new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
		new Color(218, 170, 0) };

	Color color = colors[shape.ordinal()];

	g.setColor(color);
	g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

	g.setColor(color.brighter());
	g.drawLine(x, y + squareHeight() - 1, x, y);
	g.drawLine(x, y, x + squareWidth() - 1, y);

	g.setColor(color.darker());
	g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
	g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    /**
     * 문자열을 가로 기준 중앙에 그려준다.
     * 
     * @param g
     *            그래픽스 모델
     * @param color
     *            색상
     * @param size
     *            폰트 사이즈
     * @param str
     *            문자열
     * @param height
     *            출력 높이
     */
    private void drawStringCenterOfPanel(Graphics g, Color color, int size, String str, int height) {

	Font small = new Font("Helvetica", Font.BOLD, size);
	FontMetrics metr = getFontMetrics(small);

	g.setColor(color);
	g.setFont(small);

	g.drawString(str, (int) (getSize().getWidth()) / 2 - metr.stringWidth(str) / 2, height);

    }

}
