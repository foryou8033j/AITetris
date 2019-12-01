package AITetris.Util;

import java.util.Comparator;

import AITetris.Model.Rank;

/**
 * 랭킹 데이터 정렬을 위한 클래스
 * Comparator 인터페이스의 compare 메소드를 재정의한다
 * @author Jeongsam
 *
 */
public class RankingAscending implements Comparator<Rank> {

    @Override
    public int compare(Rank o1, Rank o2) {
	return o1.getPoint() > o2.getPoint() ? -1 : o1.getPoint() < o2.getPoint() ? 1 : 0;
    }

}