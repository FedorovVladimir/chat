package client_server;

import javax.swing.*;

public class Window extends JFrame {

    private JTextArea log = new JTextArea();
    protected JTextField fieldHost = new JTextField();
    protected JTextField fieldPort = new JTextField();
    private JTextField fieldInput = new JTextField("");
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    Server server;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Window("127.0.0.1", "8189", "8190");
            new Window("127.0.0.1", "8190", "8189");
        });
    }

    public Window(String host, String port, String youPort) {
        setTitle(port);
        server = new Server(Integer.parseInt(port), log);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        fieldHost.setText(host);
        fieldHost.setBounds(5, 5, 190, 25);
        add(fieldHost);

        fieldPort.setText(youPort);
        fieldPort.setBounds(200, 5, 180, 25);
        add(fieldPort);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setBounds(5, 65, 375, 260);
        add(log);

        fieldInput.addActionListener(actionEvent -> {
            server.sendText(fieldInput.getText());
            log.append("Вы: " + fieldInput.getText() + "\n");
            fieldInput.setText("");
        });
        fieldInput.setBounds(5, 330, 375, 25);
        add(fieldInput);

        JButton button = new JButton("connect");
        button.setBounds(200, 35, 180, 25);
        button.addActionListener(actionEvent -> server.connect(fieldHost.getText(), Integer.parseInt(fieldPort.getText())));
        add(button);

        setVisible(true);
    }
}
