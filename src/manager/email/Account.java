package manager.email;

import java.io.*;

public class Account {
	public static final File ACCOUNTS_FILE = new File(Account.class.getResource("resources/accounts.txt").getFile());

	// 비밀번호는 16진수로 저장된다.
	private String id, password, email, ePassword;

	public Account(String id, String password, String email, String ePassword) {
		this.id = id;
		this.password = password;
		this.email = email;
		this.ePassword = ePassword;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}
	
	public String getId() {
		return id;
	}
	
	public String getEPassword() {
		return ePassword;
	}

	// 계정 정보를 파일에 저장
	public void writeToFile() {
		String data = id + "/" + password + "/" + email + "/" + ePassword;

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE, true));
			writer.write(data);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
