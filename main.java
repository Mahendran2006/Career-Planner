import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import javax.sound.sampled.*;
import java.util.Timer;
import java.util.TimerTask;

public class GoalTrackerApp extends JFrame {

    private JTable timetableTable;
    private JLabel timerLabel;
    private Timer timer;
    private DefaultTableModel tableModel;

    // User-selected alarm sound file
    private File alarmSoundFile = new File("alarm.wav"); // default

    // ---------------- LOGIN CREDENTIALS ----------------
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234";

    public GoalTrackerApp() {
        // ---------------- LOGIN ----------------
        if (!showLoginDialog()) {
            System.exit(0); // close app if login fails
        }

        setTitle("Goal Tracker App");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        // ---------------- MENU BAR ----------------
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.BLACK);
        menuBar.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        JMenu menu = new JMenu("📋 Menu"); // Added emoji
        menu.setForeground(Color.GREEN);

        JMenuItem addGoalItem = new JMenuItem("Add New Goal");
        addGoalItem.addActionListener(e -> addNewGoal());
        menu.add(addGoalItem);

        JMenuItem addTimerItem = new JMenuItem("Add Timer");
        addTimerItem.addActionListener(e -> addCustomTimer());
        menu.add(addTimerItem);

        JMenuItem editTableItem = new JMenuItem("Edit Timetable");
        editTableItem.addActionListener(e -> editTimetable());
        menu.add(editTableItem);

        JMenuItem setSoundItem = new JMenuItem("Set Alarm Sound");
        setSoundItem.addActionListener(e -> chooseAlarmSound());
        menu.add(setSoundItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        menu.add(exitItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        // ---------------- TIMETABLE ----------------
        String[] columns = {"Time", "Activity", "Completed"};
        Object[][] data = {
                {"8AM", "Study", false},
                {"12PM", "Lunch", false},
                {"3PM", "Work", false},
                {"7PM", "Exercise", false},
                {"10PM", "Sleep", false}
        };

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // only Completed column editable directly
            }
        };

        timetableTable = new JTable(tableModel);
        timetableTable.setBackground(Color.BLACK);
        timetableTable.setForeground(Color.GREEN);
        timetableTable.setGridColor(Color.GREEN);
        timetableTable.setRowHeight(35);
        timetableTable.getTableHeader().setBackground(Color.BLACK);
        timetableTable.getTableHeader().setForeground(Color.GREEN);

        JScrollPane timetableScroll = new JScrollPane(timetableTable);
        timetableScroll.getViewport().setBackground(Color.BLACK);
        timetableScroll.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.BLACK);
        centerPanel.add(timetableScroll);
        add(centerPanel, BorderLayout.CENTER);

        // ---------------- TIMER PANEL ----------------
        JPanel timerPanel = new JPanel();
        timerPanel.setBackground(Color.BLACK);

        timerLabel = new JLabel("Timer: 00:00");
        timerLabel.setForeground(Color.GREEN);
        timerPanel.add(timerLabel);

        JButton startBtn = new JButton("Start Timer");
        JButton stopBtn = new JButton("Stop Timer");
        styleButton(startBtn);
        styleButton(stopBtn);

        timerPanel.add(startBtn);
        timerPanel.add(stopBtn);
        add(timerPanel, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> startTimer());
        stopBtn.addActionListener(e -> stopTimer());

        // ---------------- ALARMS ----------------
        checkAlarms();

        setVisible(true);
    }

    // ---------------- LOGIN DIALOG ----------------
    private boolean showLoginDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(null, panel,
                "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String enteredUsername = usernameField.getText().trim();
            String enteredPassword = new String(passwordField.getPassword()).trim();
            return USERNAME.equals(enteredUsername) && PASSWORD.equals(enteredPassword);
        }
        return false;
    }

    // ---------------- STYLE BUTTON ----------------
    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        button.setPreferredSize(new Dimension(120, 35));
    }

    // ---------------- ADD NEW GOAL ----------------
    private void addNewGoal() {
        JTextField timeField = new JTextField();
        JTextField activityField = new JTextField();

        Object[] message = {
                "Enter Time (e.g., 2PM):", timeField,
                "Enter Activity:", activityField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Goal", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String time = timeField.getText().trim();
            String activity = activityField.getText().trim();

            if (!time.isEmpty() && !activity.isEmpty()) {
                tableModel.addRow(new Object[]{time, activity, false});
            } else {
                JOptionPane.showMessageDialog(this, "Both time and activity are required.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ---------------- EDIT TIMETABLE ----------------
    private void editTimetable() {
        int selectedRow = timetableTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a timetable entry to edit.");
            return;
        }

        String currentTime = tableModel.getValueAt(selectedRow, 0).toString();
        String currentActivity = tableModel.getValueAt(selectedRow, 1).toString();

        JTextField timeField = new JTextField(currentTime);
        JTextField activityField = new JTextField(currentActivity);

        Object[] message = {
                "Time:", timeField,
                "Activity:", activityField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Timetable", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newTime = timeField.getText().trim();
            String newActivity = activityField.getText().trim();

            if (!newTime.isEmpty() && !newActivity.isEmpty()) {
                tableModel.setValueAt(newTime, selectedRow, 0);
                tableModel.setValueAt(newActivity, selectedRow, 1);
            } else {
                JOptionPane.showMessageDialog(this, "Both time and activity are required.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ---------------- ADD CUSTOM TIMER ----------------
    private void addCustomTimer() {
        JTextField minutesField = new JTextField();
        JTextField secondsField = new JTextField();

        Object[] message = {
                "Minutes:", minutesField,
                "Seconds:", secondsField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Timer", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                int minutes = Integer.parseInt(minutesField.getText().trim());
                int seconds = Integer.parseInt(secondsField.getText().trim());
                int totalSeconds = minutes * 60 + seconds;

                if (totalSeconds > 0) {
                    startCountdown(totalSeconds);
                } else {
                    JOptionPane.showMessageDialog(this, "Enter a valid time!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ---------------- COUNTDOWN TIMER ----------------
    private void startCountdown(int timeLeft) {
        final int[] remaining = {timeLeft};
        new Thread(() -> {
            try {
                while (remaining[0] > 0) {
                    int min = remaining[0] / 60;
                    int sec = remaining[0] % 60;
                    SwingUtilities.invokeLater(() ->
                            timerLabel.setText(String.format("Timer: %02d:%02d", min, sec))
                    );
                    Thread.sleep(1000);
                    remaining[0]--;
                }
                SwingUtilities.invokeLater(() -> showAlarmDialog("⏰ Custom Timer Finished!"));
            } catch (InterruptedException ignored) {}
        }).start();
    }

    private void showAlarmDialog(String message) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(alarmSoundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            JOptionPane.showMessageDialog(this, message);

            clip.stop();
            clip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- TIMER FUNCTIONS ----------------
    private void startTimer() {
        stopTimer();
        final int[] secondsPassedArr = {0};
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                secondsPassedArr[0]++;
                int minutes = secondsPassedArr[0] / 60;
                int seconds = secondsPassedArr[0] % 60;
                SwingUtilities.invokeLater(() ->
                        timerLabel.setText(String.format("Timer: %02d:%02d", minutes, seconds))
                );
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    // ---------------- ALARMS ----------------
    private void checkAlarms() {
        Timer alarmTimer = new Timer();
        alarmTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                String currentTime = java.time.LocalTime.now().withSecond(0).withNano(0).toString();
                for (int i = 0; i < timetableTable.getRowCount(); i++) {
                    final int row = i;
                    String time = timetableTable.getValueAt(row, 0).toString();
                    boolean completed = (boolean) timetableTable.getValueAt(row, 2);
                    if (convertTo24Hour(time).equals(currentTime) && !completed) {
                        SwingUtilities.invokeLater(() -> showAlarmDialog("Reminder: " + timetableTable.getValueAt(row, 1)));
                        tableModel.setValueAt(true, row, 2); // mark as completed
                    }
                }
            }
        }, 0, 60000);
    }

    // ---------------- TIME CONVERTER ----------------
    private String convertTo24Hour(String time) {
        switch (time.toUpperCase()) {
            case "8AM": return "08:00";
            case "12PM": return "12:00";
            case "3PM": return "15:00";
            case "7PM": return "19:00";
            case "10PM": return "22:00";
        }
        return "00:00";
    }

    // ---------------- SET CUSTOM SOUND ----------------
    private void chooseAlarmSound() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (selectedFile.exists() && selectedFile.getName().endsWith(".wav")) {
                alarmSoundFile = selectedFile;
                JOptionPane.showMessageDialog(this, "Alarm sound set to: " + selectedFile.getName());
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid .wav file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GoalTrackerApp::new);
    }
}

