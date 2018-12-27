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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
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
				{ new Point(1, 1), new Point(1, 1), new Point(1, 1), new Point(1, 1) },
				{ new Point(1, 1), new Point(1, 1), new Point(1, 1), new Point(1, 1) },
				{ new Point(1, 1), new Point(1, 1), new Point(1, 1), new Point(1, 1) },
				{ new Point(1, 1), new Point(1, 1), new Point(1, 1), new Point(1, 1) }
			}

	};
	
        
	// tetramino colors
	private final Color[] tetraminoColors = {
		Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red, Color.white
	};
	
        static private JFrame f = new JFrame("Tetris");
        static private JFrame saveFrame = new JFrame("Save");
        static private JButton bt = new JButton("Save");  
        static private JTextField hst = new JTextField();
        
	static private Point pieceOrigin;
        static private Point ghostOrigin;
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
        static Graphics staticG ;
	
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
            
            if(currentPiece == 7) bombExplosion();
            
		pieceOrigin = new Point(5, 2); // center top coordinates
		rotation = 0;
		if(collidesAt(5,2,0)){
			GameOver = true;
			return;
		}
                	
		Random rnd = new Random();
		
		if (nextPieces.isEmpty()) {
			Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
                        if (isCrazy) nextPieces.add(7);
			Collections.shuffle(nextPieces);
		}
		currentPiece = nextPieces.get(0);
		nextPieces.remove(0);
                if (isCrazy) nextPieces.add(rnd.nextInt(8));
                else nextPieces.add(rnd.nextInt(7));
	}
	
        
        private void bombExplosion(){
            well[pieceOrigin.x+1][pieceOrigin.y+2] = Color.BLACK;
            well[pieceOrigin.x][pieceOrigin.y+2] = Color.BLACK;
            well[pieceOrigin.x+1][pieceOrigin.y+3] = Color.BLACK;
            well[pieceOrigin.x+2][pieceOrigin.y+2] = Color.BLACK;
            well[pieceOrigin.x+1][pieceOrigin.y+1] = Color.BLACK;
            for (int i = 0; i < 11; i++) { // columns
			for (int j = 0; j < 23; j++) { //rows 
				if (i == 0 || i == 11 || j == 22) {  // left || right || bottom 
					well[i][j] = Color.GRAY;	// gray
				}
			}
		}
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
           // if(isCrazy) clearGhostPiece();
            
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
        //    if(isCrazy) clearGhostPiece();
            
		if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
			pieceOrigin.x += i;	
		}
                
                
		repaint();
	}
	
        
	// Drops the piece one line or fixes it to the well if it can't drop
	public void dropDown() {
      //      if(isCrazy) clearGhostPiece();
            
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			pieceOrigin.y += 1;
		} else {
			fixToWell();
		}
                
                
		repaint();
	}
        
        public void SwapNextPiece(){
        //    if(isCrazy) clearGhostPiece();
            
            int swapTemp;
            swapTemp = currentPiece;
            currentPiece = nextPieces.get(0);
            nextPieces.set(0, swapTemp);
            
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
				if (well[i][j] == Color.BLACK || well[i][j] == Color.DARK_GRAY) {
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
	private void drawPiece() {		
		staticG.setColor(tetraminoColors[currentPiece]);
		for (Point p : Tetraminos[currentPiece][rotation]) {
			staticG.fillRect((p.x + pieceOrigin.x) * 26, 
					   (p.y + pieceOrigin.y) * 26, 
					   25, 25);
		}
	}
        
        private void drawGhostPiece(){
            ghostOrigin = new Point(pieceOrigin.x, pieceOrigin.y);
            
            while(!collidesAt(ghostOrigin.x, ghostOrigin.y+1, rotation)){
                ghostOrigin.y++;
            }
            
            staticG.setColor(Color.DARK_GRAY);
            for (Point p : Tetraminos[currentPiece][rotation]) {
			staticG.fillRect((p.x + ghostOrigin.x) * 26, 
					   (p.y + ghostOrigin.y) * 26, 
					   25, 25);
		}
            
        }
        
//        
//        private void clearGhostPiece(){
//            staticG.setColor(Color.BLACK);
//            for (Point p : Tetraminos[currentPiece][rotation]) {
//			staticG.fillRect((p.x + ghostOrigin.x) * 26, 
//					   (p.y + ghostOrigin.y) * 26, 
//					   25, 25);
//		}
//        }
//        
	
	@Override 
	public void paintComponent(Graphics g){
            // Paint the well
            staticG = g;
            staticG.fillRect(0, 0, 26*12, 26*23);
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
            
            
            try {
                putPauseText();
            } catch (InterruptedException ex) {
                Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Display UI
            GameModeStatus();
            scores(); 
            level();
            linesCleared();
            // putPauseText();

            // Display next piece
            drawNextPiece();
            

            if(GameOver){
                GameOver();
            }
            else{
                if(isCrazy) drawGhostPiece();
                // Draw the currently falling piece
                drawPiece();
                
            }
	}
        
        private void GameModeStatus(){
            staticG.setFont(new Font("Arial", Font.BOLD, 20));
            staticG.setColor(Color.BLACK);
            if(isCrazy) staticG.drawString("Crazy",30*12-10, 25);
            else staticG.drawString("Classic",30*12-15, 25);
        }
            
        
        private void linesCleared(){
            staticG.setFont(new Font("Arial", Font.BOLD, 15));
            staticG.setColor(Color.WHITE);
            staticG.drawString("Lines",30*13, 26*7);
            
            staticG.drawString(String.valueOf(linesCleared),26*15+13, 26*8);
        }
        
        
        private void level(){
            staticG.setFont(new Font("Arial", Font.BOLD, 15));
            staticG.setColor(Color.WHITE);
            staticG.drawString("Level",30*11, 26*7);
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
            
            staticG.drawString(String.valueOf(level),30*11+18, 26*8);
            
        }
	
        
	private void drawNextPiece() {	
            staticG.setFont(new Font("Arial", Font.BOLD, 15));
            staticG.setColor(Color.WHITE);
            staticG.drawString("Next Piece:",30*11, 26*3);


            staticG.setColor(tetraminoColors[nextPieces.get(0)]);
            for (Point p : Tetraminos[nextPieces.get(0)][0]) {
                    staticG.fillRect((p.x + 13) * 26, 
                                       (p.y + 4) * 26, 
                                       25,        25);
            }
	}
        
        
        public static void saveHighScore(){
                    JPanel panel = new JPanel(new GridLayout(1,2));
            
                    hst.setBounds(26*7-13, 26*11, 26*4, 25);
                    bt.setBounds(26*8-13, 26*12, 26*3, 25);
                    panel.add(hst);
                    panel.add(bt);
                    saveFrame.add(panel,BorderLayout.CENTER);
                    
                    
                    saveFrame.setVisible(true);
        }
        
	
	private void GameOver(){
		staticG.setFont(new Font("Arial", Font.BOLD, 30));
		staticG.setColor(Color.WHITE);
		staticG.drawString("GAME OVER!",26*5, 26*11);
                
                
                if(highScoreList.size() < 5){
                    saveHighScore();
                }
                else {
                    if(highScoreList.get(4).point < score){
                        saveHighScore();
                    }
                } 
	}
        
	
	private void scores(){
		// High Scores
		staticG.setFont(new Font("Arial", Font.BOLD, 18));
		staticG.setColor(Color.RED);
		staticG.drawString("--High Scores--", 26*12+13, 26*15+50);
		
		int ScoresY = 465;
		
		
                for(Score A : highScoreList){
                    staticG.setFont(new Font("Arial",Font.PLAIN,18));
                    staticG.setColor(Color.WHITE);
                    staticG.drawString(A.PlayerName, 325 , ScoresY+= 15);
                    staticG.drawString(String.valueOf(A.point), 415 , ScoresY);
                }
                
                
                
		
		// Current Score
		staticG.setFont(new Font("Arial", Font.BOLD, 15));
		staticG.setColor(Color.WHITE);
		staticG.drawString("Score: " + score, 30*11, 50);
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
        
        static private void putPauseText() throws InterruptedException{
                
                if(!Thread2.running){
                    staticG.setFont(new Font("Arial", Font.BOLD, 30));
                    staticG.setColor(Color.WHITE);
                    staticG.drawString("PAUSED",26*5, 26*11);
                }
                else{
                    staticG.setFont(new Font("Arial", Font.BOLD, 30));
                    staticG.setColor(Color.WHITE);
                    staticG.drawString("             ",26*5, 26*11);
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
                running = false;
                game.paintComponent(staticG);
            }

            public void resumeThread() throws InterruptedException{
                running = true;
                game.paintComponent(staticG);
            }
        }
        
        
        public static void startGame() throws IOException{
            
            
                        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        f.setSize(18*26+10, 26*23+25);
                        f.setVisible(true);
                        
                        game.init();
                        f.add(game);
            
                        
                        
                Thread2.start();
                
            
        }
        
	public static void main(String[] args) throws IOException, InterruptedException{
		
		
                JFrame fmod = new JFrame("Pick Mode");
                
                fmod.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                fmod.setSize(10*26+10, 3*23+25);
                fmod.setVisible(true);
                
                JPanel panel = new JPanel(new GridLayout(1,2));
                JButton btClassic = new JButton("Classic");
                JButton btCrazy = new JButton("Crazy");
                panel.add(btClassic);
                panel.add(btCrazy);
                fmod.add(panel,BorderLayout.CENTER);
                
                ActionListener Mod = new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() == btClassic){
                            fmod.dispose();
                            try {
                                startGame();
                            } catch (IOException ex) {
                                Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        else if(e.getSource() == btCrazy){
                            ActivateCrazyMod();
                            fmod.dispose();
                            try {
                                startGame();
                            } catch (IOException ex) {
                                Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    
                    
                    
                };
                
                btClassic.addActionListener(Mod);
                btCrazy.addActionListener(Mod);
                
                
                
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
                                    if(Thread2.running){
                                        game.rotate(-1);
                                    }
					break;
                                        
				case KeyEvent.VK_DOWN:
                                    if(Thread2.running){
					game.rotate(+1);
                                    }
					break;
				case KeyEvent.VK_LEFT:
                                    if(Thread2.running){
					game.move(-1);
                                    }
					break;
				case KeyEvent.VK_RIGHT:
                                    if(Thread2.running){
					game.move(+1);
                                    }
					break;
				case KeyEvent.VK_SPACE:
                                    if(Thread2.running){
					game.dropDown();
                                    }
                                    break;
				case KeyEvent.VK_S:
                                    if(Thread2.running){
                                        if(isCrazy){
                                            game.SwapNextPiece();
                                    
                                        }
                                    }
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
	}
}
