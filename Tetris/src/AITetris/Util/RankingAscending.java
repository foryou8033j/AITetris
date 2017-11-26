package AITetris.Util;

import java.util.Comparator;

import AITetris.Model.Rank;

//오름차순
public class RankingAscending implements Comparator<Rank> {

    @Override
    public int compare(Rank o1, Rank o2) {
	return o1.getPoint() > o2.getPoint() ? -1 : o1.getPoint() < o2.getPoint() ? 1 : 0;
    }

}