package AITetris.View.Ranking;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;

import AITetris.Tetris;
import AITetris.Model.Rank;
import AITetris.Util.String.StringPadding;

public class RankingPanel extends JPanel {

	private Tetris tetris;

	public RankingPanel(Tetris tetris) {

		this.tetris = tetris;

		requestFocus();

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				tetris.initTitle();

				super.keyPressed(e);
			}
		});

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		drawRank(g);

		repaint();
	}

	private void drawRank(Graphics g) {

		List<Rank> list = tetris.getDB().selectAll();

		drawStringCenterOfPanel(g, Color.BLACK, 32, StringPadding.getCPad("Press Any Key Return To Title", 20, " "),
				60);

		Ascending ascending = new Ascending();
		Collections.sort(list, ascending);

		int y = 220;

		for(int i=0; i<list.size(); i++) {
			if(i>10) break;
			Rank rank = list.get(i);
			String str = (i+1) + ". \t" + rank.getName() + "\t " + rank.getType() + "\t " + rank.getPoint(); 
			drawStringCenterOfPanel(g, Color.BLACK, 18, StringPadding.getCPad(str, 20, " "), y+=40);
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

	// 오름차순
	class Ascending implements Comparator<Rank> {

		@Override
		public int compare(Rank o1, Rank o2) {
			return o1.getPoint() > o2.getPoint() ? -1 : o1.getPoint() < o2.getPoint() ? 1 :0;
		}

	}

}
