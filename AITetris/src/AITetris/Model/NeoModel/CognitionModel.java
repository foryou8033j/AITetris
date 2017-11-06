package AITetris.Model.NeoModel;

import javafx.collections.ObservableList;

/**
 * 블럭의 배치 중요도 연산 모델
 * @author Jeongsam
 *
 */
public class CognitionModel {

    public int[][] recognitionWeight(int[][] weightModel, int width, int height){
	
	//가장 낮은 층의 블럭이 0이다, 한칸씩 높여가며 가중치를 낮춘다.
	int weightMult = 10;
	
	for (int i = 0; i < height; ++i) {
	    for (int j = 0; j < width; ++j) {
		if(weightModel[j][i] == -1) continue;
		
		weightModel[j][i] = weightMult;
	    }
	    
	    weightMult--;
	}
	
	return weightModel;
    }
    
}
