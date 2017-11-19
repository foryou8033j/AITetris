package AITetris.View.Title;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import AITetris.Tetris;
import AITetris.Util.MusicPlayer;
import AITetris.Util.String.StringPadding;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * 타이틀 메뉴를 그려주는 패널 클래스
 * 
 * @author Jeongsam
 *
 */
public class Title extends JPanel {

    private Tetris tetris;

    int curPoint = 0;
    int minPoint = 0;
    int maxPoint = 5;
    
    boolean duoCompetition = false;
    boolean aiCompetition = false;
    
    boolean isQuitQuestion = false;
    boolean isQuitCursorOnExit = true;

    //TitleKeyHandler keyHandler = new TitleKeyHandler();
    
    KeyAdapter keyHandler = null;

    public Title(final Tetris tetris) {
	this.tetris = tetris;

	setLayout(null);

	setBorder(BorderFactory.createLineBorder(Color.black));
	setFocusable(true);
	
	new MusicPlayer(tetris.getProperties()).play();
	

	keyHandler = new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {

		    switch (e.getKeyCode()) {

		    case KeyEvent.VK_UP:
			if (curPoint - 1 >= minPoint)
			    curPoint--;
			else
			    curPoint = maxPoint;
			break;

		    case KeyEvent.VK_DOWN:
			if (curPoint + 1 <= maxPoint)
			    curPoint++;
			else
			    curPoint = 0;
			break;
			
		    case KeyEvent.VK_LEFT:
			
			if(isQuitQuestion)
			    isQuitCursorOnExit = true;
			
			if(curPoint == 1) {
			    if(duoCompetition)
				duoCompetition = false;
			}
			if(curPoint == 2) {
			    if(aiCompetition)
				aiCompetition = false;
			}
			break;
			
		    case KeyEvent.VK_RIGHT:
			
			if(isQuitQuestion)
			    isQuitCursorOnExit = false;
			
			if(curPoint == 1) {
			    if(!duoCompetition)
				duoCompetition = true;
			}
			if(curPoint == 2) {
			    if(!aiCompetition)
				aiCompetition = true;
			}
			break;

		    case KeyEvent.VK_ENTER:
			
			if(isQuitQuestion && isQuitCursorOnExit)
			    System.exit(0);
			else if(isQuitQuestion && !isQuitCursorOnExit) {
			    isQuitQuestion = false;
			    isQuitCursorOnExit = true;
			    repaint();
			}else {
			    switch(curPoint) {
			    	case 0:
			    		tetris.initSingle();
			    		break;
			    	case 1:
			    		tetris.initDuo(duoCompetition);
			    		break;
			    	case 2:
			    		tetris.initNeo(aiCompetition);
			    		break;
			    	case 3:
			    		break;
			    	case 4:
			    	    	tetris.initOption();
			    		break;
			    	case 5:
			    		isQuitQuestion = true;
			    		break;
			    	}
			}
			
		    	
			break;
		    default:
			break;
		    }
	    }
	};
	
	addKeyListener(keyHandler);
	

    }

    public void clearPanel() {
	setFocusable(false);
	removeKeyListener(keyHandler);
	keyHandler = null;
	
	tetris.getContentPane().setLayout(null);
	tetris.getContentPane().removeAll();
	tetris.getContentPane().repaint();
	
    }


    @Override
    public void paint(Graphics g) {
	super.paint(g);

	drawTitle(g);
	
	if(isQuitQuestion)
	    drawQuitQuestionMenu(g);
	else
	    drawMenues(g);

	repaint();

    }
    
    private void drawTitle(Graphics g) {
	drawStringCenterOfPanel(g, Color.BLACK, 64, "T E T R I S", 120);
	drawStringCenterOfPanel(g, Color.BLACK, 18, "소프트웨어 분석 및 설계 - 레벨3", 160);
	drawStringCenterOfPanel(g, Color.BLACK, 16, "20140636 서정삼", 190);
    }

    private void drawQuitQuestionMenu(Graphics g) {
	Color color = Color.BLACK;
	
	drawStringCenterOfPanel(g, color, 20, StringPadding.getCPad("ARE YOU SURE?", 30, " "), 300);
	
	color = (isQuitCursorOnExit ? Color.RED : Color.BLACK);
	drawStringCenterOfPanel(g, color, 16, StringPadding.getRPad("YES", 30, " "), 360);
	color = (!isQuitCursorOnExit ? Color.RED : Color.BLACK);
	drawStringCenterOfPanel(g, color, 16, StringPadding.getLPad("NO", 30, " "), 360);
    }
    
    private void drawMenues(Graphics g) {
	Color color = Color.BLACK;

	int firstLine = 320;
	
	color = (curPoint == 0 ? Color.RED : Color.BLACK);
	drawStringCenterOfPanel(g, color, 16, StringPadding.getCPad("Single", 14, " "), firstLine);

	color = (curPoint == 1 ? Color.RED : Color.BLACK);
	if(duoCompetition)
	    drawStringCenterOfPanel(g, color, 16, StringPadding.getRPad("◀ Human VS Human Competition", 15, " "), firstLine+=40);
	else
	    drawStringCenterOfPanel(g, color, 16, StringPadding.getRPad("Human VS Human ▶", 15, " "), firstLine+=40);

	color = (curPoint == 2 ? Color.RED : Color.BLACK);
	if(aiCompetition)
	    drawStringCenterOfPanel(g, color, 16, StringPadding.getRPad("◀ Human VS Neo Competition", 15, " "), firstLine+=40);
	else
	    drawStringCenterOfPanel(g, color, 16, StringPadding.getRPad("Human VS Neo ▶", 15, " "), firstLine+=40);

	color = (curPoint == 3 ? Color.RED : Color.BLACK);
	drawStringCenterOfPanel(g, color, 16, StringPadding.getCPad("Help", 14, " "), firstLine+=40);
	
	color = (curPoint == 4 ? Color.RED : Color.BLACK);
	drawStringCenterOfPanel(g, color, 16, StringPadding.getCPad("Option", 14, " "), firstLine+=40);

	color = (curPoint == 5 ? Color.RED : Color.BLACK);
	drawStringCenterOfPanel(g, color, 16, StringPadding.getCPad("Exit", 14, " "), firstLine+=40);

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

	Font small = new Font("Malgun Gothic", Font.BOLD, size);
	FontMetrics metr = getFontMetrics(small);

	g.setColor(color);
	g.setFont(small);

	g.drawString(str, (int) getSize().getWidth() / 2 - metr.stringWidth(str) / 2, height);

    }

}
