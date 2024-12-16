package piece;

import main.GamePanel;

public class Bishop extends Piece {
	
	public Bishop (int color, int col, int row) {
		
		super(color, col, row);
		
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/white-bishop-87x87");
		} else {
			image = getImage("/piece/black-bishop-87x87");
		}
		
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
			if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
				if (isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
