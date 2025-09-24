import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException; // for input errors
import java.io.FileWriter; // for writing resume to file
import java.io.IOException; // for handling file errors

class Goal {
    String goal;
    String endtime;
    ArrayList<String> steps = new ArrayList<>(); // for storing the subgoals
}

class Skills {
    ArrayList<String> completedSubgoals = new ArrayList<>(); // completed subgoals
    ArrayList<String> incompleteSubgoals = new ArrayList<>(); // incompleted subgoals
    ArrayList<String> completedGoals = new ArrayList<>(); // completed main goals
    ArrayList<String> incompleteGoals = new ArrayList<>(); // incompleted main goals
}

class Resume {
    String name;
    String email;
    String phone;
    String education;
    String experience;
}

public class Main {
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);

        ArrayList<Goal> goals = new ArrayList<>(); // for storing main goals
        Skills sk = new Skills(); // object for storing completed/incompleted goals & subgoals

        while (true) {
            try {
                // Menu options
                System.out.println("\n=== MENU ===");
                System.out.println("1. Add Goals");
                System.out.println("2. Display All Goals and Subgoals");
                System.out.println("3. Mark Subgoals as Completed/Not Completed");
                System.out.println("4. Display Completed Goals");
                System.out.println("5. Display Incompleted Goals");
                System.out.println("6. Create Resume");
                System.out.println("7. Exit");
                System.out.print("Enter choice: ");

                int choice = s.nextInt();
                s.nextLine();

                switch (choice) {
                    case 1: // adding main goals
                        try {
                            System.out.print("Enter number of goals: ");
                            int noGoals = s.nextInt();
                            s.nextLine();

                            // loop for main goals
                            for (int i = 0; i < noGoals; i++) {
                                Goal g = new Goal();
                                System.out.print("Enter goal " + (i + 1) + ": ");
                                g.goal = s.nextLine();

                                System.out.print("Enter end time: ");
                                g.endtime = s.nextLine();

                                System.out.print("Enter number of subgoals: ");
                                int noSteps = s.nextInt();
                                s.nextLine();

                                // loop for subgoals
                                for (int j = 0; j < noSteps; j++) {
                                    System.out.print("Enter subgoal " + (j + 1) + ": ");
                                    g.steps.add(s.nextLine());
                                }
                                goals.add(g); // add goal into goals arraylist
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("⚠ Error: Please enter numbers only for counts!");
                            s.nextLine(); // clear wrong input
                        }
                        break;

                    case 2: // displaying all main goals and their subgoals
                        if (goals.isEmpty()) {
                            System.out.println("⚠ No goals available. Add some first.");
                        } else {
                            System.out.println("\n=== Goals and Subgoals ===");
                            for (int i = 0; i < goals.size(); i++) { // loop for main goals
                                Goal g = goals.get(i);
                                System.out.println((i + 1) + ". " + g.goal + " (End time: " + g.endtime + ")");
                                for (int j = 0; j < g.steps.size(); j++) { // loop for subgoals
                                    System.out.println("   - " + g.steps.get(j)); // printing subgoals
                                }
                            }
                        }
                        break;

                    case 3: // marking subgoals as completed/not completed
                        if (goals.isEmpty()) {
                            System.out.println("⚠ No goals to mark. Add goals first.");
                        } else {
                            for (int i = 0; i < goals.size(); i++) { // loop for main goals
                                Goal g = goals.get(i);
                                boolean allCompleted = true; // flag for checking if all subgoals are completed

                                System.out.println("\nMarking subgoals for: " + g.goal);
                                for (int j = 0; j < g.steps.size(); j++) { // loop for subgoals
                                    try {
                                        System.out.print("Is subgoal '" + g.steps.get(j) + "' completed? (true/false): ");
                                        boolean completed = s.nextBoolean();

                                        if (completed) {
                                            sk.completedSubgoals.add(g.steps.get(j)); // add to completed subgoals
                                        } else {
                                            sk.incompleteSubgoals.add(g.steps.get(j)); // add to incompleted subgoals
                                            allCompleted = false; // at least one subgoal is not completed
                                        }
                                    } catch (InputMismatchException e) {
                                        System.out.println("⚠ Error: Enter only true or false.");
                                        s.nextLine(); // clear buffer
                                        j--; // repeat same subgoal
                                    }
                                }

                                // check if all subgoals completed → add goal to completed goals
                                if (allCompleted) {
                                    sk.completedGoals.add(g.goal);
                                } else {
                                    sk.incompleteGoals.add(g.goal); // otherwise add goal to incomplete goals
                                }
                            }
                        }
                        break;

                    case 4: // displaying completed goals
                        if (sk.completedGoals.isEmpty()) {
                            System.out.println("⚠ No completed goals yet.");
                        } else {
                            System.out.println("\n✅ Completed Goals:");
                            for (int i = 0; i < sk.completedGoals.size(); i++) { // loop for completed goals
                                System.out.println("- " + sk.completedGoals.get(i));
                            }
                        }
                        break;

                    case 5: // displaying incompleted goals
                        if (sk.incompleteGoals.isEmpty()) {
                            System.out.println("⚠ No incompleted goals.");
                        } else {
                            System.out.println("\n❌ Incompleted Goals:");
                            for (int i = 0; i < sk.incompleteGoals.size(); i++) { // loop for incompleted goals
                                System.out.println("- " + sk.incompleteGoals.get(i));
                            }
                        }
                        break;

                    case 6: // creating resume
                        try {
                            Resume r = new Resume();
                            System.out.print("Enter your name: ");
                            r.name = s.nextLine();

                            System.out.print("Enter your email: ");
                            r.email = s.nextLine();

                            System.out.print("Enter your phone number: ");
                            r.phone = s.nextLine();

                            System.out.print("Enter your education: ");
                            r.education = s.nextLine();

                            System.out.print("Enter your work experience: ");
                            r.experience = s.nextLine();

                            // writing resume to file
                            FileWriter fw = new FileWriter("resume.txt");
                            fw.write("===== RESUME =====\n");
                            fw.write("Name: " + r.name + "\n");
                            fw.write("Email: " + r.email + "\n");
                            fw.write("Phone: " + r.phone + "\n");
                            fw.write("Education: " + r.education + "\n");
                            fw.write("Experience: " + r.experience + "\n");

                            // asking user if they want to add skills
                            System.out.print("Would you like to add the skills you have learned? (yes/no): ");
                            String addSkills = s.nextLine();

                            if (addSkills.equalsIgnoreCase("yes")) {
                                fw.write("\nSkills Learned:\n");

                                // loop for completed goals
                                for (int i = 0; i < sk.completedGoals.size(); i++) {
                                    fw.write("- " + sk.completedGoals.get(i) + "\n");
                                }

                                // loop for completed subgoals
                                for (int j = 0; j < sk.completedSubgoals.size(); j++) {
                                    fw.write("- " + sk.completedSubgoals.get(j) + "\n");
                                }
                            }

                            fw.close(); // closing file writer
                            System.out.println("✅ Resume created successfully in 'resume.txt'.");
                        } catch (IOException e) {
                            System.out.println("⚠ Error writing resume file: " + e.getMessage());
                        }
                        break;

                    case 7: // exit
                        System.out.println("Exiting program...");
                        return;

                    default: // invalid menu choice
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("⚠ Error: Please enter only numbers for menu choice!");
                s.nextLine(); // clear wrong input
            }
        }
    }
}
