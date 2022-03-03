package manager.email;

import java.io.*;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;

public class Email {
	private String email;

	public Email(String email) {
		this.email = email;
	}

	// 이메일 형식인지 확인
	public static boolean isEmailFormat(String str) {
		boolean result = false;

		if (Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", str)) {
			result = true;
		}

		return result;
	}

	// 이메일을 파일에 저장
	public void writeToFile(String file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(email);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getEmail() {
		return email;
	}

	// 메일 보내기
	public void send(String eap, String title, String content) {
		if (eap.equals("") || title.equals("") || content.equals("")) {
			return;
		}

		String[] eaps = eap.split("/");
		String email = eaps[0], password = eaps[1];
		Properties p = System.getProperties();
		int port = 0;
		String host = "";

		if (this.email.endsWith("naver.com")) {
			host = "smtp.naver.com";
			port = 587;
		} else if (this.email.endsWith("gmail.com")) {
			host = "smtp.gmail.com";
			port = 465;

			p.put("mail.smtp.ssl.enable", "true");
			p.put("mail.smtp.ssl.trust", host);
		}

		p.put("mail.smtp.host", host);
		p.put("mail.smtp.port", port);
		p.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(p, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, password);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(email));
			message.addRecipient(RecipientType.TO, new InternetAddress(this.email));
			message.setSubject(title);
			message.setText(content);

			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
