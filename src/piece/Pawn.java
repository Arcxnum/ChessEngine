package piece;

import main.GamePanel;

public class Pawn extends Piece {
	
	public Pawn (int color, int col, int row) {
		
		super(color, col, row);
		
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/white-pawn-87x87");
		} else {
			image = getImage("/piece/black-pawn-87x87");
		}
		
	}
	
}
