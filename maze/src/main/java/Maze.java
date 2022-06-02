
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Maze extends JFrame {

    private int[][] values;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;

    public Maze(int algorithm, int size, int startRow, int startColumn) {
        this.algorithm = algorithm;
        Random random = new Random();
        this.values = new int[size][];
        for (int i = 0; i < values.length; i++) {
            int[] row = new int[size];
            for (int j = 0; j < row.length; j++) {
                if (i > 1 || j > 1) {
                    row[j] = random.nextInt(8) % 7 == 0 ? Definitions.OBSTACLE : Definitions.EMPTY;
                } else {
                    row[j] = Definitions.EMPTY;
                }
            }
            values[i] = row;
        }
        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setLocationRelativeTo(null);
        GridLayout gridLayout = new GridLayout(rows, columns);
        this.setLayout(gridLayout);
        for (int i = 0; i < rows * columns; i++) {
            int value = values[i / rows][i % columns];
            JButton jButton = new JButton(String.valueOf(i));
            if (value == Definitions.OBSTACLE) {
                jButton.setBackground(Color.BLACK);
            } else {
                jButton.setBackground(Color.WHITE);
            }
            this.buttonList.add(jButton);
            this.add(jButton);
        }
        this.setVisible(true);
        this.setSize(Definitions.WINDOW_WIDTH, Definitions.WINDOW_HEIGHT);
        this.setResizable(false);
    }

    public void checkWayOut() {
        new Thread(() -> {
            boolean result = false;
            switch (this.algorithm) {
                case Definitions.ALGORITHM_DFS:
                    result = run(buttonList.get(0));
                    break;
                case Definitions.ALGORITHM_BFS:
                    break;
            }
            JOptionPane.showMessageDialog(null,  result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");

        }).start();
    }

    public boolean run(JButton root) {
        Stack<JButton> myStack = new Stack<>();
        myStack.add(root);
        while (!myStack.isEmpty()) {
            JButton currentNode = myStack.pop();
            int index = buttonList.indexOf(currentNode);
            int i = index / rows;
            int j = index % rows;
            boolean isObstacle = currentNode.getBackground() == Color.BLACK;

            if (!isObstacle) {
                if(!visited[i][j]) {
                    setSquareAsVisited(i ,j,true);
                    if (currentNode.getText().equals(String.valueOf((rows * rows) -1))) {
                        return true;
                    }
                    for (JButton neighbor : getAllNeighbors(currentNode)) {
                        if(!isVisited(neighbor)) {
                            myStack.add(neighbor);
                        }
                    }
                }
            }

        }
        return false;
    }

    public ArrayList<JButton> getAllNeighbors(JButton currentNode) {
        ArrayList<JButton> neighbors = new ArrayList<>();

        int index = buttonList.indexOf(currentNode);
        int i = index / rows;
        int j = index % rows;

        //neighbor - TOP
        if (i != 0) {
            neighbors.add(buttonList.get(index-rows));
        }

        // neighbor - BOTTOM
        if (i != rows -1) {
            neighbors.add(buttonList.get(index+rows));
        }

        // neighbors - LEFT
        if (j!=0) {
            neighbors.add(buttonList.get(index-1));

        }

        // neighbors - LEFT
        if (j != rows -1) {
            neighbors.add(buttonList.get(index+1));

        }

        return neighbors;
    }

    public boolean isVisited(JButton node) {
        int index = buttonList.indexOf(node);
        int i = index / rows;
        int j = index % rows;

        return visited[i][j];
    }

    public void setSquareAsVisited(int x, int y, boolean visited) {
        try {
            if (visited) {
                if (this.backtracking) {
                    Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE * 5);
                    this.backtracking = false;
                }
                this.visited[x][y] = true;
                for (int i = 0; i < this.visited.length; i++) {
                    for (int j = 0; j < this.visited[i].length; j++) {
                        if (this.visited[i][j]) {
                            if (i == x && y == j) {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.RED);
                            } else {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.BLUE);
                            }
                        }
                    }
                }
            } else {
                this.visited[x][y] = false;
                this.buttonList.get(x * this.columns + y).setBackground(Color.WHITE);
                Thread.sleep(Definitions.PAUSE_BEFORE_BACKTRACK);
                this.backtracking = true;
            }
            if (!visited) {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE / 4);
            } else {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
