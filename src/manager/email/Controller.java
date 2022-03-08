package manager.email;

import java.io.*;
import java.net.URL;
import java.util.*;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable {
	@FXML
	private AnchorPane mainPane, loginPane, registerPane;
	@FXML
	private TextField emailField, idField, rIdField;
	@FXML
	private PasswordField passwordField, rPasswordField, checkPasswordField, ePasswordField;
	@FXML
	private Label emailLabel, idLabel;
	@FXML
	private ListView<String> emailList;

	private ArrayList<Account> accounts;
	private ArrayList<Email> emails;
	private Timeline timeline;
	private Account currentAccount;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		accounts = loadAccounts();
		timeline = new Timeline();
	}

	// 계정 정보를 파일에서 불러오기
	private ArrayList<Account> loadAccounts() {
		ArrayList<Account> accounts = new ArrayList<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(Account.ACCOUNTS_FILE));
			String line;

			while ((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "/");
				String id = st.nextToken(), password = st.nextToken(), email = st.nextToken(),
						ePassword = st.nextToken();

				accounts.add(new Account(id, password, email, ePassword));
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return accounts;
	}

	// 이메일을 파일에서 불러오기
	private ArrayList<Email> loadEmails(String accountId) {
		ArrayList<Email> emails = new ArrayList<>();

		try {
			File file = new File("C:/EmailManager/" + accountId + "Emails.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;

			while ((line = reader.readLine()) != null) {
				emails.add(new Email(line));
				emailList.getItems().add(line);
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return emails;
	}

	// 계정 삭제
	private void deleteAccount(Account account) {
		accounts.remove(account);
		emails.clear();
		emailList.getItems().clear();

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(Account.ACCOUNTS_FILE));

			for (Account a : accounts) {
				writer.write(a.getId() + "/" + a.getPassword() + "/" + a.getEmail() + "/" + a.getEPassword());
				writer.newLine();
			}

			writer.flush();
			writer.close();

			writer = new BufferedWriter(
					new FileWriter("bin\\manager\\email\\resources\\" + account.getId() + "Emails.txt"));
			writer.write("");

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 다이얼로그 창
	private Alert dialog(AlertType type, String... thc) {
		String title = thc[0], header = thc[1], content = thc.length > 2 ? thc[2] : "";

		Alert dialog = new Alert(type);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);

		return dialog;
	}

	// 로그인
	@FXML
	protected void setOnLogin() {
		String id = idField.getText(), password = passwordField.getText();

		if (id.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "로그인 오류", "아이디를 입력해 주세요.");
			dialog.show();
			idField.requestFocus();
			return;
		} else if (password.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "로그인 오류", "비밀번호를 입력해 주세요.");
			dialog.show();
			passwordField.requestFocus();
			return;
		}

		for (Account a : accounts) {
			if (a.getId().equalsIgnoreCase(id)) {
				if (HexString.toNormalString(a.getPassword()).equals(password)) {
					timeline.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(1), new KeyValue(loginPane.translateYProperty(),
									-mainPane.getPrefHeight(), Interpolator.SPLINE(1, 0, 0, 1))));
					timeline.setOnFinished(e -> timeline.getKeyFrames().clear());
					timeline.play();

					idLabel.setText(a.getId());
					emailLabel.setText(a.getEmail());

					idField.clear();
					passwordField.clear();

					emails = loadEmails(a.getId());

					currentAccount = a;
				} else {
					Alert dialog = dialog(AlertType.ERROR, "로그인 오류", "비밀번호가 일치하지 않습니다.", "다시 입력해 주세요.");
					dialog.show();
				}

				return;
			}
		}

		Alert dialog = dialog(AlertType.ERROR, "로그인 오류", "존재하지 않는 계정입니다.", "다시 입력해 주세요.");
		dialog.show();
		dialog.setOnCloseRequest(e -> idField.requestFocus());
	}

	// 회원가입 화면 표시
	@FXML
	protected void setOnStartRegister() {
		idField.clear();
		passwordField.clear();

		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
				new KeyValue(registerPane.translateXProperty(), 0, Interpolator.SPLINE(1, 0, 0, 1))));
		timeline.setOnFinished(e -> timeline.getKeyFrames().clear());
		timeline.play();
	}

	// 회원가입
	@FXML
	protected void setOnRegister() {
		String id = rIdField.getText(), password = rPasswordField.getText(), cPassword = checkPasswordField.getText();
		String email = emailField.getText(), ePassword = ePasswordField.getText();

		if (id.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "아이디를 입력하세요.");
			dialog.show();
			idField.requestFocus();
			return;
		} else if (email.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "이메일을 입력하세요.");
			dialog.show();
			emailField.requestFocus();
			return;
		} else if (password.equals("") || cPassword.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "비밀번호를 입력하세요.");
			dialog.show();

			if (password.equals("")) {
				passwordField.requestFocus();
			} else {
				checkPasswordField.requestFocus();
			}

			return;
		} else if (ePassword.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "이메일 비밀번호를 입력하세요.");
			dialog.show();

			ePasswordField.requestFocus();

			return;
		}

		for (Account a : accounts) {
			if (a.getEmail().equalsIgnoreCase(email)) {
				Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "" + email + "는 이미 사용된 이메일입니다.", "다른 이메일을 입력하세요.");
				dialog.show();
				emailField.requestFocus();
				return;
			} else if (a.getId().equalsIgnoreCase(id)) {
				Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "" + id + "는 이미 사용된 아이디입니다.", "다른 아이디를 입력하세요.");
				dialog.show();
				idField.requestFocus();
				return;
			}
		}

		if (!password.equals(cPassword)) {
			Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "비밀번호가 일치하지 않습니다.");
			dialog.show();
			checkPasswordField.requestFocus();
			return;
		}

		Account newAcc = new Account(id, HexString.toHexString(password), email, HexString.toHexString(ePassword));
		accounts.add(newAcc);
		newAcc.writeToFile();

		setOnCancel();
	}

	// 회원가입 취소
	@FXML
	protected void setOnCancel() {
		rIdField.clear();
		ePasswordField.clear();
		emailField.clear();
		rPasswordField.clear();
		checkPasswordField.clear();

		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new KeyValue(registerPane.translateXProperty(),
				mainPane.getPrefWidth(), Interpolator.SPLINE(1, 0, 0, 1))));
		timeline.setOnFinished(e -> timeline.getKeyFrames().clear());
		timeline.play();
	}

	// 로그아웃
	@FXML
	protected void setOnLogout() {
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
				new KeyValue(loginPane.translateYProperty(), 0, Interpolator.SPLINE(1, 0, 0, 1))));
		timeline.setOnFinished(e -> timeline.getKeyFrames().clear());
		timeline.play();

		currentAccount = null;
	}

	// 이메일 삭제
	@FXML
	protected void setOnDelete() {
		Alert dialog = dialog(AlertType.CONFIRMATION, "삭제 확인", "이 계정을 삭제하겠습니까?");
		dialog.showAndWait().ifPresent(result -> {
			if (result == ButtonType.OK) {
				deleteAccount(currentAccount);
			}
		});

		setOnLogout();
	}

	// 이메일 추가
	@FXML
	protected void setOnAdd() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("이메일 입력");
		dialog.setHeaderText("추가할 이메일을 입력하세요.");
		dialog.showAndWait().ifPresent(e -> {
			if (Email.isEmailFormat(e)) {
				if (!emailList.getItems().contains(e)) {
					Email email = new Email(e);
					email.writeToFile("C:/EmailManager/" + currentAccount.getId() + "Emails.txt");
					emails.add(email);
					emailList.getItems().add(e);
				} else {
					Alert d = dialog(AlertType.ERROR, "이메일 추가 오류", "이미 존재하는 이메일 입니다.");
					emailList.getSelectionModel().select(e);
					d.show();
				}
			} else {
				Alert d = dialog(AlertType.ERROR, "이메일 추가 오류", "이메일 형식이 아닙니다.",
						"올바른 이메일 형식(예시: test@naver.com)으로\n입력해 주세요.");
				d.show();
			}
		});
	}

	@FXML
	protected void setOnDeleteEmail() {
		Alert confirmDelete = dialog(AlertType.CONFIRMATION, "삭제 확인", "이 이메일을 정말 삭제하시겠습니까?");
		confirmDelete.showAndWait().ifPresent(e -> {
			if (e == ButtonType.OK) {
				String selectedEmail = emailList.getSelectionModel().getSelectedItem();
				Email selectedEmailObject = null;

				if (selectedEmail == null) {
					return;
				}

				emailList.getItems().remove(selectedEmail);

				for (Email email : emails) {
					if (email.getEmail().equals(selectedEmail)) {
						selectedEmailObject = email;
					}
				}

				if (selectedEmailObject != null) {
					emails.remove(selectedEmailObject);
				}

				if (emailList.getItems().size() != 0) {
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(
								"bin\\manager\\email\\resources\\" + currentAccount.getId() + "Emails.txt"));

						for (String email : emailList.getItems()) {
							writer.write(email);
							writer.newLine();
						}

						writer.flush();
						writer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}

	@FXML
	protected void setOnSend() {
		String selectedEmail = emailList.getSelectionModel().getSelectedItem();

		if (selectedEmail == null) {
			return;
		}

		for (Email email : emails) {
			if (email.getEmail().equals(selectedEmail)) {
				SendDialog sendDialog = new SendDialog(email);
				sendDialog.show();
			}
		}
	}

	@FXML
	protected void setOnSendToMe() {
		Email me = new Email(currentAccount.getEmail());
		new SendDialog(me).show();
	}

	private class SendDialog extends Stage {
		SendDialog(Email to) {
			setScene(new Scene(new AnchorPane(), 320, 240));
			getScene().getStylesheets().add(SendDialog.class.getResource("resources/style.css").toString());
			setResizable(false);
			setTitle("메일 보내기");

			AnchorPane pane = (AnchorPane) getScene().getRoot();
			Button sendButton = new Button("메일 보내기");
			TextArea textArea = new TextArea();
			TextField titleField = new TextField();
			Label toLabel = new Label("TO: " + to.getEmail());
			Label fromLabel = new Label("FROM: " + currentAccount.getEmail());

			titleField.setPromptText("메일 제목 입력");
			textArea.setPromptText("메일 내용 입력");

			VBox vbox = new VBox(toLabel, fromLabel, titleField, textArea, sendButton);
			vbox.setAlignment(Pos.CENTER);
			vbox.setSpacing(5);
			vbox.setPrefSize(320, 220);

			pane.getChildren().add(vbox);

			sendButton.setOnAction(e -> {
				Alert loading = dialog(AlertType.NONE, "메일 보내기", "메일 '" + titleField.getText() + "'을/를 보내는 중입니다.",
						"잠시 기다려 주세요..."), sent = dialog(AlertType.INFORMATION, "메일 보내기", "메일이 성공적으로 보내졌습니다.");
				new Thread(() -> {
					to.send(currentAccount.getEmail() + "/" + HexString.toNormalString(currentAccount.getEPassword()),
							titleField.getText(), textArea.getText());

					Platform.runLater(() -> {
						loading.getButtonTypes().add(ButtonType.OK);
						loading.hide();
						sent.show();
						hide();
						titleField.clear();
						textArea.clear();
					});
				}).start();
				loading.show();
			});

			setOnCloseRequest(e -> {
				titleField.clear();
				textArea.clear();
			});
		}
	}
}
