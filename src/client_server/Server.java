package client_server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Server {

    long a;
    long b;
    long g;
    long p;
    long K;
    boolean isConnected = false;

    private BufferedReader in = null;
    private BufferedWriter out;
    private Thread rxThreadIn;
    private Thread rxThreadOut;
    private ServerSocket serverSocket;
    private Socket socket;
    private JTextArea log;

    public Server(int port, JTextArea log) {
        this.log = log;
        try {
            serverSocket = new ServerSocket(port);
            rxThreadIn = new Thread(() -> {
                while (!rxThreadIn.isInterrupted()) {
                    try {
                        socket = serverSocket.accept();
                        while (true) {
                            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                            String text = in.readLine();
                            if (isConnected) {
                                StringBuilder sb = new StringBuilder(text);
                                StringBuilder msg = new StringBuilder();
                                for (int i = 0; i < sb.length(); i++) {
                                    msg.append((char)(sb.charAt(i) - K));
                                }
                                log.append("Он: " + msg + "\n");
                            } else {
                                String[] values = text.split(" ");
                                long g = Long.parseLong(values[0]);
                                long p = Long.parseLong(values[1]);
                                long A = Long.parseLong(values[2]);
                                b = (long) (Math.random() * 10) + 1;
                                K = (long) (Math.pow(A, b) % p);
                                K++;
                                sendText("" + (long) (Math.pow(g, b) % p));
                                isConnected = true;
                                System.out.println("Секретный ключ K = " + K);
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Не удалось получить сообщение");
                    }
                }
            });
            rxThreadIn.start();
            System.out.println("Server start: " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
        } catch (IOException e) {
            System.out.println("Не удалось открыть сокет.");
            System.exit(1);
        }
    }

    public void connect(String host, int port) {
        a = (long) (Math.random() * 10) + 1;
        g = (long) (Math.random() * 10) + 1;
        p = (long) (Math.random() * 10) + 1;
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            rxThreadOut = new Thread(() -> {
                while (!rxThreadOut.isInterrupted()) {
                    try {
                        String text = in.readLine();
                        if (isConnected) {
                            StringBuilder sb = new StringBuilder(text);
                            StringBuilder msg = new StringBuilder();
                            for (int i = 0; i < sb.length(); i++) {
                                msg.append((char)(sb.charAt(i) - K));
                            }
                            log.append("Он: " + msg + "\n");
                        } else {
                            isConnected = true;
                            long B = Long.parseLong(text);
                            K = (long) (Math.pow(B, a) % p);
                            K++;
                            System.out.println("Секретный ключ K = " + K);
                        }
                    } catch (IOException e) {
                        System.out.println("Не удалось получить сообщение");
                    }
                }
            });
            rxThreadOut.start();
            System.out.println("Связь с " + host + ":" + port + " налажена");
            if (!isConnected) {
                sendText(g + " " + p + " " + (long) (Math.pow(g, a) % p));
            }
        } catch (IOException e) {
            System.out.println("Не удалось подключиться к " + host + ":" + port);
            e.printStackTrace();
        }
    }

    public void sendText(String text) {
        try {
            if (isConnected) {
                StringBuilder sb = new StringBuilder(text);
                StringBuilder msg = new StringBuilder();
                for (int i = 0; i < sb.length(); i++) {
                    msg.append((char)(sb.charAt(i) + K));
                }
                System.out.println("send: " + msg);
                out.write(msg + "\r\n");
                out.flush();
            } else {
                out.write(text + "\r\n");
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Не удалось отправить сообщение");
        }
    }
}
