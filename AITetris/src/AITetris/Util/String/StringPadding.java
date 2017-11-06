package AITetris.Util.String;

public class StringPadding {

    /**
     * 양쪽으로 자리수만큼 문자 채우기
     *
     * @param str
     *            원본 문자열
     * @param size
     *            총 문자열 사이즈(리턴받을 결과의 문자열 크기)
     * @param strFillText
     *            원본 문자열 외에 남는 사이즈만큼을 채울 문자
     * @return
     */
    public static String getCPad(String str, int size, String strFillText) {
	int intPadPos = 0;
	for (int i = (str.getBytes()).length; i < size; i++) {
	    if (intPadPos == 0) {
		str += strFillText;
		intPadPos = 1;
	    } else {
		str = strFillText + str;
		intPadPos = 0;
	    }
	}
	return str;
    }

    /**
     * 왼쪽으로 자리수만큼 문자 채우기
     *
     * @param str
     *            원본 문자열
     * @param size
     *            총 문자열 사이즈(리턴받을 결과의 문자열 크기)
     * @param strFillText
     *            원본 문자열 외에 남는 사이즈만큼을 채울 문자
     * @return
     */
    public static String getLPad(String str, int size, String strFillText) {
	for (int i = (str.getBytes()).length; i < size; i++) {
	    str = strFillText + str;
	}
	return str;
    }

    /**
     * 오른쪽으로 자리수만큼 문자 채우기
     *
     * @param str
     *            원본 문자열
     * @param size
     *            총 문자열 사이즈(리턴받을 결과의 문자열 크기)
     * @param strFillText
     *            원본 문자열 외에 남는 사이즈만큼을 채울 문자
     * @return
     */
    public static String getRPad(String str, int size, String strFillText) {
	for (int i = (str.getBytes()).length; i < size; i++) {
	    str += strFillText;
	}
	return str;
    }

}
