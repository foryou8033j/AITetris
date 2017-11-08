package AITetris.Model.NeoModel;

import javafx.collections.ObservableList;

/**
 * 블럭의 배치 중요도 연산 모델
 * 
 * @author Jeongsam
 *
 */
public class CognitionModel {

    public int[][] recognitionWeight(int[][] weightModel, int width, int height) {

	// 가장 낮은 층의 블럭이 0이다, 한칸씩 높여가며 가중치를 낮춘다.
	int weightMult = 0;

	for (int j = 0; j < width; ++j) {
	    for (int i = height - 1; i >= 0; --i) {
		if (weightModel[j][i] == -1)
		    continue;

		weightModel[j][i] = weightMult += 10;

	    }

	    // 새로운 열에서 가중치값을 초기화한다.
	    weightMult = 0;
	}

	for (int j = 0; j < width; ++j) {
	    for (int i = height - 1; i >= 0; --i) {
		if (weightModel[j][i] != -1) {

		    try {
			if (weightModel[j - 1][i] == -1)
			    weightModel[j][i] += 20;
			
			if (weightModel[j - 2][i] == -1)
			    weightModel[j][i] += 20;

			if (weightModel[j + 1][i] == -1)
			    weightModel[j][i] += 20;
			
			if (weightModel[j + 2][i] == -1)
			    weightModel[j][i] += 20;

			if (weightModel[j][i + 1] == -1)
			    weightModel[j][i] += 20;
			
			if (weightModel[j][i + 2] == -1)
			    weightModel[j][i] += 20;

		    } catch (Exception e) {
			weightModel[j][i] += 30;
		    }

		}

	    }

	    // 새로운 열에서 가중치값을 초기화한다.
	}

	return weightModel;
    }

}
