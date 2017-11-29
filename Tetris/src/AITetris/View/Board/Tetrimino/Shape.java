package AITetris.View.Board.Tetrimino;

import java.util.Random;

/**
 * 테트리미노의 고유 형태를 정의한다
 * @author Jeongsam
 * @since 2016-12-18
 *
 */
public class Shape implements Cloneable {

    private Tetrominoes pieceShape;	//테트리미노의 고유 형태를 정의한다
    private int coords[][];		//정의 된 형태를 바탕으로 고유 좌표를 정의한다
    private int[][][] coordsTable;	//고유 좌표가 저장된 좌표 데이터

    /**
     * Shape 클래스를 초기화한다
     */
    public Shape() {
	coords = new int[4][2];
	setShape(Tetrominoes.NoShape);
    }

    /**
     * 모양의 고정 좌표값을 초기화한다. 각 블록을 기준 좌표로부터 절대 좌표값을 가지며
     * 각 모양의 Index 정보는 {@link Tetrominoes} Enum 으로부터 부여 받는다
     * @param shape
     */
    public void setShape(Tetrominoes shape) {
	coordsTable = new int[][][] { 
	    { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } },
	    { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, 
	    { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } },
	    { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, 
	    { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } },
	    { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, 
	    { { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } },
	    { { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, 
	    { { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } },
	    { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } } };

	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 2; ++j) {
		coords[i][j] = coordsTable[shape.ordinal()][i][j];
	    }
	}

	pieceShape = shape;
    }

    /**
     * Shape 를 설정한다
     * @param shape
     */
    public void setShape(Shape shape) {

	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 2; ++j) {
		coords[i][j] = shape.getCoords()[i][j];
	    }
	}

	pieceShape = shape.getShape();
    }

    private void setX(int index, int x) {
	coords[index][0] = x;
    }

    private void setY(int index, int y) {
	coords[index][1] = y;
    }

    public int x(int index) {
	return coords[index][0];
    }

    public int y(int index) {
	return coords[index][1];
    }

    /**
     * 모양을 반환받는다
     * @return
     */
    public Tetrominoes getShape() {
	return pieceShape;
    }

    /**
     * 좌표 정보를 반환한다
     * @return
     */
    public int[][] getCoords() {
	return coords;
    }

    /**
     * 랜덤한 모양을 지정한다
     */
    public void setRandomShape() {
	Random r = new Random();
	int x = Math.abs(r.nextInt()) % 7 + 1;
	Tetrominoes[] values = Tetrominoes.values();
	setShape(values[x]);
    }

    public int minX() {
	int m = coords[0][0];
	for (int i = 0; i < 4; i++) {
	    m = Math.min(m, coords[i][0]);
	}
	return m;
    }

    public int minY() {
	int m = coords[0][0];
	for (int i = 0; i < 4; i++) {
	    m = Math.min(m, coords[i][1]);
	}
	return m;
    }

    public Shape rotateLeft() {
	if (pieceShape == Tetrominoes.SquareShape)
	    return this;

	Shape result = new Shape();
	result.pieceShape = pieceShape;

	for (int i = 0; i < 4; ++i) {
	    result.setX(i, y(i));
	    result.setY(i, -x(i));
	}

	return result;
    }

    public Shape rotateRight() {
	if (pieceShape == Tetrominoes.SquareShape)
	    return this;

	Shape result = new Shape();
	result.pieceShape = pieceShape;

	for (int i = 0; i < 4; ++i) {
	    result.setX(i, -y(i));
	    result.setY(i, x(i));
	}

	return result;
    }

    /**
     * 테트리미노 데이터 복사 Clone 메소드, clone() 메소드를 재정의한다
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
	// TODO Auto-generated method stub

	Object obj = null;
	try {
	    obj = super.clone();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return obj;
    }

}
