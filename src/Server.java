import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class Server {
    private static final int PORT = 8007;
    private static final HashMap<String, String> loginData = new HashMap<>();
    public static ArrayList<String> activeUsers = new ArrayList<>();
    public static List<Socket> clients = new ArrayList<>();

    public static ArrayList<String> getActiveUsers() {
        return activeUsers;
    }

    public static List<Socket> getClients() {
        return clients;
    }

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port: " + PORT);
            while (true) {
                Socket socket = serverSocket.accept(); //oczekiwanie na polaczenie
//                System.out.println("client connected");
                new ClientThread(socket); //tworzenie watku dla klienta
            }

        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }

    private static class ClientThread extends Thread {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;


        public ClientThread(Socket socket) { //konstruktor watku
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.out.println("Client exception when starting thread [1]: " + e.getMessage());
                close();
                return;
            }
            start();
        }

        @Override
        public void run() {

            loadDataFile(); //wczytanie hasel z bazy

            try {
                String username = "";
                String password;
                try {
                    String flag = in.readLine();

                    if (flag.equals("register")) {
                        username = in.readLine();
                        password = in.readLine();

                        if (loginData.containsKey(username)) {
                            out.println("Registration failed, username already exists.");
                        } else {
                            saveCredentialsToDB(username, password);
                            out.println("success");
                        }

                    } else if (flag.equals("login")) {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream(), true);

                        username = in.readLine();
                        password = in.readLine();

                        if (loginData.containsKey(username)) {
                            if (loginData.get(username).equals(password)) {
                                //udalo sie zalogowac
                                clients.add(socket); //dodanie klienta do listy klientow
                                out.println("success");
                                activeUsers.add(username);
                                printActiveUsers();
                            } else {
                                out.println("Login failed. Wrong password");
                            }
                        } else {
                            out.println("Login failed. Wrong username");
                        }

                        String imie = in.readLine();

                        if (imie != null) {
                            System.out.println(imie + " connected to chat!");
                        }

                        File history = new File("chatHistory.txt");
//                        FileWriter writeHistory = new FileWriter(history, true);

                        if (history.exists()) {
                            BufferedReader readHistory = new BufferedReader(new FileReader(history));
                            String line;
                            while ((line = readHistory.readLine()) != null) {
                                out.println(line);
                            }
                            out.println("end");
                        }

                        Thread activeUsersThread = new Thread(() -> {
                            while (true) {
                                try {
                                    Thread.sleep(5000); // 5 sekund
                                    for (Socket socket : clients) {
                                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                        out.println("activeUsers");
                                        out.println(getActiveUsers());
                                    }
                                } catch (InterruptedException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        activeUsersThread.start();


                        //odbierz wiadomosc
                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println("nowa wiadomosc od -> " + imie + ": " + message);

                            //save to chatHistory.txt
                            FileWriter writeHistory = new FileWriter(history, true);
                            writeHistory.write(imie + ": " + message + '\n');
                            writeHistory.close();
//                            wrDit

                            //wyslij wiadomosc do wszystkich
                            for (Socket socket : clients) {
                                PrintWriter out2 = new PrintWriter(socket.getOutputStream(), true);
                                out2.println(imie + ": " + message);
                            }
                        }
                    } else {
                        out.println("Wrong flag");
                    }

                } catch (IOException e) {
                    activeUsers.remove(username);
                    clients.remove(socket); //usuniecie klienta z listy klientow
                    printActiveUsers();
                } finally {
                    try {
                        activeUsers.remove(username);
                        clients.remove(socket); //usuniecie klienta z listy klientow
                        printActiveUsers();
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Client exception when closing socket [2]: " + e.getMessage());
                    }
                    close();
                }
            } catch (Exception e) {
//                throw new RuntimeException(e);
            }
        }

        private static void saveCredentialsToDB(String username, String password) throws IOException {
            //write login data to file
            password = encrypt(password);
            loginData.put(username, password);
            FileWriter fw = new FileWriter("loginData.txt", true);
            fw.write(username + " " + password + "\n");
            fw.close();
        }

        private static String encrypt(String password) {
//            System.out.println("Encrypting password...");
            byte[] encoded = Base64.getEncoder().encode(password.getBytes());
            return new String(encoded);
        }

        private static String decrypt(String password) {
//            System.out.println("Decrypting password...");
            byte[] decoded = Base64.getDecoder().decode(password);
            return new String(decoded);
        }

        private static void loadDataFile() {
            System.out.println("Loading login DB...");

            File loginDataFile = new File("loginData.txt");

            if (loginDataFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(loginDataFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(" ");
                        String username = parts[0];
                        String password = parts[1];
                        password = decrypt(password);
                        loginData.put(username, password);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading login data file: " + e.getMessage());
                }
            }
        }

        private void close() {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }

        public void printActiveUsers() {
            System.out.println("Active users: " + getActiveUsers() + " (" + getActiveUsers().size() + ")");
        }
    }
}
