package AITetris;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import AITetris.View.Title.Title;

/**
 * 테트리스의 기본 프레임을 띄워준다.
 * 
 * @author Jeongsam
 *
 */
public class Tetris extends JFrame{

	private Title title;
	
    public Tetris() {

	super();
	setTitle("AITetris");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(20, 20, 1330, 680);
	setFocusable(true);
	setLocationByPlatform(true);
	
	getContentPane().setLayout(null);
	
	title = new Title(getContentPane());
	title.setBounds(20, 20, 1270, 600);
	title.setBorder(BorderFactory.createLineBorder(Color.black));
	
	getContentPane().add(title);
	title.setFocusable(true);
	

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
