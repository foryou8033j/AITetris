package AITetris.Model;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 기본 옵션을 저장하는 Properties 클래스, Properties 라이브러리를 사용하여 옵션을 저장/불러온다
 * @author Jeongsam
 *
 */
public class Properties {

    private String propFilePath = "AITetris.dat";

    public double masterVolume = 0.5;
    public double backgroundVolume = 0.5;
    public double effectVolume = 0.5;

    public int blockDownWeight = 10; // 블럭 하강 가중치
    public int bottomCellsWeight = 20; // 최하단 블럭 가중치
    public int lineRemoveWeight = 250; // 라인 제거 가중치
    public int belowVoidCellWeight = -100; // 아래 공백 블럭 생성 가중치
    public int createVoidCellWeight = -100; // 전체 공백 블럭 생성 가중치
    public int voidCellbetweenBlocksWeight = 30;// 블럭 사이에 낀 공백 블럭 가중치
    public int voidCellStickyWallWeight = 50; // 벽에 붙은 공백 블럭 가중치
    
    public static int time = 600000;

    public Properties() {
	read();
    };

    public void masterVolumeUp() {
	if (masterVolume >= 1.0)
	    return;
	masterVolume += 0.1;
    }

    public void backgroundVolumeUp() {

	if (backgroundVolume >= 1.0)
	    return;
	backgroundVolume += 0.1;

    }

    public void effectVolumeUp() {

	if (effectVolume >= 1.0)
	    return;
	effectVolume += 0.1;
    }

    public void masterVolumeDown() {
	if (masterVolume <= 0)
	    return;
	masterVolume -= 0.1;
    }

    public void backgroundVolumeDown() {
	if (backgroundVolume <= 0)
	    return;
	backgroundVolume -= 0.1;
    }

    public void effectVolumeDown() {
	if (effectVolume <= 0)
	    return;
	effectVolume -= 0.1;
    }
    
    /**
     * 파일로 부터 속성을 읽는다
     */
    public void read() {
	
	java.util.Properties prop = new java.util.Properties();

	try {
	    
	    FileInputStream in = new FileInputStream("AITetris.properties");
	    prop.load(in);
	    in.close();
	    
	    masterVolume = Double.valueOf(prop.getProperty("masterVolume"));
	    backgroundVolume = Double.valueOf(prop.getProperty("backgroundVolume"));
	    effectVolume = Double.valueOf(prop.getProperty("effectVolume"));
	    
	    blockDownWeight = Integer.valueOf(prop.getProperty("blockDownWeight"));
	    bottomCellsWeight = Integer.valueOf(prop.getProperty("bottomCellsWeight"));
	    lineRemoveWeight = Integer.valueOf(prop.getProperty("lineRemoveWeight"));
	    belowVoidCellWeight = Integer.valueOf(prop.getProperty("belowVoidCellWeight"));
	    createVoidCellWeight = Integer.valueOf(prop.getProperty("createVoidCellWeight"));
	    voidCellbetweenBlocksWeight = Integer.valueOf(prop.getProperty("voidCellbetweenBlocksWeight"));
	    voidCellStickyWallWeight = Integer.valueOf(prop.getProperty("voidCellStickyWallWeight"));
	    
	    time = Integer.valueOf(prop.getProperty("time"));
	    
	}catch (Exception e) {
	    
	}
	
	
    }

    /**
     * 파일로 속성을 쓴다
     */
    public void save() {

	java.util.Properties prop = new java.util.Properties();
	try {

	    prop.setProperty("masterVolume", String.valueOf(masterVolume));
	    prop.setProperty("backgroundVolume", String.valueOf(backgroundVolume));
	    prop.setProperty("effectVolume", String.valueOf(effectVolume));

	    prop.setProperty("blockDownWeight", String.valueOf(blockDownWeight));
	    prop.setProperty("bottomCellsWeight", String.valueOf(bottomCellsWeight));
	    prop.setProperty("lineRemoveWeight", String.valueOf(lineRemoveWeight));
	    prop.setProperty("belowVoidCellWeight", String.valueOf(belowVoidCellWeight));
	    prop.setProperty("createVoidCellWeight", String.valueOf(createVoidCellWeight));
	    prop.setProperty("voidCellbetweenBlocksWeight", String.valueOf(voidCellbetweenBlocksWeight));
	    prop.setProperty("voidCellStickyWallWeight", String.valueOf(voidCellStickyWallWeight));
	    
	    prop.setProperty("time", String.valueOf(time));
	    
	    FileOutputStream out = new FileOutputStream("AITetris.properties");
	    prop.store(out, "AITetris Config Settings");
	    out.close();

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
