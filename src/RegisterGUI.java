import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterGUI extends JFrame {

    private JPanel loginPanel;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    public static String username;
    public static String password;

    public static boolean czyZarejestrowano = false;

    public boolean getBoolean() {
        return czyZarejestrowano;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public RegisterGUI() {
        // create and show the login GUI
        JFrame loginFrame = new JFrame("Register");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 200);

        JPanel loginPanel = new JPanel();
        loginFrame.add(loginPanel);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Create account");

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        loginFrame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                password = passwordField.getText();
   //           System.out.println(username + " " + password);
                czyZarejestrowano = true;
                loginFrame.dispose();
            }
        });
    }
    public static void main(String[] args) {
        LoginGUI loginGUI = new LoginGUI();
    }

}