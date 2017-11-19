package AITetris.View.Option;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import AITetris.Tetris;
import AITetris.Model.Properties;
import javafx.scene.input.KeyCode;

public class OptionPanel extends JPanel {

    private Tetris tetris;
    private Properties properties = null;

    int curPoint = 0;
    int minPoint = 0;
    int maxPoint = 9;

    public OptionPanel(Tetris tetris) {
	this.tetris = tetris;
	this.properties = tetris.getProperties();

	requestFocus();

	addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		    tetris.initTitle();
		    break;

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
		    
		    
		    switch(curPoint) {
		    case 0:
			properties.masterVolumeDown();
			break;
		    case 1:
			properties.backgroundVolumeDown();
			break;
		    case 2:
			properties.effectVolumeDown();
			break;
		    case 3:
			properties.blockDownWeight -= 10;
			break;
		    case 4:
			properties.bottomCellsWeight -= 10;
			break;
		    case 5:
			properties.lineRemoveWeight -=10;
			break;
		    case 6:
			properties.belowVoidCellWeight -= 10;
			break;
		    case 7:
			properties.createVoidCellWeight -= 10;
			break;
		    case 8:
			properties.voidCellbetweenBlocksWeight -= 10;
			break;
		    case 9:
			properties.voidCellStickyWallWeight -= 10;
			break;
		    }
		    
		    properties.save();
		    
		    break;
		    
		case KeyEvent.VK_RIGHT:
		    
		    switch(curPoint) {
		    case 0:
			properties.masterVolumeUp();
			break;
		    case 1:
			properties.backgroundVolumeUp();
			break;
		    case 2:
			properties.effectVolumeUp();
			break;
		    case 3:
			properties.blockDownWeight += 10;
			break;
		    case 4:
			properties.bottomCellsWeight += 10;
			break;
		    case 5:
			properties.lineRemoveWeight +=10;
			break;
		    case 6:
			properties.belowVoidCellWeight += 10;
			break;
		    case 7:
			properties.createVoidCellWeight += 10;
			break;
		    case 8:
			properties.voidCellbetweenBlocksWeight += 10;
			break;
		    case 9:
			properties.voidCellStickyWallWeight += 10;
			break;
		    }
		    
		    properties.save();
		    
		    break;

		}

		super.keyPressed(e);
	    }
	});

    }

    @Override
    public void paint(Graphics g) {
	super.paint(g);

	drawTitle(g);

	drawProperties(g);

	repaint();
    }

    private void drawTitle(Graphics g) {

	int stringHeight = 0;

	String message = "";

	Font small = new Font("Malgun Gothic", Font.BOLD, 16);
	FontMetrics metr = getFontMetrics(small);

	Color color = Color.BLACK;

	stringHeight = drawStringCenterOfPanel(g, Color.RED, 16, "Press 'ESC' Back To Title", 20);
	stringHeight = drawStringCenterOfPanel(g, Color.BLACK, 64, "Options", 120);
	stringHeight = drawStringCenterOfPanel(g, Color.BLACK, 24, "Sounds", 100 + stringHeight);

	g.drawLine(200, 150, tetris.getSize().width - 200, 150);

	g.setFont(small);

	g.setColor((curPoint == 0 ? Color.RED : Color.BLACK));
	message = "마스터";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 240);
	drawVolumeVar(g, 520, 222, properties.masterVolume);

	g.setColor((curPoint == 1 ? Color.RED : Color.BLACK));
	message = "배경음악";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 280);
	drawVolumeVar(g, 520, 262, properties.backgroundVolume);

	g.setColor((curPoint == 2 ? Color.RED : Color.BLACK));
	message = "효과음";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 320);
	drawVolumeVar(g, 520, 302, properties.effectVolume);

	g.setColor(Color.BLACK);
	g.drawLine(200, 360, tetris.getSize().width - 200, 360);

	stringHeight = drawStringCenterOfPanel(g, Color.BLACK, 24, "AI. Properties", 350 + stringHeight);

	g.setFont(small);
	g.setColor((curPoint == 3 ? Color.RED : Color.BLACK));
	message = "블럭 하강 가중치(+10)";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 410);
	drawStringCenterOfPanel(g, g.getColor(), 16, String.valueOf(properties.blockDownWeight), 410);

	g.setColor((curPoint == 4 ? Color.RED : Color.BLACK));
	message = "최하단 블럭 가중치(+20)";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 440);
	drawStringCenterOfPanel(g, g.getColor(), 16, String.valueOf(properties.bottomCellsWeight), 440);

	g.setColor((curPoint == 5 ? Color.RED : Color.BLACK));
	message = "라인 제거 가중치(+250)";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 470);
	drawStringCenterOfPanel(g, g.getColor(), 16, String.valueOf(properties.lineRemoveWeight), 470);

	g.setColor((curPoint == 6 ? Color.RED : Color.BLACK));
	message = "공백 블럭 생성 가중치(-100)";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 500);
	drawStringCenterOfPanel(g, g.getColor(), 16, String.valueOf(properties.belowVoidCellWeight), 500);

	g.setColor((curPoint == 7 ? Color.RED : Color.BLACK));
	message = "전체 공백 블럭 생성 가중치(-100)";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 530);
	drawStringCenterOfPanel(g, g.getColor(), 16, String.valueOf(properties.createVoidCellWeight), 530);

	g.setColor((curPoint == 8 ? Color.RED : Color.BLACK));
	message = "블럭 사이에 낀 공백 블럭 가중치(30)";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 560);
	drawStringCenterOfPanel(g, g.getColor(), 16, String.valueOf(properties.voidCellbetweenBlocksWeight), 560);

	g.setColor((curPoint == 9 ? Color.RED : Color.BLACK));
	message = "벽에 붙은 공백 블럭 가중치(50)";
	g.drawString(message, 420 - metr.stringWidth(message) / 2, 590);
	drawStringCenterOfPanel(g, g.getColor(), 16, String.valueOf(properties.voidCellStickyWallWeight), 590);
    }

    private void drawProperties(Graphics g) {

    }

    private void drawVolumeVar(Graphics g, int x, int y, double value) {

	for (int i = 0; i < 10; i++) {

	    if ((double) i < value * 10.0)
		g.fillRect(x + (i * 22), y, 20, 20);
	    else
		g.drawRect(x + (i * 22), y, 20, 20);
	}

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
    private int drawStringCenterOfPanel(Graphics g, Color color, int size, String str, int height) {

	Font tmpFont = g.getFont();

	Font small = new Font("Malgun Gothic", Font.BOLD, size);
	FontMetrics metr = getFontMetrics(small);

	g.setColor(color);
	g.setFont(small);

	g.drawString(str, (int) getSize().getWidth() / 2 - metr.stringWidth(str) / 2, height);

	g.setFont(tmpFont);

	return metr.getHeight();

    }

}
