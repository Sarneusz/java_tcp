import java.net.*;
import java.io.*;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client {
    public static void main(String[] args) throws IOException {

        JFrame frame = new JFrame();
        frame.setIconImage(ImageIO.read(new File("ikona.jpg")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Chat REP projekt");
        frame.setSize(500, 500);

        // Use GridBagLayout for a more flexible layout
        GridBagLayout layout = new GridBagLayout();
        frame.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();

        // Create the text area for displaying messages
        JTextArea messages = new JTextArea();
        messages.setFont(new Font("Arial", Font.PLAIN, 19));
        messages.setBackground(Color.lightGray);
        messages.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(messages);

        // Add the scroll pane to the center of the window
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        layout.setConstraints(scrollPane, constraints);
        frame.add(scrollPane);

        // Create the text field for entering messages
        JTextField messageField = new JTextField();
        //set placeholder to "XD"
        // Add the text field to the bottom of the window
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        layout.setConstraints(messageField, constraints);
        messageField.setEditable(false);
        frame.add(messageField);

        JTextField messageField2 = new JTextField();
        //set placeholder to "XD"
        // Add the text field to the bottom of the window
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        layout.setConstraints(messageField2, constraints);
        messageField2.setText("Hello Friend :)");
        messageField2.setEditable(false);
        frame.add(messageField2);


        JTextField topMessageField = new JTextField();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        layout.setConstraints(topMessageField, constraints);
        topMessageField.setEditable(false);
        frame.add(topMessageField);


        // Create the login button
        JButton loginButton = new JButton("Login");
        // Add the login button to the top left of the window
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        layout.setConstraints(loginButton, constraints);
        frame.add(loginButton);

        // Create the register button
        JButton registerButton = new JButton("Register");
        // Add the register button to the top center of the window
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        layout.setConstraints(registerButton, constraints);
        frame.add(registerButton);

        JButton logoutButton = new JButton("Exit");
        // Add the logout button to the top right of the window
        logoutButton.setVisible(false);
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        layout.setConstraints(logoutButton, constraints);
        frame.add(logoutButton);

        JButton sendButton = new JButton("Send");
        sendButton.setVisible(false);
        // Add the send button to the right of the text field
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        layout.setConstraints(sendButton, constraints);
        frame.add(sendButton);

        frame.setVisible(true);

        //Tworzenie połączenia z serwerem
        Socket socket;

        try {
            socket = new Socket("localhost", 8007);
        } catch (Exception e) {
            System.out.println("Server is not running");
            System.out.println("Starting server...");
            Server.main(args);
            return;
        }

        OutputStream out = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true); //wysylanie
        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in)); //odbieranie

        String choice;
        final String[] xd = new String[1];

        while (xd[0] == null) {
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xd[0] = "l";
                }
            });

            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xd[0] = "r";
                }
            });
        }

        choice = xd[0];

        if (choice.equals("r")) {

            RegisterGUI registerGUI = new RegisterGUI();

            while (!registerGUI.getBoolean()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String username = registerGUI.getUsername();
            String password = registerGUI.getPassword();

            // send registration data to server
            writer.println("register");
            writer.println(username);
            writer.println(password);

            // receive response from server
            String response = reader.readLine();

            if (response.equals("success")) {
                System.out.println("Registration successful");
                System.exit(0);
            } else {
                System.out.println(response);
                System.exit(0);
            }

        } else if (choice.equals("l")) {

            LoginGUI loginGUI = new LoginGUI();

            while (!loginGUI.getBoolean()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    System.out.println("EXCEPTION 1");
                }
            }

            String username = loginGUI.getUsername();
            String password = loginGUI.getPassword();

            // send login data to server
            writer.println("login");
            writer.println(username);
            writer.println(password);

            // check for login success
            String response = reader.readLine();

            if (response.equals("success")) {
                System.out.println("Login successful!");
                messageField.setEditable(true);
                loginButton.setEnabled(false);
                loginButton.setVisible(false);
                registerButton.setEnabled(false);
                registerButton.setVisible(false);
                logoutButton.setVisible(true);
                messageField2.setText("Hello " + username + " :)");

                writer.println(username);

                frame.setTitle("Chat Client - " + username);

                //read history
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("end")) {
                        break;
                    }
                    messages.append(line + "\n");
                }

                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

                //czytanie wiadomosci z serwera
                Thread readerThread = new Thread(() -> {
                    String readerMessageThread; //wiadomosc z serwera
                    String activeUsers;
                    while (true) {
                        try {
                            readerMessageThread = reader.readLine(); //czytanie wiadomosci z serwera
                            if (readerMessageThread.equals("activeUsers")) {
                                activeUsers = reader.readLine();
                                activeUsers = activeUsers.substring(1, activeUsers.length()-1);
                                messageField2.setText("Active users: " + activeUsers);
                            } else {
                                messages.append(readerMessageThread + "\n"); //dodanie wiadomosci do pola tekstowego
                            }
                        } catch (IOException e) {
                            try {
                                logout(socket, writer, reader);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            System.out.println("EXCEPTION 2");
                        }
                    }
                });

                readerThread.start();

                //wysylanie wiadomosci do serwera

                String message = null;

                sendButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String message = messageField.getText();
                        writer.println(message);
                        messageField.setText("");
                    }
                });

                messageField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String message = messageField.getText();
                        writer.println(message);
                        messageField.setText("");
                    }
                });

                logoutButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            logout(socket, writer, reader);
                        } catch (IOException ex) {
//                        throw new RuntimeException(ex);
                            System.out.println("EXCEPTION 3");
                        }
                    }
                });

                //when want to log out type "exit"
                while (!Objects.equals(message, "exit")) {
                    message = userInput.readLine();
                    writer.println(message);
                }

                logout(socket, writer, reader);

            } else {
                System.out.println("Invalid login credentials. Exiting.");
                logout(socket, writer, reader);
            }
        } else {
            System.out.println("Invalid choice. Exiting.");
            System.exit(0);
        }
    }

    private static void logout(Socket socket, PrintWriter writer, BufferedReader reader) throws IOException {
        writer.close();
        reader.close();
        socket.close();
        System.exit(0);
    }

}