package AITetris.Model.NeoModel;

public class WeightModel {
    
    int x;
    int y;
    int weight;
    
    public WeightModel(int x, int y) {
	this.x = x;
	this.y = y;
	
	weight = 0;
    }
    
    public WeightModel(int x, int y, int weight) {
	this.x = x;
	this.y = y;
	
	this.weight = weight;
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

}
