package piece;

import main.GamePanel;

public class King extends Piece {

	public King (int color, int col, int row) {

		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = getImage("/piece/white-king-87x87");
		} else {
			image = getImage("/piece/black-king-87x87");
		}

	}

}
