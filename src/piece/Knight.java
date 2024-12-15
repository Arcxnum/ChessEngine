package piece;

import main.GamePanel;

public class Knight extends Piece {
	
	public Knight (int color, int col, int row) {
		
		super(color, col, row);
		
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/white-knight-87x87");
		} else {
			image = getImage("/piece/black-knight-87x87");
		}
		
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if (isWithinBoard(targetCol, targetRow)) {
			// The knight's movement ratio (horizontal:vertical) is always 2:1 or 1:2.
			if (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
				if (isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}
		}
		return false;
	}
	
}

