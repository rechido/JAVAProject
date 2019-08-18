import bwapi.*;

public class Box {
	TilePosition leftTop;
	TilePosition rightBottom;
	
	public Box(TilePosition leftTop, TilePosition rightBottom) {
		super();
		this.leftTop = leftTop;
		this.rightBottom = rightBottom;
	}

	public TilePosition getLeftTopTilePosition() {
		return leftTop;
	}

	public TilePosition getRightBottomTilePosition() {
		return rightBottom;
	}
	
	public Position getLeftTopPosition() {
		return leftTop.toPosition();
	}

	public Position getRightBottomPosition() {
		return rightBottom.toPosition();
	}
}
