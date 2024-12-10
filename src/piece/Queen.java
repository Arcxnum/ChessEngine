package piece;

import main.GamePanel;

public class Queen extends Piece {
	
	public Queen (int color, int col, int row) {
		
		super(color, col, row);
		
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/white-queen-87x87");
		} else {
			image = getImage("/piece/black-queen-87x87");
		}
		
	}
	
}