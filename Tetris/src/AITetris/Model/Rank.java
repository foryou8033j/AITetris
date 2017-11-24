package AITetris.Model;

public class Rank {

	private String name;
	private String type;
	private int point;
	
	public Rank(String name, String type, int point) {
		this.name = name;
		this.type = type;
		this.point = point;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getPoint() {
		return point;
	}
	
	@Override
	public String toString() {
		return "Rank [name=" +name + ", type=" + type + ", point=" + point;
	}
	
}
