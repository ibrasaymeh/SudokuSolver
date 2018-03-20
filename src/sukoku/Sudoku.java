package sukoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import javafx.util.Pair;

public class Sudoku {

    private int[][] board;
    private boolean solved;
    private Pair<Integer, Integer> initialEmptyCoordinates;

    public int getBoardValue(Pair<Integer, Integer> coordinates) {
        return this.board[getYAxisValue(coordinates)][getXAxisValue(coordinates)];
    }

    public void setBoardValue(Pair<Integer, Integer> coordinates, int value) {
        this.board[getYAxisValue(coordinates)][getXAxisValue(coordinates)] = value;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public Pair<Integer, Integer> getInitialEmptyCoordinates() {
        return initialEmptyCoordinates;
    }

    public void setInitialEmptyCoordinates(Pair<Integer, Integer> initialEmptyCoordinates) {
        this.initialEmptyCoordinates = initialEmptyCoordinates;
    }

    public static void main(String[] args) {

        System.out.println("Start");
        Sudoku s = new Sudoku();
        if (s.importedPuzzle()) {
            System.out.println("Imported puzzle");
            s.setSolved(false);
            s.setInitialEmptyCoordinates(s.findNextEmptyCoordinates());
            s.solve(s.getInitialEmptyCoordinates());
            s.exportPuzzle();
        }
        System.out.println("End");

    }

    public Sudoku() {
        zeroInitBoard();
    }

    private void zeroInitBoard() {
        board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                setBoardValue(new Pair<>(i, j), 0);
            }
        }
    }

    private boolean importedPuzzle() {

        File file = new File("coordinates.txt");
        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        while (sc.hasNextLine()) {
            parseAndSetBoardValues(sc.nextLine());
        }
        return true;
    }

    private void parseAndSetBoardValues(String line) {
        int yAxis = Character.getNumericValue(line.charAt(0));
        int xAxis = Character.getNumericValue(line.charAt(1));
        int value = Character.getNumericValue(line.charAt(2));

        setBoardValue(new Pair<>(yAxis, xAxis), value);
    }

    private Pair<Integer, Integer> findNextEmptyCoordinates() {

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (getBoardValue(new Pair<>(i, j)) == 0) {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
    }

    private Integer getYAxisValue(Pair<Integer, Integer> coordinates) {
        return coordinates.getKey();
    }

    private Integer getXAxisValue(Pair<Integer, Integer> coordinates) {
        return coordinates.getValue();
    }

    private void solve(Pair<Integer, Integer> coordinates) {

        while(!isSolved()){
            int yAxis = getYAxisValue(coordinates);
            int xAxis = getXAxisValue(coordinates);
            int value = getBoardValue(new Pair<>(yAxis, xAxis));

            if (value == 9) {
                if(coordinates.equals(getInitialEmptyCoordinates())) System.out.println("No solutions found.");
                setBoardValue(new Pair<>(yAxis, xAxis), 0);
                return;
            }

            setBoardValue(new Pair<>(yAxis, xAxis), 1 + value);

            if(!validMove(coordinates)){
                continue;
            }

            Pair<Integer, Integer> nextEmptyCoordinates = findNextEmptyCoordinates();
            if (null == nextEmptyCoordinates) {
                setSolved(true);
            }

            solve(nextEmptyCoordinates);
        }
    }

    private boolean validMove(Pair<Integer, Integer> coordinates){

        int yAxis = getYAxisValue(coordinates);
        int xAxis = getXAxisValue(coordinates);
        int value = getBoardValue(new Pair<>(yAxis, xAxis));

        int count=0;

        //vertical check
        for (int i=0;i<9;i++){
            if(getBoardValue(new Pair<>(i, xAxis)) == value) count++;
        }

        //horizontal check
        for (int j=0;j<9;j++){
            if(getBoardValue(new Pair<>(yAxis, j)) == value) count++;
        }

        //block check
        int yBlock = yAxis/3;
        int xBlock = xAxis/3;

        for (int i=(3*yBlock);i<(3*yBlock+3);i++){
            for (int j=(3*xBlock);j<(3*xBlock+3);j++){
                if(getBoardValue(new Pair<>(i, j)) == value) count++;
            }
        }

        return (count==3);
    }

    private void exportPuzzle() {

        PrintWriter pw;
        try {
            pw = new PrintWriter("board.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                pw.print(getBoardValue(new Pair<>(i, j)) + " ");
            }
            pw.println();
        }
        pw.close();
    }

}
