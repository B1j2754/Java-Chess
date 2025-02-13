// TODO Castling
// TODO win condition
// TODO sound fx?

package Chess;

import javax.swing.JFrame; // Framing of your interface
import javax.swing.JPanel; // Size of your interface
import javax.swing.JButton; // Buttons
import javax.swing.JLabel; // Labels
import javax.swing.*;
import javax.swing.BorderFactory; // Border group

import java.awt.event.ActionEvent; // 1/2 button click functionality
import java.awt.event.ActionListener; // 2/2 button click functionality
import javax.swing.*;
import javax.swing.ImageIcon; // Use for images

// Layout for border
// Layout for elements
import java.awt.*;

import java.util.Arrays; // For array management
import java.util.ArrayList; // For dynamic arrays
import java.util.Collections; // For sorting
import java.util.HashMap;

import java.math.*;

class Tuple<A, B> {
    public A first;
    public B second;

    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return first.equals(tuple.first) && second.equals(tuple.second);
    }

    @Override
    public int hashCode() {
        return 31 * first.hashCode() + second.hashCode();
    }
}

public class Chess implements ActionListener {
    char[] pieces = "♔♕♖♗♘♙♚♛♜♝♞♟".toCharArray(); // get index of piece, mod by 6; add 6 to get 2nd layer
    char[] wpieces = "♔♕♖♗♘♙".toCharArray();
    char[] bpieces = "♚♛♜♝♞♟".toCharArray();
    static int sizex = 8, sizey = 8;

    @SuppressWarnings("unchecked")
    Tuple<JButton, Character>[][] board = new Tuple[sizex][sizey];
    char[][] initialBoard = {
        {'♜', '♞', '♝', '♛', '♚', '♝', '♞', '♜'},
        {'♟', '♟', '♟', '♟', '♟', '♟', '♟', '♟'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {'♙', '♙', '♙', '♙', '♙', '♙', '♙', '♙'},
        {'♖', '♘', '♗', '♕', '♔', '♗', '♘', '♖'}
    };    

    // Gui elements
    JFrame frame_object;
    JPanel panel_object;

    // Board interface variables
    int prevx = -1;
    int prevy = -1;
    boolean selectMove = false;

    ArrayList<Tuple<Integer, Integer>> possibleMoves = new ArrayList<>();

    // Player variables
    char player = 'w';

    public Chess() {
        // Set up frame object
        frame_object = new JFrame(); 
        panel_object = new JPanel();

        // Set up panel layout
        panel_object.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        panel_object.setLayout(new GridLayout(8, 8));

        // Create chess-board buttons
        for(int x = 0; x < sizex; x++){
            for(int y = 0; y < sizey; y++){
                board[x][y] = new Tuple<>(new JButton(), initialBoard[x][y]);
                board[x][y].first = new JButton(String.valueOf(board[x][y].second));
                board[x][y].first.addActionListener(this);

                // Change bg color based on coordinate point
                if(((x + y) % 2) == 0){
                    board[x][y].first.setBackground(Color.WHITE);
                } else {
                    board[x][y].first.setBackground(Color.BLACK);
                }
                board[x][y].first.setPreferredSize(new Dimension(60, 60)); // Make uniform shape
                board[x][y].first.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 25)); // Increase font size
                board[x][y].first.setFocusable(false);
                panel_object.add(board[x][y].first);
            }
        }

        // Setup frame
        frame_object.add(panel_object, BorderLayout.CENTER);
        frame_object.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_object.setTitle("Chess");
        frame_object.pack();
        frame_object.setVisible(true);
    }

    char getColor(char character){
        // Analyze further to see if its our own piece or not
        if(new String(wpieces).indexOf(character) != -1){
            return 'w';
        } else {
            return 'b';
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Find the pressed chess square
        for(int x = 0; x < sizex; x++){
            for(int y = 0; y < sizey; y++){
                if(e.getSource() == board[x][y].first){
                    // Clear suggestions
                    clearMoves();

                    // Check if user is UN-selecting piece (recurring click)
                    if(selectMove && prevx == x && prevy == y){
                        selectMove = false;
                        return;
                    } else if(possibleMoves.contains(new Tuple<Integer, Integer>(x, y))){
                        // Update button text
                        board[x][y].first.setText(board[prevx][prevy].first.getText());
                        board[prevx][prevy].first.setText(" ");

                        // Reset selection state
                        selectMove = false;
                        possibleMoves.clear();

                        // Update piece value
                        board[x][y].second = board[prevx][prevy].second;
                        board[prevx][prevy].second = ' ';

                        // Check for pawn promotion
                        if(new String(pieces).indexOf(board[x][y].second) % 6 == 5){
                            System.out.println("WOAH");
                            System.out.println(x);
                            if(x == 0 || x == 7){
                                if(getColor(board[x][y].second) == 'w'){
                                    board[x][y].first.setText("♕");
                                    board[x][y].second = '♕';
                                } else {
                                    board[x][y].first.setText("♛");
                                    board[x][y].second = '♛';
                                }
                            }
                        }

                        // Update turn
                        if(player == 'w'){
                            player = 'b';
                        } else {
                            player = 'w';
                        }

                        return;
                    } else {
                        // Get color and piece type
                        char color = getColor(board[x][y].second);

                        if(player == color) {
                            // Update found piece's coords
                            prevx = x;
                            prevy = y;
                            selectMove = true;
                            
                            // Display possible moves
                            displayMoves(color, new String(pieces).indexOf(board[x][y].second) % 6, prevx, prevy);
                            break;
                        }
                    }
                }
            }
        }
    }

    static Color tintColor(JButton button, int[] tintColor) {
        // Get the original background color of the button
        Color originalColor = button.getBackground();

        // Extract RGB values of the original color
        int r = originalColor.getRed();
        int g = originalColor.getGreen();
        int b = originalColor.getBlue();

        // Apply tint depending on the specified color
        r += tintColor[0];
        r %= 225;
        r = Math.abs(r);
        g += tintColor[1];
        g %= 225;
        g = Math.abs(g);
        b += tintColor[2];
        b %= 225;
        b = Math.abs(b);

        // Return the new color with the applied tint
        return new Color(r, g, b);
    }

    ArrayList<Tuple<Integer, Integer>> transform(ArrayList<Tuple<Integer, Integer>> initial, char color){
        if (color == 'w') {  // For white pieces, flip the row direction
            ArrayList<Tuple<Integer, Integer>> transformed = new ArrayList<>();
            for (Tuple<Integer, Integer> coordinate : initial) {
                transformed.add(new Tuple<>(-1 * coordinate.first, coordinate.second));
            }
            return transformed;
        } else {
            return initial;
        }
    }

    void displayMoves(char color, int type, int x, int y){
        // Create thing to store squares to be eval'd
        ArrayList<Tuple<Integer, Integer>> squares = new ArrayList<>();

        // Depending on piece, calculate moves
        ArrayList<Tuple<Integer, Integer>> moves = new ArrayList<>();
        Tuple<Boolean, Integer> info;
        Tuple<Integer, Integer> move;

        // Set up variables for pieces that need to move an undefinite amount of spaces
        int[][] increments;
        boolean stop;
        int xinc;
        int yinc;
        moves.clear(); // Clear past move possiblities
        squares.clear();

        switch(type){
            case 0: // King
                moves.add(new Tuple(1, 0));
                moves.add(new Tuple(1, 1));
                moves.add(new Tuple(1, -1));
                moves.add(new Tuple(0, 1));
                moves.add(new Tuple(0, -1));
                moves.add(new Tuple(-1, 0));
                moves.add(new Tuple(-1, 1));
                moves.add(new Tuple(-1, -1));
                moves = new ArrayList<>(transform(moves, color));

                // Loop through and validify each move
                for(Tuple<Integer, Integer> imove: moves){
                    move = imove;
                    info = validify(x + move.first, y + move.second);
                    if(info.first && (info.second == 1 | info.second == 2)){
                        squares.add(new Tuple<>(move.first, move.second));
                    }
                }

                break;
            case 1: // Queen
                increments = new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,-1},{1,-1},{-1,1}}; // All direction increments
                stop = false; // Stop marker
                xinc = 0; // X-increment
                yinc = 0; // Y-increment

                for (int[] increment : increments) {
                    // Reset vars
                    stop = false;
                    xinc = 0;
                    yinc = 0;
                    while(!(stop)){
                        xinc += increment[0];
                        yinc += increment[1];
                        moves.clear();
                        moves.add(new Tuple<>(xinc, yinc));
                        moves = new ArrayList<>(transform(moves, color));
                        info = validify(x + moves.get(0).first, y + moves.get(0).second);
                        if(info.first && info.second == 1){
                            squares.add(new Tuple<>(moves.get(0).first, moves.get(0).second));
                        } else if(info.first && info.second == 2) {
                            squares.add(new Tuple<>(moves.get(0).first, moves.get(0).second));
                            stop = true;
                        } else {
                            stop = true;
                        }
                    }
                }

                break;
            case 2: // Rook
                increments = new int[][]{{1,0},{-1,0},{0,1},{0,-1}}; // All direction increments
                stop = false; // Stop marker
                xinc = 0; // X-increment
                yinc = 0; // Y-increment

                for (int[] increment : increments) {
                    // Reset vars
                    stop = false;
                    xinc = 0;
                    yinc = 0;
                    while(!(stop)){
                        xinc += increment[0];
                        yinc += increment[1];
                        moves.clear();
                        moves.add(new Tuple<>(xinc, yinc));
                        moves = new ArrayList<>(transform(moves, color));
                        info = validify(x + moves.get(0).first, y + moves.get(0).second);
                        if(info.first && info.second == 1){
                            squares.add(new Tuple<>(moves.get(0).first, moves.get(0).second));
                        } else if(info.first && info.second == 2) {
                            squares.add(new Tuple<>(moves.get(0).first, moves.get(0).second));
                            stop = true;
                        } else {
                            stop = true;
                        }
                    }
                }

                break;
            case 3: // Bishop
                increments = new int[][]{{1,1},{-1,-1},{1,-1},{-1,1}}; // All direction increments
                stop = false; // Stop marker
                xinc = 0; // X-increment
                yinc = 0; // Y-increment

                for (int[] increment : increments) {
                    // Reset vars
                    stop = false;
                    xinc = 0;
                    yinc = 0;
                    while(!(stop)){
                        xinc += increment[0];
                        yinc += increment[1];
                        moves.clear();
                        moves.add(new Tuple<>(xinc, yinc));
                        moves = new ArrayList<>(transform(moves, color));
                        info = validify(x + moves.get(0).first, y + moves.get(0).second);
                        if(info.first && info.second == 1){
                            squares.add(new Tuple<>(moves.get(0).first, moves.get(0).second));
                        } else if(info.first && info.second == 2) {
                            squares.add(new Tuple<>(moves.get(0).first, moves.get(0).second));
                            stop = true;
                        } else {
                            stop = true;
                        }
                    }
                }

                break;
            case 4: // Knight
                moves.add(new Tuple(2, 1));
                moves.add(new Tuple(2, -1));
                moves.add(new Tuple(-2, 1));
                moves.add(new Tuple(-2, -1));
                moves.add(new Tuple(1, 2));
                moves.add(new Tuple(1, -2));
                moves.add(new Tuple(-1, 2));
                moves.add(new Tuple(-1, -2));
                moves = new ArrayList<>(transform(moves, color));

                // Loop through and validify each move
                for(Tuple<Integer, Integer> imove: moves){
                    move = imove;
                    info = validify(x + move.first, y + move.second);
                    if(info.first && (info.second == 1 | info.second == 2)){
                        squares.add(new Tuple<>(move.first, move.second));
                    }
                }

                break;
            case 5: // Pawn
                moves.add(new Tuple(1, 0));
                moves.add(new Tuple(1, 1));
                moves.add(new Tuple(1, -1));
                if(x == 1 | x == 6){
                    moves.add(new Tuple(2, 0)); // Double move forwards only availible on first layer
                }
                moves = new ArrayList<>(transform(moves, color));

                move = moves.get(0);
                info = validify(x + move.first, y + move.second);
                if(info.first && info.second == 1){
                    squares.add(new Tuple<>(move.first, move.second));
                }
                
                move = moves.get(1);
                info = validify(x + move.first, y + move.second);
                if(info.first && info.second == 2){
                    squares.add(new Tuple<>(move.first, move.second));
                }
                
                move = moves.get(2);
                info = validify(x + move.first, y + move.second);
                if(info.first && info.second == 2){
                    squares.add(new Tuple<>(move.first, move.second));
                }
                
                if(moves.size() > 3){
                    move = moves.get(3);
                    info = validify(x + move.first, y + move.second);
                    if(info.first && info.second == 1){
                        squares.add(new Tuple<>(move.first, move.second));
                    }
                }

                break;
        }

        // Iterate over squares, validate and color accordingly
        for (Tuple<Integer, Integer> coordinate : squares){
            info = validify(x + coordinate.first, y + coordinate.second);
            if(info.first) {
                if (info.second == 1){
                    JButton button = board[x + coordinate.first][y + coordinate.second].first;
                    board[x + coordinate.first][y + coordinate.second].first.setBackground(tintColor(button, new int[]{0, 150, 0}));
                } else if (info.second == 2){
                    JButton button = board[x + coordinate.first][y + coordinate.second].first;
                    board[x + coordinate.first][y + coordinate.second].first.setBackground(tintColor(button, new int[]{150, 0, 0}));
                }
            }
        }

        // Save possible moves for next click check
        possibleMoves = new ArrayList<>();
        for (Tuple<Integer, Integer> coordinate : squares){
            possibleMoves.add(new Tuple<>(x + coordinate.first, y + coordinate.second));
        }
    }

    void clearMoves(){
        for(int x = 0; x < sizex; x++){
            for(int y = 0; y < sizey; y++){
                // Change bg color based on coordinate point
                if(((x + y) % 2) == 0){
                    board[x][y].first.setBackground(Color.WHITE);
                } else {
                    board[x][y].first.setBackground(Color.BLACK);
                }
            }
        }
    }

    Tuple<Boolean, Integer> validify(int x, int y){
        // return if its valid or not, and the corresponding value: 0 --> your own piece occupies, 1 --> possible, 2 --> take piece
        if(x < 0 || x > 7 || y < 0 || y > 7){ // Off the board
            return new Tuple<Boolean, Integer>(false, null);
        } else if(board[x][y].second == ' '){ // Free spot
            return new Tuple<Boolean, Integer>(true, 1);
        } else {
            // Check if it belongs to current player or not
            char color = getColor(board[x][y].second);

            // If color is the same as current player, it is an invalid move
            if(color == player){
                return new Tuple<Boolean, Integer>(false, 0);
            } else { // Else it is opponent piece and can be taken
                return new Tuple<Boolean, Integer>(true, 2);
            }
        }
    }

    public static void main(String[] args) {
        new Chess();
    }
}
