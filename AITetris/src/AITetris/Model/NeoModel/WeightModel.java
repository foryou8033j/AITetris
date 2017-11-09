package AITetris.Model.NeoModel;

import AITetris.View.Board.Tetrimino.Shape;

/**
 * 가중치 저장 모델
 * @author Jeongsam
 *
 */
public class WeightModel {

	int x;
	int y;
	int weight;
	Shape rotation;
	
	// +a 

	public WeightModel(int x, int y) {
		this.x = x;
		this.y = y;

		weight = 0;
		rotation = new Shape();
	}

	public WeightModel(int x, int y, int weight) {
		this.x = x;
		this.y = y;

		this.weight = weight;
		rotation = new Shape();
	}

	public WeightModel(int x, int y, int weight, Shape rotation) {
		this.x = x;
		this.y = y;

		this.weight = weight;
		this.rotation = rotation;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int set) {
		weight = set;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Shape getShape() {
		return rotation;
	}

}
