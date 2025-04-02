import java.util.*;

public class MazeGenerator {
    private static final char WALL = '#';
    private static final char PATH = '.';
    private static final char START = 'S';
    private static final char EXIT = 'E';
    private static final int[] DX = {0, 0, -1, 1};
    private static final int[] DY = {-1, 1, 0, 0};
    
    private int rows, cols;
    private char[][] maze;
    private Random random = new Random();
    
    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.maze = new char[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            Arrays.fill(maze[i], WALL);
        }
        
        generateMaze(1, 1);
        
        maze[1][1] = START;
        maze[rows - 2][cols - 2] = EXIT;
    }
    
    private void generateMaze(int x, int y) {
        maze[x][y] = PATH;
        List<Integer> directions = Arrays.asList(0, 1, 2, 3);
        Collections.shuffle(directions, random);
        
        for (int dir : directions) {
            int nx = x + DX[dir] * 2;
            int ny = y + DY[dir] * 2;
            
            if (isValid(nx, ny)) {
                maze[x + DX[dir]][y + DY[dir]] = PATH;
                generateMaze(nx, ny);
            }
        }
    }
    
    private boolean isValid(int x, int y) {
        return x > 0 && y > 0 && x < rows - 1 && y < cols - 1 && maze[x][y] == WALL;
    }
    
    public void printMaze() {
        for (char[] row : maze) {
            System.out.println(new String(row));
        }
    }
    
    public static void main(String[] args) {
        int N = 7, M = 7;
        MazeGenerator maze = new MazeGenerator(N, M);
        maze.printMaze();
    }
}
