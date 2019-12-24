import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientWindow extends JFrame {
    // адрес сервера
    private static final String SERVER_HOST = "localhost";
    // порт
    private static final int SERVER_PORT = 3443;
    // клиентский сокет
    private Socket clientSocket;
    // входящее сообщение
    private Scanner inMessage;
    // исходящее сообщение
    private PrintWriter outMessage;
    // следующие поля отвечают за элементы формы
    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;
    private JButton jbSendMessage;
    // имя клиента
    private String clientName = "";
    // получаем имя клиента
    public String getClientName() {
        return this.clientName;
    }

    private int myUniqueNumber;

    private String loginAuto = "";

    private Autorization autorization;

    // конструктор
    public ClientWindow() {
        try {
            // подключаемся к серверу
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Задаём настройки элементов на форме
        setBounds(400, 200, 600, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);

        add(jsp, BorderLayout.CENTER);
        // label, который будет отражать количество клиентов в чате
        JLabel jlNumberOfClients = new JLabel("Количество клиентов в чате: ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        jbSendMessage = new JButton("Отправить");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("Введите ваше сообщение: ");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfName = new JTextField("Введите ваше имя: ");
        bottomPanel.add(jtfName, BorderLayout.WEST);
        // обработчик события нажатия кнопки отправки сообщения
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если имя клиента, и сообщение непустые, то отправляем сообщение
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    sendMsg();
                    // фокус на текстовое поле с сообщением
                    jtfMessage.grabFocus();
                }
            }
        });
        // при фокусе поле сообщения очищается
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
        // при фокусе поле имя очищается
        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });
        // в отдельном потоке начинаем работу с сервером
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // бесконечный цикл
                    while (true) {
                        // если есть входящее сообщение
                        if (inMessage.hasNext()) {
                            // считываем его
                            String inMes = inMessage.nextLine();

                            //-----------------------------Добавил----------------------------------------

                            String addNumberInChat = "";
                            if(inMes.length() > 17) addNumberInChat = inMes.substring(0,18);

                            if(addNumberInChat.compareTo("###new###member###") == 0){
                                String addNumberInChatField = inMes.substring(18);
                                if(myUniqueNumber == 0) myUniqueNumber = Integer.parseInt(addNumberInChatField);
                                continue;
                            }

                            String loginAndPasswordAutorization = "";
                            if(inMes.length() > 21) loginAndPasswordAutorization = inMes.substring(0,22);

                            if(loginAndPasswordAutorization.compareTo("###Autoriz#log#pass###") == 0){
                                loginAndPasswordAutorization = inMes.substring(22);
                                String[] masLoginPasswordReg = loginAndPasswordAutorization.split("###");
                                if (myUniqueNumber == Integer.parseInt(masLoginPasswordReg[0])) {
                                    if(Boolean.parseBoolean(masLoginPasswordReg[1])){
                                        jtfName.setText(loginAuto);
                                        autorization.enabled();
                                        //enabled();
//                                        AutorizationRegister autorizationRegister = new AutorizationRegister(
//                                                "Регистрация прошла успешна");
//                                        autorizationRegister.setVisible(true);
//                                        autorization.ifRegistrationOk();
                                    }
                                    else{
                                        AutorizationRegister autorizationRegister = new AutorizationRegister(
                                                "Авторизация не прошла, где-то ошибка");
                                        autorizationRegister.setVisible(true);
                                    }
                                }
                                continue;
                            }

                            String strLoginPasswordReg = "";
                            if(inMes.length() > 22) strLoginPasswordReg = inMes.substring(0,23);

                            if(strLoginPasswordReg.compareTo("###otvetRegistration###") == 0){
                                strLoginPasswordReg = inMes.substring(23);
                                String[] masLoginPasswordReg = strLoginPasswordReg.split("###");
                                if (myUniqueNumber == Integer.parseInt(masLoginPasswordReg[0])) {
                                    if(Boolean.parseBoolean(masLoginPasswordReg[1])){
                                        AutorizationRegister autorizationRegister = new AutorizationRegister(
                                                "Регистрация прошла успешно");
                                        autorizationRegister.setVisible(true);
                                        autorization.ifRegistrationOk();
                                    }
                                    else{
                                        AutorizationRegister autorizationRegister = new AutorizationRegister(
                                                "Регистрация не прошла");
                                        autorizationRegister.setVisible(true);
                                    }
                                }
                                continue;
                            }

                            //----------------------------------------------------------------------------





                            String clientsInChat = "Клиентов в чате = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                // выводим сообщение
                                jtaTextAreaMessage.append(inMes);
                                // добавляем строку перехода
                                jtaTextAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
        // добавляем обработчик события закрытия окна клиентского приложения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    // здесь проверяем, что имя клиента непустое и не равно значению по умолчанию
                    if (!clientName.isEmpty() && clientName != "Введите ваше имя: ") {
                        outMessage.println(clientName + " вышел из чата!");
                    } else {
                        outMessage.println("Участник вышел из чата!");
                    }
                    // отправляем служебное сообщение, которое является признаком того, что клиент вышел из чата
                    outMessage.println("##session##end##" + myUniqueNumber);
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {

                }
            }
        });
        jtfName.setEnabled(false);
        // отображаем форму
        setVisible(true);

        autorization = new Autorization();
        autorization.setVisible(true);
    }

    // отправка сообщения
    public void sendMsg() {
        // формируем сообщение для отправки на сервер
        String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
        // отправляем сообщение
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }



    //-------------------------Добавление окна------------------------

    public void sendLoginPassword(String login, String password) {
        // формируем сообщение для отправки на сервер
        String messageStr = "###login###password###" + login + "###" + password + "###" + myUniqueNumber;
        // отправляем сообщение
        outMessage.println(messageStr);
        outMessage.flush();
    }

    public void sendAutorizationLoginPassword(String login, String password) {
        // формируем сообщение для отправки на сервер
        String messageStr = "###Autoriz#log#pass###" + login + "###" + password + "###" + myUniqueNumber;
        // отправляем сообщение
        outMessage.println(messageStr);
        outMessage.flush();
    }


    public class AutorizationRegister extends JDialog{
        public AutorizationRegister(String str){
            super();
            JButton button = new JButton(str);
            add(button, BorderLayout.SOUTH);
            setBounds(700,400,350,55);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }
    }


    public class Autorization extends JDialog {
        private boolean isNextStep = false;
        private JPanel panelR = new JPanel(new BorderLayout());
        private JPanel panelA = new JPanel(new BorderLayout());

        private Autorization autorization;

        private Autorization() {
            super();

            panelA.setLayout((new GridLayout(4, 1)));
            panelR.setLayout((new GridLayout(5, 1)));
            setPanelAuto();
            setPanelReg();
            add(panelR, BorderLayout.CENTER);
            panelR.setEnabled(false);
            panelR.setVisible(false);
            add(panelA, BorderLayout.CENTER);

            setUndecorated(true);

            setBounds(500, 300, 250, 200);
            //jbSendMessage.setEnabled(false);
            jtfMessage.setEnabled(false);
            jbSendMessage.setEnabled(false);
        }

        public void enabled() {
            jtfMessage.setEnabled(true);
            jbSendMessage.setEnabled(true);
            dispose();
            //whoIsMove();
        }

        public void ifRegistrationOk() {
            panelR.setEnabled(false);
            panelR.setVisible(false);
            remove(panelR);
            add(panelA);
            panelA.setEnabled(true);
            panelA.setVisible(true);
        }

        public void setPanelReg() {
            JTextField login, password, password1;

            JLabel field1 = new JLabel("Логин");
            field1.setLocation(20, 0);
            panelR.add(field1);

            login = new JTextField();
            login.setSize(40, 40);
            login.setLocation(20, 120);
            panelR.add(login);

            JLabel field2 = new JLabel("Пароль");
            field2.setLocation(20, 90);
            panelR.add(field2);

            password = new JPasswordField();
            password.setSize(40, 40);
            password.setLocation(20, 200);
            panelR.add(password);

            JLabel field3 = new JLabel("Повторить пароль");
            field3.setLocation(20, 90);
            panelR.add(field3);

            password1 = new JPasswordField();
            password1.setSize(40, 40);
            password1.setLocation(20, 200);
            panelR.add(password1);

//            JButton buttonR = new JButton("Регистрация");
//            JButton buttonA = new JButton("Вход");
//            JPanel bottomPanel = new JPanel(new BorderLayout());
//            bottomPanel.setLayout((new GridLayout(2,1)));
//            bottomPanel.add(buttonA);
//            bottomPanel.add(buttonR);

            JButton registration = new JButton("Регистрация");
            panelR.add(registration);

            JButton back = new JButton("Назад");
            panelR.add(back);

            registration.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (password.getText().compareTo(password1.getText()) == 0) {
                        sendLoginPassword(login.getText(), password.getText());
                    } else {
                        //Ошибка в воде подтверждения пароля
                        AutorizationRegister autorizationRegister = new AutorizationRegister(
                                "Ошибка в воде подтверждения пароля");
                        autorizationRegister.setVisible(true);
                    }
                }
            });

            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ifRegistrationOk();
                }
            });
        }

        public void setPanelAuto() {
            JTextField login, password;

            JLabel field1 = new JLabel("Логин");
            field1.setLocation(20, 0);
            panelA.add(field1);

            login = new JTextField();
            login.setSize(40, 40);
            login.setLocation(20, 120);
            panelA.add(login);

            JLabel field2 = new JLabel("Пароль");
            field2.setLocation(20, 90);
            panelA.add(field2);

            password = new JPasswordField();
            password.setSize(40, 40);
            password.setLocation(20, 200);
            panelA.add(password);

//            JButton buttonR = new JButton("Регистрация");
//            JButton buttonA = new JButton("Вход");
//            JPanel bottomPanel = new JPanel(new BorderLayout());
//            bottomPanel.setLayout((new GridLayout(2,1)));
//            bottomPanel.add(buttonA);
//            bottomPanel.add(buttonR);

            JButton enter = new JButton("Вход");
            panelA.add(enter);

            JButton registration = new JButton("Регистрация");
            panelA.add(registration);

            enter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loginAuto = login.getText();
                    sendAutorizationLoginPassword(login.getText(), password.getText());
                }
            });

            registration.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    panelA.setEnabled(false);
                    panelA.setVisible(false);
                    remove(panelA);
                    add(panelR);
                    panelR.setEnabled(true);
                    panelR.setVisible(true);
                }
            });
        }
    }
}