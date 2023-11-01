package interactions;

import model.Maze;
import java.util.InputMismatchException;
import java.util.Scanner;
import static java.lang.Integer.parseInt;

public class ConsoleInteraction {


    /**
     * Scanner is used for reading user input.
     */
    private Scanner scanner;

    /**
     * The current maze. It is null when the game starts.
     * {@code isMazeAvailable} indicates whether the
     * maze exists.
     */
    private Maze maze;

    /**
     * Tells if the Maze is generated or not
     */
    private boolean isMazeAvailable = false;


    /**
     * An endless loop that prints available options and
     * processes user input. All available options are:
     * <p>
     * 1. Generate a new maze<br>
     * 2. Display the maze<br>
     * 3. Find the escape<br>
     * 4. Exit <br>
     * <p>
     * The option 2 and 3 are available only if
     * {@code isMazeAvailable == true}.
     */
    public void start() {
        scanner = new Scanner(System.in);
        while (true) {

            System.out.println("\n\n=============== Menu ===============");
            System.out.println("1. Generate a new maze");
            if (isMazeAvailable) {
                System.out.println("2. Display the maze");
                System.out.println("3. Find the escape");
            }
            System.out.println("4. Exit");
            System.out.print("Enter your Choice : ");
            try {
                var choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> generate();
                    case 2 -> display();
                    case 3 -> findEscape();
                    case 4 -> {
                        exit();
                        return;
                    }
                    default -> System.out.println("Incorrect option. Please try again");
                }
            } catch (InputMismatchException e) {
                System.out.println("Incorrect option. Please try again");
            }
        }
    }

    /**
     * Closes an input and finishes the game.
     */
    private void exit() {
        scanner.close();
        System.out.println("Bye!");
    }

    /**
     * Asks a user to enter the dimensions of the new maze
     * and then generates and prints the new one.
     */
    private void generate() {
        System.out.print("Enter the size of the new maze (in the [size] or [height width] format) : ");
        var line = scanner.nextLine();
        var split = line.split(" ");
        if (split.length == 1) {
            var size = parseInt(split[0]);
            maze = new Maze(size);
        } else if (split.length == 2) {
            var height = parseInt(split[0]);
            var width = parseInt(split[1]);
            maze = new Maze(height, width);
        } else {
            System.out.println("Cannot generate a maze. Invalid size");
        }
        isMazeAvailable = true;
        display();
    }

    /**
     * Prints the current maze.
     */
    private void display() {
        System.out.println(maze);
    }

    /**
     * Prints the maze with its path from the entrance to the exit.
     */
    private void findEscape() {
        System.out.println(maze.findEscape());
    }
}
