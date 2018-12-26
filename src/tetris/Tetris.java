/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Thread.yield;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Tetris extends JPanel {
	
	//        [pieces] [rotations] [coordinates]
	private final Point[][][] Tetraminos = {
			// I-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
			},
			
			// J-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
			},
			
			// L-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) }
			},
			
			// O-Piece
			{
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
			},
			
			// S-Piece
			{
				{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
				{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
			},
			
			// T-Piece
			{
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) }
			},
			
			// Z-Piece
			{
				{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
				{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
			},
                        // o-Piece-Bomb
			{
				{ new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) },
				{ new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) },
				{ new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) },
				{ new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) }
			}

	};
	
        
	// tetramino colors
	private final Color[] tetraminoColors = {
		Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
	};
	
        static private JFrame f = new JFrame("Tetris");
        static private JButton bt = new JButton("Save");  
        static private JTextField hst = new JTextField();
        
	private Point pieceOrigin;
	private int currentPiece; // current piece index
	private int rotation; // rotation index
	private ArrayList<Integer> nextPieces = new ArrayList<Integer>();
	
        
        static private boolean gonnaPause = false;
	static private boolean GameOver = false;
        static private int gameSpeed = 1000;
        static private int linesCleared = 0;
        static private boolean isCrazy = false;
        static public boolean Continue = false; // helps us coose Classic or Crazy mod
        
        
        static final Tetris game = new Tetris();
        static private GameThread Thread2 = new GameThread();
        
	
        static class Score implements Comparable<Score>{
            public String PlayerName;
            public int point;
            
            @Override
            public int compareTo(Score comparePoint) {
                int compScore = ((Score)comparePoint).point;
                return compScore-this.point;
            }
        }
        
        static private ArrayList<Score> highScoreList = new ArrayList<Score>();
        
        
        
//        static private boolean isPaused = false;
        
        

	static public int score;
	private Color[][] well;
	
        
	// Creates a border around the well and initializes the dropping piece
	private void init() throws IOException {
		well = new Color[19][24]; //play area size 
		for (int i = 0; i < 11; i++) { // columns
			for (int j = 0; j < 23; j++) { //rows 
				if (i == 0 || i == 11 || j == 22) {  // left || right || bottom 
					well[i][j] = Color.GRAY;	// gray
				} else {			// rest is black
					well[i][j] = Color.BLACK;
				}
			}
		}
                ReadHighScoreFile();
		newPiece();
	}
	
        
	// Put a new, random piece into the dropping position
	public void newPiece() {
		pieceOrigin = new Point(5, 2); // center top coordinates
		rotation = 0;
		if(collidesAt(5,2,0)){
			GameOver = true;
			return;
		}
			
		Random rnd = new Random();
		
		if (nextPieces.isEmpty()) {  	// 
			Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
			Collections.shuffle(nextPieces);
		}
		
		currentPiece = nextPieces.get(0);
		nextPieces.remove(0);
		nextPieces.add(rnd.nextInt(7));
	}
	
        
	// Collision test for the dropping piece
	private boolean collidesAt(int x, int y, int rotation) {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			if (well[p.x + x][p.y + y] != Color.BLACK) {
				return true;
			}
		}
		return false;
	}
	
        
	// Rotate the piece clockwise or counterclockwise
	public void rotate(int i) {
		int newRotation = (rotation + i) % 4;
		if (newRotation < 0) {
			newRotation = 3;
		}
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
			rotation = newRotation;
		}
		repaint();
	}
	
        
	// Move the piece left or right
	public void move(int i) {
		if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
			pieceOrigin.x += i;	
		}
		repaint();
	}
	
        
	// Drops the piece one line or fixes it to the well if it can't drop
	public void dropDown() {
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			pieceOrigin.y += 1;
		} else {
			fixToWell();
		}	
		repaint();
	}
	
        
	// Make the dropping piece part of the well, so it is available for
	// collision detection.
	public void fixToWell() {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
		}
		clearRows();
		newPiece();
	}
	
        
	public void deleteRow(int row) {
		for (int j = row-1; j > 0; j--) {
			for (int i = 1; i < 11; i++) {
				well[i][j+1] = well[i][j];
			}
		}
	}
	
        
	// Clear completed rows from the field and award score according to
	// the number of simultaneously cleared rows.
	public void clearRows() {
		boolean gap;
		int numClears = 0;
		
		for (int j = 21; j > 0; j--) {
			gap = false;
			for (int i = 1; i < 11; i++) {
				if (well[i][j] == Color.BLACK) {
					gap = true;
					break;
				}
			}
			if (!gap) {
				deleteRow(j);
				j += 1;
				numClears += 1;
                                linesCleared++;
			}
		}
		
		switch (numClears) {
		case 1:
			score += 10;
                        gameSpeed-=20;
			break;
		case 2:
			score += 30;
                        gameSpeed-=30;
			break;
		case 3:
			score += 60;
                        gameSpeed-=40;
			break;
		case 4:
			score += 100;
                        gameSpeed-=50;
			break;
		}
	}
        
	
	// Draw the falling piece
	private void drawPiece(Graphics g) {		
		g.setColor(tetraminoColors[currentPiece]);
		for (Point p : Tetraminos[currentPiece][rotation]) {
			g.fillRect((p.x + pieceOrigin.x) * 26, 
					   (p.y + pieceOrigin.y) * 26, 
					   25, 25);
		}
	}
        
	
	@Override 
	public void paintComponent(Graphics g){
            // Paint the well
            g.fillRect(0, 0, 26*12, 26*23);
            for (int i = 0; i < 18; i++) {
                    for (int j = 0; j < 23; j++) {
                            g.setColor(well[i][j]);
                            if(i > 11){
                                g.fillRect(26*i, 26*j, 26, 26);
                            }else{
                                g.fillRect(26*i, 26*j, 25, 25);
                            }

                    }
            }
            
            // Display UI
            scores(g); 
            level(g);
            linesCleared(g);
            // putPauseText(g);

            // Display next piece
            drawNextPiece(g);

            if(GameOver){
                GameOver(g);
            }
            else{
                // Draw the currently falling piece
                drawPiece(g);
            }
	}
        
        
        private void linesCleared(Graphics g){
            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.setColor(Color.WHITE);
            g.drawString("Lines",26*15, 26*8);
            
            g.drawString(String.valueOf(linesCleared),26*15+13, 26*9);
        }
        
        
        private void level(Graphics g){
            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.setColor(Color.WHITE);
            g.drawString("Level",26*13, 26*8);
            int level;
            if(gameSpeed > 900) level = 1;
            else if(gameSpeed > 800) level = 2;
            else if(gameSpeed > 700) level = 3;
            else if(gameSpeed > 600) level = 4;
            else if(gameSpeed > 500) level = 5;
            else if(gameSpeed > 400) level = 6;
            else if(gameSpeed > 300) level = 7;
            else if(gameSpeed > 200) level = 8;
            else level = 9;
            
            g.drawString(String.valueOf(level),26*13+13, 26*9);
            
        }
	
        
	private void drawNextPiece(Graphics g) {	
            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.setColor(Color.WHITE);
            g.drawString("Next Piece:",26*13, 26*3);


            g.setColor(tetraminoColors[nextPieces.get(0)]);
            for (Point p : Tetraminos[nextPieces.get(0)][0]) {
                    g.fillRect((p.x + 13) * 26, 
                                       (p.y + 4) * 26, 
                                       25,        25);
            }
	}
        
	
	private void GameOver(Graphics g){
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.setColor(Color.WHITE);
		g.drawString("GAME OVER!",26*5, 26*11);
                
                if(highScoreList.size() < 5){
                    hst.setBounds(26*7-13, 26*11, 26*4, 25);
                    bt.setBounds(26*8-13, 26*12, 26*3, 25);
                    f.add(hst,BorderLayout.CENTER);
                    f.add(bt);
                }
                else {
                    if(highScoreList.get(4).point < score){
                    hst.setBounds(26*7-13, 26*11, 26*4, 25);
                    bt.setBounds(26*8-13, 26*12, 26*3, 25);
                        f.add(hst,BorderLayout.CENTER);
                        f.add(bt);
                    }
                } 
	}
        
	
	private void scores(Graphics g){
		// High Scores
		g.setFont(new Font("Arial", Font.BOLD, 15));
		g.setColor(Color.RED);
		g.drawString("--High Scores--", 26*13, 26*15);
		
		int ScoresY = 415;
		
		
                for(Score A : highScoreList){
                    g.setFont(new Font("Arial",Font.PLAIN,12));
                    g.setColor(Color.WHITE);
                    g.drawString(A.PlayerName, 350 , ScoresY+= 15);
                    g.drawString(String.valueOf(A.point), 415 , ScoresY);
                }
                
                
                
		
		// Current Score
		g.setFont(new Font("Arial", Font.BOLD, 15));
		g.setColor(Color.WHITE);
		g.drawString("Score: " + score, 30*12, 25);
	}
        
        
        private void ReadHighScoreFile() throws FileNotFoundException, IOException{
            File file = new File("HighScores.txt");
		if(!file.exists()){
			file.createNewFile();
		}
		BufferedReader bfr = new BufferedReader(new FileReader(file));
		String scoreLine = "";
                
                while((scoreLine = bfr.readLine()) != null){
			String[] data = scoreLine.split(" ");
                        Score A = new Score();
                        A.PlayerName = data[0];
                        A.point = Integer.valueOf(data[1]);
                        highScoreList.add(A);
		}
                bfr.close();
        }
        
        static private void pause() throws InterruptedException {
            if(Thread2.running) Thread2.pauseThread();
            else Thread2.resumeThread();
        }
        
        public static void ActivateCrazyMod(){
            isCrazy = true;
        }
        
        static private void putPauseText(Graphics g) throws InterruptedException{
                
                if(gonnaPause){
                    Thread2.running = false;
                    g.setFont(new Font("Arial", Font.BOLD, 30));
                    g.setColor(Color.WHITE);
                    g.drawString("PAUSED",26*5, 26*11);
                }
                else{
                    g.setFont(new Font("Arial", Font.BOLD, 30));
                    g.setColor(Color.WHITE);
                    g.drawString("        ",26*5, 26*11);
                }           
        }
        
        
        
        static class GameThread extends Thread{
            private volatile boolean running = true;
            
            @Override
            public void run(){
                while(true){
                    // Only keep painting while "running" is true
                    // This is a crude implementation of pausing the thread
                    while (!running)
                        Thread.yield();

                    try {
                        Thread.sleep(gameSpeed);
			if(GameOver){
							
			}else
                            game.dropDown();

			} 
                    catch ( InterruptedException e ) {}
                    
                    
                }
            }

            public void pauseThread() throws InterruptedException{
                gonnaPause = true;
                
            }

            public void resumeThread(){
                gonnaPause = false;
                running = true;
            }
        }
        
        
        
        
	public static void main(String[] args) throws IOException, InterruptedException{
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(18*26+10, 26*23+25);
		f.setVisible(true);
//                
//                JPanel panel = new JPanel(new GridLayout(1,2));
//                JButton btClassic = new JButton("Classic");
//                JButton btCrazy = new JButton("Crazy");
                
                
                
                
//                
//                
//                
//                btClassic.addActionListener(new ActionListener(){
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        if(e.getSource() == btClassic){
//                            btClassic.setVisible(false);
//                            btCrazy.setVisible(false);
//                            Continue = true;
//                        }
//                    }
//                });
//                
//                btCrazy.addActionListener(new ActionListener(){
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        if(e.getSource() == btClassic){
//                            ActivateCrazyMod();
//                            btClassic.setVisible(false);
//                            btCrazy.setVisible(false);
//                            Continue = true;
//                        }
//                    }
//                });
//		
                
                
                
                
                
		game.init();
		f.add(game);
                
                bt.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() == bt){
                            PrintWriter pr = null;
                            try {
                                Score A = new Score();
                                A.PlayerName = hst.getText();
                                A.point = score;
                                highScoreList.add(A);
                                Collections.sort(highScoreList);
                                int i = 0;
                                pr = new PrintWriter("HighScores.txt");
                                for(Score s : highScoreList){
                                    pr.println(s.PlayerName + " " + s.point);
                                    i++;
                                    if(i == 5) break;
                                }   bt.setVisible(false);
                                hst.setVisible(false);
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                pr.close();
                            }
                        }
                    }
                });
		
		// Keyboard controls
		f.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					game.rotate(-1);
					break;
				case KeyEvent.VK_DOWN:
					game.rotate(+1);
					break;
				case KeyEvent.VK_LEFT:
					game.move(-1);
					break;
				case KeyEvent.VK_RIGHT:
					game.move(+1);
					break;
				case KeyEvent.VK_SPACE:
					game.dropDown();
					break;
                                case KeyEvent.VK_P:
                                {
                                    try {
                                        pause();
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                        break;
				} 
			}
			
			public void keyReleased(KeyEvent e) {
			}
		});
                
                
                Thread2.pauseThread();
                Thread2.start();
		
		// Make the falling piece drop every second
//		new Thread() {
//			@Override public void run() {
//				while (true) {
//					try {
//						Thread.sleep(gameSpeed);
//						if(GameOver){
//							
//						}else
//                                                    game.dropDown();
//					} catch ( InterruptedException e ) {}
//				}
//			}
//		}.start();
	}
}




// PAUSE Function i duzelt
