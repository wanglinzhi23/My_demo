package intellif.utils;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionValidator {

	// 手机号验证
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	// 身份证验证
	public static boolean isIdCard(String idCard) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		// p = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])"); //
		// 15位或者18位 最后一位可以为x
		p = Pattern.compile("(\\d{14}[0-9X])|(\\d{17}[0-9X])|(\\d{14}[0-9x])|(\\d{17}[0-9x])");
		m = p.matcher(idCard);
		b = m.matches();
		return b;
	}

	// 身份证验证++
	public static boolean checkIDCard(String idCard) {

		boolean flag = false;
		String birthDate = "";
		int areaCode = 0;
		if (isIdCard(idCard)) {
			try {
				if (idCard.length() == 15) {
					birthDate = "19" + idCard.substring(6, 12);

				} else if (idCard.length() == 18) {
					birthDate = idCard.substring(6, 14);
				}
				birthDate = birthDate.substring(0, 4) + "-"
						+ birthDate.substring(4, 6) + "-"
						+ birthDate.substring(6, 8);
				areaCode = Integer.valueOf(idCard.substring(0, 6)).intValue();

				flag = checkDate(birthDate) && checkAreaCode(areaCode);
			} catch (Exception e) {
				System.out.println("身份证解析异常:" + e);
				return false;
			}
			return flag;
		} else {
			return false;
		}

	}

	// 日期合法性的验证
	public static boolean checkDate(String date) {

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			format.setLenient(false);
			System.out.print(format.parse(date));
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	// 校验出生地的行政区编码 最小的行政区划码是150000 最大的行政区划码是110000
	public static boolean checkAreaCode(int code) {
		if (code < 700000 && code > 110000) {
			return true;
		} else {
			return false;
		}
	}

/*	public static void main(String args[]) {
		System.out.println(isMobile("25576685005"));
		System.out.println(checkIDCard("61232219900630262x"));
		

	}*/

}
