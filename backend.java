import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

// Class to represent a Career Goal
class Goal implements Serializable {
    String name;
    LocalDate deadlineDate;      // Date part
    LocalTime deadlineTime;      // Time part (12-hour)
    boolean completed;
    boolean alarmSet;

    public Goal(String name, String dateStr, String timeStr) throws Exception {
        this.name = name;
        try {
            this.deadlineDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.deadlineTime = LocalTime.parse(timeStr.toUpperCase(), DateTimeFormatter.ofPattern("hh:mm a")); // 12-hour
        } catch (Exception e) {
            throw new Exception("Invalid date or time format! Use yyyy-MM-dd and hh:mm AM/PM.");
        }
        this.completed = false;
        this.alarmSet = false;
    }

    public LocalDateTime getDeadlineDateTime() {
        return LocalDateTime.of(deadlineDate, deadlineTime);
    }

    public void markCompleted() { completed = true; }

    public long hoursLeft() {
        return ChronoUnit.HOURS.between(LocalDateTime.now(), getDeadlineDateTime());
    }

    public String getStatus() {
        if (completed) return "✅ Completed";
        if (LocalDateTime.now().isAfter(getDeadlineDateTime())) return "⚠ Overdue";
        return "⏳ Incomplete";
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        return String.format("%-20s | %-12s | %-8s | %-12s | %-6d hrs left",
                name,
                deadlineDate.format(dateFormatter),
                deadlineTime.format(timeFormatter),
                getStatus(),
                Math.max((int)hoursLeft(), 0));
    }
}

// Main backend
public class backend {
    static Scanner s = new Scanner(System.in);
    static ArrayList<Goal> goals = new ArrayList<>();
    static Timer timer = new Timer(true);
    static DateTimeFormatter menuTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a");

    public static void main(String[] args) {
        loadData();

        System.out.println("===================================================");
        System.out.println("           WELCOME TO CAREER PLANNER                ")
        System.out.println("===================================================");

        System.out.print("Enter your full name: ");
        String fullName = s.nextLine();

        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1: addGoalWithAlarm(); break;
                case 2: markGoalCompleted(); break;
                case 3: viewGoals(); break;
                case 4: deleteGoal(); break;
                case 5: saveData(); System.out.println("Goodbye! Have a productive day!"); System.exit(0);
                default: System.out.println("⚠ Invalid choice! Please select a valid option."); break;
            }
        }
    }

    // ---------------- Menu Display ----------------
    static void displayMenu() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n----------------------------------------------------");
        System.out.println("              MENU (" + now.format(menuTimeFormat) + ")");
        System.out.println("----------------------------------------------------");
        System.out.println("1. 📝 Add Goal with Alarm");
        System.out.println("2. ✅ Mark Goal Completed");
        System.out.println("3. 📋 View All Goals");
        System.out.println("4. 🗑 Delete a Goal");
        System.out.println("5. 🚪 Exit");
    }

    // ---------------- Input Handling ----------------
    static int getIntInput(String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = s.nextInt();
                s.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("⚠ Invalid input! Please enter a number.");
                s.nextLine();
            }
        }
        return value;
    }

    // ---------------- Goal Methods ----------------
    static void addGoalWithAlarm() {
        try {
            System.out.print("Goal Name: ");
            String name = s.nextLine();
            System.out.print("Deadline Date (yyyy-MM-dd): ");
            String dateStr = s.nextLine();
            System.out.print("Deadline Time (hh:mm AM/PM): ");
            String timeStr = s.nextLine();

            Goal g = new Goal(name, dateStr, timeStr);
            goals.add(g);
            System.out.println("✅ Goal '" + name + "' added successfully!");

            scheduleAlarm(g);

        } catch (Exception e) {
            System.out.println("⚠ Error: " + e.getMessage());
        }
    }

    static void scheduleAlarm(Goal g) {
        long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), g.getDeadlineDateTime().minusHours(1));
        if (delay > 0 && !g.alarmSet) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("\n🔔 REMINDER: Goal '" + g.name + "' is due in 1 hour!");
                }
            }, delay);
            g.alarmSet = true;
        }
    }

    static void markGoalCompleted() {
        if (goals.isEmpty()) { System.out.println("⚠ No goals added."); return; }
        listGoals();
        int idx = getIntInput("Select goal number to mark as completed: ");
        if (idx >= 1 && idx <= goals.size()) {
            goals.get(idx-1).markCompleted();
            System.out.println("✅ Goal marked as completed!");
        } else System.out.println("⚠ Invalid number!");
    }

    static void viewGoals() {
        if (goals.isEmpty()) { System.out.println("⚠ No goals added yet."); return; }
        System.out.println("\n------------------- YOUR GOALS -------------------");
        System.out.printf("%-5s | %-20s | %-12s | %-8s | %-12s | %-10s\n", "No.", "Name", "Date", "Time", "Status", "Hrs Left");
        System.out.println("-------------------------------------------------------------");
        for (int i = 0; i < goals.size(); i++)
            System.out.printf("%-5d | %s\n", i+1, goals.get(i).toString());
    }

    static void deleteGoal() {
        if (goals.isEmpty()) { System.out.println("⚠ No goals to delete."); return; }
        listGoals();
        int idx = getIntInput("Enter goal number to delete: ");
        if (idx >= 1 && idx <= goals.size()) {
            System.out.println("🗑 Goal '" + goals.get(idx-1).name + "' deleted.");
            goals.remove(idx-1);
        } else System.out.println("⚠ Invalid number!");
    }

    static void listGoals() {
        for (int i = 0; i < goals.size(); i++)
            System.out.printf("%d. %s (%s)\n", i+1, goals.get(i).name, goals.get(i).getStatus());
    }

    // ---------------- File Handling ----------------
    static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("career_goals.dat"))) {
            oos.writeObject(goals);
        } catch (IOException e) {
            System.out.println("⚠ Error saving data: " + e.getMessage());
        }
    }

    static void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("career_goals.dat"))) {
            goals = (ArrayList<Goal>) ois.readObject();
            for (Goal g : goals) scheduleAlarm(g);
        } catch (Exception e) {
            goals = new ArrayList<>();
        }
    }
}

