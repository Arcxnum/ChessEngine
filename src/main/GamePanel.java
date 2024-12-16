package main;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import piece.*;

public class GamePanel extends JPanel implements Runnable {

	public static final int WIDTH = 1100;
	public static final int HEIGHT = 700;
	final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();
	
	// Pieces
	public static ArrayList<Piece> pieces = new ArrayList<Piece>();
	public static ArrayList<Piece> simPieces = new ArrayList<Piece>();
	ArrayList<Piece> promoPieces = new ArrayList<Piece>();
	Piece activeP;
	public static Piece castlingP;
	
	// Color
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor = WHITE;
	
	// Booleans
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	
	public GamePanel () {	
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);	
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		setPieces();
		copyPieces(pieces, simPieces);
	
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();	
	}
	
	public void setPieces() {
		// White pieces
		pieces.add(new Pawn(WHITE, 0, 6));
		pieces.add(new Pawn(WHITE, 1, 6));
		pieces.add(new Pawn(WHITE, 2, 6));
		pieces.add(new Pawn(WHITE, 3, 6));
		pieces.add(new Pawn(WHITE, 4, 6));
		pieces.add(new Pawn(WHITE, 5, 6));
		pieces.add(new Pawn(WHITE, 6, 6));
		pieces.add(new Pawn(WHITE, 7, 6));
		
		pieces.add(new Knight(WHITE, 1, 7));
		pieces.add(new Knight(WHITE, 6, 7));
		
		pieces.add(new Rook(WHITE, 0, 7));
		pieces.add(new Rook(WHITE, 7, 7));
		
		pieces.add(new Bishop(WHITE, 2, 7));
		pieces.add(new Bishop(WHITE, 5, 7));
		
		pieces.add(new King(WHITE, 4, 7));
		pieces.add(new Queen(WHITE, 3, 7));
		
		// Black pieces
		pieces.add(new Pawn(BLACK, 0, 1));
		pieces.add(new Pawn(BLACK, 1, 1));
		pieces.add(new Pawn(BLACK, 2, 1));
		pieces.add(new Pawn(BLACK, 3, 1));
		pieces.add(new Pawn(BLACK, 4, 1));
		pieces.add(new Pawn(BLACK, 5, 1));
		pieces.add(new Pawn(BLACK, 6, 1));
		pieces.add(new Pawn(BLACK, 7, 1));
		
		pieces.add(new Knight(BLACK, 1, 0));
		pieces.add(new Knight(BLACK, 6, 0));
		
		pieces.add(new Rook(BLACK, 0, 0));
		pieces.add(new Rook(BLACK, 7, 0));
		
		pieces.add(new Bishop(BLACK, 2, 0));
		pieces.add(new Bishop(BLACK, 5, 0));
		
		pieces.add(new King(BLACK, 4, 0));
		pieces.add(new Queen(BLACK, 3, 0));
	}
	
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}
	
	@Override
	public void run() {
		
		// GAME LOOP
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while (gameThread != null) {
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime)/drawInterval;
			lastTime = currentTime;
			
			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}

	}
	
	private void update() {
		if (promotion) {
			promoting();
		} else {
			// MOUSE BUTTON PRESSED //
			if (mouse.pressed) {
				if (activeP == null) {
					// If the activeP is null, check if you can pick up a piece
					for (Piece piece : simPieces) {
						// If the mouse is on a piece of your color, pick it up as the activeP
						if (piece.color == currentColor && piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
							activeP = piece;
						}
					}
				}
				
				else {
					simulate();
				}
			}
		
			// Mouse button released
			if (mouse.pressed == false) {
				if (activeP != null) {
					if (validSquare) {
						
						// Move confirmed
						
						// Update the piece list in case a piece has been captured and removed during simulation
						copyPieces(simPieces, pieces);
						activeP.updatePosition();
						if (castlingP != null) {
							castlingP.updatePosition();
						}
						
						if (canPromote()) {
							promotion = true;
						} else {
							changePlayer();
						}
					}
					
					else {
						// The move has not yet been played, so reset everything
						copyPieces(pieces, simPieces);
						activeP.resetPosition();
						activeP = null;
					}
				}
			}
		}	
	}
	
	private void simulate() {
		canMove = false;
		validSquare = false;
		
		// Reset the piece list in every loop
		// This restores the removed piece during simulation
		copyPieces(pieces, simPieces);
		
		// Reset the castling piece's position
		if (castlingP != null) {
			castlingP.col = castlingP.preCol;
			castlingP.x = castlingP.getX(castlingP.col);
			castlingP = null;
		}
		
		// If a piece is being held, update its position
		activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
		activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
		activeP.col = activeP.getCol(activeP.x);
		activeP.row = activeP.getRow(activeP.y);
		
		// Check if the piece is hovering over a reachable square
		if (activeP.canMove(activeP.col, activeP.row)) {
			canMove = true;
			
			// If hitting a piece, remove it from the list
			if (activeP.hittingP != null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}
			
			checkCastling();
			
			if (!isIllegal(activeP)) {
				validSquare = true;
			}
		}
	}
	
	private boolean isIllegal(Piece king) {
		if (king.type == Type.KING) {
			for (Piece piece : simPieces) {
				if (piece != king && piece.color != king.color && piece.canMove(king.col,  king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void checkCastling() {
		if (castlingP != null) {
			if (castlingP.col == 0) {
				castlingP.col += 3;
			} else if (castlingP.col == 7) {
				castlingP.col -= 2;
			}
			castlingP.x = castlingP.getX(castlingP.col);
		}
	}
	
	private void changePlayer() {
		if (currentColor == WHITE) {
			currentColor = BLACK;
			// Reset black's two stepped status for en passant
			for (Piece piece : pieces) {
				piece.twoStepped = false;
			}
		} else {
			currentColor = WHITE;
			// Reset white's two stepped status
			for (Piece piece : pieces) {
				if (piece.color == WHITE) {
					piece.twoStepped = false;
				}
			}
		}
		activeP = null;
	}
	
	private boolean canPromote() {
		if (activeP.type == Type.PAWN) {
			if (currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
				promoPieces.clear();
				promoPieces.add(new Rook(currentColor, 10, 2));
				promoPieces.add(new Knight(currentColor, 10, 3));
				promoPieces.add(new Bishop(currentColor, 10, 4));
				promoPieces.add(new Queen(currentColor, 10, 5));
				return true;
			}
		}
		return false;
	}
	
	private void promoting() {
		if (mouse.pressed) {
			for (Piece piece : promoPieces) {
				if (piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
					switch (piece.type) {
						case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row)); break;
						case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row)); break;
						case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row)); break;
						case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
						default: break;
					}
					simPieces.remove(activeP.getIndex());
					copyPieces(simPieces, pieces);
					activeP = null;
					promotion = false;
					changePlayer();
				}
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		// Board
		board.draw(g2);
		
		// Pieces
		for (Piece p : simPieces) {
			p.draw(g2);
		}
		
		if (activeP != null) {
			if (canMove) {
				if (isIllegal(activeP)) {
					g2.setColor(Color.RED);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				} else {
					g2.setColor(Color.WHITE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
				
			}
			
			// Draw the piece in the end so it won't be hidden by the board or colored square
			activeP.draw(g2);
		}
		
		// Status messages
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Monospace", Font.PLAIN, 40));
		g2.setColor(Color.white);
		
		if (promotion) {
			g2.drawString("Promote to:", 800, 150);
			for (Piece piece : promoPieces) {
				g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		} else {
			if (currentColor == WHITE) {
				g2.drawString("White's turn", 780, 550);
			} else {
				g2.drawString("Black's turn", 780, 550);
			}
		}
		
	}
	
}
