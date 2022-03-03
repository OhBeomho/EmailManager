package manager.email;

import java.nio.charset.StandardCharsets;

public class HexString {
	// 일반 문자열을 16진수 문자열로 변환
	public static String toHexString(String str) {
		String hexString = "";

		char[] chars = str.toCharArray();

		for (char c : chars) {
			hexString += Integer.toHexString(c);
		}

		return hexString;
	}

	// 16진수 문자열을 일반 문자열로 변환
	public static String toNormalString(String hexStr) {
		String string;

		int l = hexStr.length();
		byte[] data = new byte[l / 2];
		for (int i = 0; i < l; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4) + Character.digit(hexStr.charAt(i + 1), 16));
		}

		string = new String(data, StandardCharsets.UTF_8);

		return string;
	}
}
