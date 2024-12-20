package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece {
	
	public Rook (int color, int col, int row) {
		
		super(color, col, row);
		
		type = Type.ROOK;
		
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/white-rook-87x87");
		} else {
			image = getImage("/piece/black-rook-87x87");
		}
		
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			// Rooks can move as long as either its col or row is the same.
			if (targetCol == preCol || targetRow == preRow) {
				if (isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}
	
}
