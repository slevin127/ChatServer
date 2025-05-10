package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class NetClient extends JFrame {

	final String serverIP = "127.0.0.1";
	final int serverPort = 1234;

	private JTextArea chatArea;
	private JTextField inputField;
	private BufferedReader in;
	private PrintWriter out;

	public NetClient() {
		super("Chat Client");

		setSize(400, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// Чат-область (только для чтения)
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setBackground(Color.BLACK);
		chatArea.setForeground(Color.WHITE);
		chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(chatArea);
		add(scrollPane, BorderLayout.CENTER);

		// Поле ввода
		inputField = new JTextField();
		inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
		add(inputField, BorderLayout.SOUTH);

		// Обработка нажатия Enter
		inputField.addActionListener(e -> {
			String message = inputField.getText().trim();
			if (!message.isEmpty()) {
				out.println(message);
				out.flush();
				inputField.setText("");
			}
		});

		// Подключение к серверу
		connect();

		setVisible(true);
		inputField.requestFocusInWindow();
	}

	private void connect() {
		try {
			Socket socket = new Socket(serverIP, serverPort);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());

			// Чтение входящих сообщений в отдельном потоке
			new Thread(() -> {
				try {
					String line;
					while ((line = in.readLine()) != null) {
						chatArea.append(line + "\n");
					}
				} catch (IOException e) {
					chatArea.append("Ошибка подключения\n");
					e.printStackTrace();
				}
			}).start();

		} catch (IOException e) {
			chatArea.setForeground(Color.RED);
			chatArea.append("Не удалось подключиться к " + serverIP + ":" + serverPort + "\n");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(NetClient::new);
	}
}
