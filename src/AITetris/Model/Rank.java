package AITetris.Model;

/**
 * 랭킹 데이터를 저장하는 Rank 클래스
 * @author Jeongsam
 *
 */
public class Rank {

	private String name;	// 이름
	private String type;	// 게임 종류
	private int point;	// 점수
	
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
