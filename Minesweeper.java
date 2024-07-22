// Shuban Langadi
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {

  private class MineTile extends JButton {
    int r;
    int c;

    public MineTile(int r, int c) {
      this.r = r;
      this.c = c;
    }
  }

  int tileSize = 70;
  int numRows = 8;
  int numCols = numRows;
  int boardWidth = numCols * tileSize;
  int boardHeight = numRows * tileSize;

  JFrame frame = new JFrame("Minesweeper");
  JLabel turnLabel = new JLabel();
  JLabel player1ScoreLabel = new JLabel();
  JLabel player2ScoreLabel = new JLabel();
  JPanel scorePanel = new JPanel();
  JPanel boardPanel = new JPanel();

  int mineCount = 10;
  MineTile[][] board = new MineTile[numRows][numCols];
  ArrayList<MineTile> mineList;
  Random random = new Random();

  int playerTurn = 1;
  int player1Score = 0;
  int player2Score = 0;
  int tilesClicked = 0;
  boolean gameOver = false;

  String player1Name;
  String player2Name;

  Minesweeper(String player1Name, String player2Name) {
    this.player1Name = player1Name;
    this.player2Name = player2Name;

    frame.setSize(boardWidth, boardHeight + 50);
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());

    turnLabel.setFont(new Font("Arial", Font.BOLD, 25));
    turnLabel.setHorizontalAlignment(JLabel.CENTER);
    turnLabel.setText(player1Name + "'s Turn");
    turnLabel.setOpaque(true);

    player1ScoreLabel.setText(player1Name + ": 0");
    player2ScoreLabel.setText(player2Name + ": 0");

    scorePanel.setLayout(new BorderLayout());
    scorePanel.add(player1ScoreLabel, BorderLayout.WEST);
    scorePanel.add(turnLabel, BorderLayout.CENTER);
    scorePanel.add(player2ScoreLabel, BorderLayout.EAST);
    frame.add(scorePanel, BorderLayout.NORTH);

    boardPanel.setLayout(new GridLayout(numRows, numCols));
    frame.add(boardPanel);

    for (int r = 0; r < numRows; r++) {
      for (int c = 0; c < numCols; c++) {
        MineTile tile = new MineTile(r, c);
        board[r][c] = tile;

        tile.setFocusable(false);
        tile.setMargin(new Insets(0, 0, 0, 0));
        tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
        tile.addMouseListener(new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            if (gameOver) {
              return;
            }
            MineTile tile = (MineTile) e.getSource();

            if (e.getButton() == MouseEvent.BUTTON1) {
              if (tile.getText().equals("")) {
                if (mineList.contains(tile)) {
                  revealMines();
                  if (playerTurn == 1) {
                    gameOver(player2Name);
                  } else {
                    gameOver(player1Name);
                  }
                } else {
                  checkMine(tile.r, tile.c);
                }
              }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
              if (tile.getText().equals("") && tile.isEnabled()) {
                tile.setText(Character.toString(0x1F3F4));
              } else if (tile.getText().equals(Character.toString(0x1F3F4))) {
                tile.setText("");
              }
            }

            // Calculate points after each click
            if (!tile.getText().equals("") && !tile.getText().equals(Character.toString(0x1F4A5))) {
              int points = Integer.parseInt(tile.getText());
              if (playerTurn == 1) {
                player1Score += points;
                player1ScoreLabel.setText(player1Name + ": " + player1Score);
              } else {
                player2Score += points;
                player2ScoreLabel.setText(player2Name + ": " + player2Score);
              }
            }

            // Switch player turn
            changePlayerTurn();
          }
        });

        boardPanel.add(tile);
      }
    }

    frame.setVisible(true);

    setMines();
  }

  public void setMines() {
    mineList = new ArrayList<MineTile>();

    int mineLeft = mineCount;
    while (mineLeft > 0) {
      int r = random.nextInt(numRows);
      int c = random.nextInt(numCols);

      MineTile tile = board[r][c];
      if (!mineList.contains(tile)) {
        mineList.add(tile);
        mineLeft -= 1;
      }
    }
  }

  public void revealMines() {
    for (MineTile tile : mineList) {
        tile.setText(Character.toString(0x1F4A5));
    }
    gameOver = true;

    // Determine the winner based on the current player's turn
    if (playerTurn == 1) {
        gameOver(player2Name);
    } else {
        gameOver(player1Name);
    }
}


  public void checkMine(int r, int c) {
    if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
      return;
    }

    MineTile tile = board[r][c];
    if (!tile.isEnabled()) {
      return;
    }
    tile.setEnabled(false);
    tilesClicked += 1;

    int minesFound = 0;

    // top 3
    minesFound += countMine(r - 1, c - 1); // top left
    minesFound += countMine(r - 1, c); // top
    minesFound += countMine(r - 1, c + 1); // top right

    // left and right
    minesFound += countMine(r, c - 1); // left
    minesFound += countMine(r, c + 1); // right

    // bottom 3
    minesFound += countMine(r + 1, c - 1); // bottom left
    minesFound += countMine(r + 1, c); // bottom
    minesFound += countMine(r + 1, c + 1); // bottom right

    if (minesFound > 0) {
      tile.setText(Integer.toString(minesFound));
    } else {
      tile.setText("");

      // top 3
      checkMine(r - 1, c - 1); // top left
      checkMine(r - 1, c); // top
      checkMine(r - 1, c + 1); // top right

      // left and right
      checkMine(r, c - 1); // left
      checkMine(r, c + 1); // right

      // bottom 3
      checkMine(r + 1, c - 1); // bottom left
      checkMine(r + 1, c); // bottom
      checkMine(r + 1, c + 1); // bottom right
    }

    if (tilesClicked == numRows * numCols - mineList.size()) {
      if (player1Score  > player2Score) {
          gameOver(player1Name);
        } else if (player1Score < player2Score) {
          gameOver(player2Name);
        } else {
          gameOver("It's a draw!");
        }
      }
    }


  public int countMine(int r, int c) {
    if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
      return 0;
    }
    if (mineList.contains(board[r][c])) {
      return 1;
    }
    return 0;
  }

  public void changePlayerTurn() {
    if (gameOver) {
        if (playerTurn == 1) {
            gameOver(player2Name);
        } else {
            gameOver(player2Name);
        }
    } else {
        if (playerTurn == 1) {
            playerTurn = 2;
            turnLabel.setText( player2Name + "'s Turn");
        } else {
            playerTurn = 1;
            turnLabel.setText( player1Name + "'s Turn");
        }

    }


  }

  public void gameOver(String winner) {
    gameOver = true;
    turnLabel.setText(winner + " Wins!!");
  }

}