

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;

public class BaseInfo {

	public static final int EAST = 1;
	public static final int WEST = 2;
	public static final int SOUTH = 3;
	public static final int NORTH = 4;

	private TilePosition baseTilePosition;
	private Position basePosition;
	private Player owner;
	private int vespeneGasDirection;
	private int mineralsDirection;
	private List<Unit> hatcheries;
	private List<Unit> creepColonies;
	private List<Unit> minerals;
	private List<Unit> gas;
	private List<Unit> mineralMiningDrones;
	private List<Unit> gasMiningDrones;
	private List<BuilderDrone> builderDrones;
	private List<Box> boxlist;
	private List<TilePosition> tilesEast;
	private List<TilePosition> tilesWest;
	private List<TilePosition> tilesSouth;
	private List<TilePosition> tilesNorth;
	

	private Main main;

	private int left;
	private int right;
	private int top;
	private int bottom;
	private int leftban;
	private int rightban;
	private int topban;
	private int bottomban;
	
	private int number;

	public BaseInfo(TilePosition baseTilePosition, Position basePosition, Main main, int number) {
		super();
		this.baseTilePosition = baseTilePosition;
		this.basePosition = basePosition;
		this.main = main;
		this.number = number;
		hatcheries = new ArrayList<>();
		creepColonies = new ArrayList<>();
		mineralMiningDrones = new ArrayList<>();
		gasMiningDrones = new ArrayList<>();
		builderDrones = new ArrayList<>();
		minerals = new ArrayList<>();
		gas = new ArrayList<>();
		boxlist = new ArrayList<>();
		tilesEast = new ArrayList<>();
		tilesWest = new ArrayList<>();
		tilesSouth = new ArrayList<>();
		tilesNorth = new ArrayList<>();
		for (Unit nUnit : main.getGame().getNeutralUnits()) {
			if (nUnit.getType() == UnitType.Resource_Mineral_Field || nUnit.getType() == UnitType.Resource_Mineral_Field_Type_2 || nUnit.getType() == UnitType.Resource_Mineral_Field_Type_3 ) {
				if(nUnit.getDistance(basePosition) <= 500) {
					minerals.add(nUnit);
				}
			}
		}
		for (Unit nUnit : main.getGame().getNeutralUnits()) {
			if (nUnit.getType() == UnitType.Resource_Vespene_Geyser) {
				if(nUnit.getDistance(basePosition) <= 500) {
					gas.add(nUnit);
				}
			}
		}

		// set mineral direction
		int bx = basePosition.getX();
		int by = basePosition.getY();
		int mx = 0;
		int my = 0;
		int mincnt = 0;
		for (Unit nUnit : minerals) {
				mx += nUnit.getPosition().getX();
				my += nUnit.getPosition().getY();
				mincnt++;
		}
		mx = mx / mincnt;
		my = my / mincnt;
		if (Math.abs(mx - bx) > Math.abs(my - by)) { // east, west
			if ((mx - bx) > 0) { // east
				this.setMineralsDirection(BaseInfo.EAST);
			} else { // west
				this.setMineralsDirection(BaseInfo.WEST);
			}
		} else { // north, south
			if ((my - by) > 0) { // south
				this.setMineralsDirection(BaseInfo.SOUTH);
			} else { // north
				this.setMineralsDirection(BaseInfo.NORTH);
			}
		}

		// set gas direction
		int gx = 0;
		int gy = 0;
		for (Unit nUnit : gas) {
				gx = nUnit.getPosition().getX();
				gy = nUnit.getPosition().getY();
				break;
		}
		if (Math.abs(gx - bx) > Math.abs(gy - by)) { // east, west
			if ((gx - bx) > 0) { // east
				this.setVespeneGasDirection(BaseInfo.EAST);
			} else { // west
				this.setVespeneGasDirection(BaseInfo.WEST);
			}
		} else { // north, south
			if ((gy - by) > 0) { // south
				this.setVespeneGasDirection(BaseInfo.SOUTH);
			} else { // north
				this.setVespeneGasDirection(BaseInfo.NORTH);
			}
		}

		setBuildBoundary();
		setTiles();
	}
	


	public void draw() {
		drawBasePosition();
		drawTextInfo();
		drawBoundaryBoxes();
		//drawCircleCanBuildHere();
		drawBoxlist();
		drawBoxTiles();
	}
	
	private void drawBoxTiles() {
		for(TilePosition tile : tilesEast) {
			int right = tile.getX()+2;
			int bottom = tile.getY()+2;
			main.getGame().drawBoxMap(tile.toPosition(), new TilePosition(right, bottom).toPosition(), Color.Green);
		}
		for(TilePosition tile : tilesWest) {
			int right = tile.getX()+2;
			int bottom = tile.getY()+2;
			main.getGame().drawBoxMap(tile.toPosition(), new TilePosition(right, bottom).toPosition(), Color.Green);
		}
		for(TilePosition tile : tilesSouth) {
			int right = tile.getX()+2;
			int bottom = tile.getY()+2;
			main.getGame().drawBoxMap(tile.toPosition(), new TilePosition(right, bottom).toPosition(), Color.Green);
		}
		for(TilePosition tile : tilesNorth) {
			int right = tile.getX()+2;
			int bottom = tile.getY()+2;
			main.getGame().drawBoxMap(tile.toPosition(), new TilePosition(right, bottom).toPosition(), Color.Green);
		}
	}
	
	private void drawBoxlist() {
		for(Box box : boxlist) {
			main.getGame().drawBoxMap(box.getLeftTopPosition(), box.getRightBottomPosition(), Color.Orange);
		}
	}
	
	private void drawBasePosition() {
		int left = basePosition.getX()-UnitType.Zerg_Hatchery.width()/2;
		int right = basePosition.getX()+UnitType.Zerg_Hatchery.width()/2;
		int top = basePosition.getY()-UnitType.Zerg_Hatchery.height()/2;
		int bot = basePosition.getY()+UnitType.Zerg_Hatchery.height()/2;
		
		if(owner==null)
			main.getGame().drawBoxMap(left, top, right, bot, Color.White);
		else if(owner==main.getSelf())
			main.getGame().drawBoxMap(left, top, right, bot, Color.Green);
		else
			main.getGame().drawBoxMap(left, top, right, bot, Color.Red);
	}
	
	private void drawTextInfo() {
		StringBuilder str = new StringBuilder();
		str.append("Bnumber: " + number + "\n");
		str.append("hatcheries: " + hatcheries.size() + "\n");
		str.append("creepColonies: " + creepColonies.size() + "\n");
		str.append("Minerals: " + minerals.size() + "\n");
		str.append("Gas: " + gas.size() + "\n");
		str.append("mineralDrones: " + mineralMiningDrones.size() + "\n");
		str.append("gasDrones: " + gasMiningDrones.size() + "\n");
		//str.append("Owner: "+getOwner()+" \n");
		Player owner = getOwner();
		if(owner == null)
			str.append("Owner: \n");
		else if(owner.getID()==main.getSelf().getID())
			str.append("Owner: You \n");
		else
			str.append("Owner: Enemy \n");
		
		int basex = basePosition.getX();
		int basey = basePosition.getY();
		
		int [] directions = {1, 2, 3, 4}; // east, west, south, north
		
		for(int cnt=0; cnt<4; cnt++) {
			if(directions[cnt]==vespeneGasDirection || directions[cnt]==mineralsDirection) {
				directions[cnt]=0;
			}
		}
		
		for(int cnt=0; cnt<4; cnt++) {
			if(directions[cnt]==1) {
				basex += 100;
				break;
			} else if(directions[cnt]==2) {
				basex -= 200;
				break;
			} else if(directions[cnt]==3) {
				basey += 100;
				break;
			} else if(directions[cnt]==4) {
				basey -= 100;
				break;
			}
		}
		
		main.getGame().drawTextMap(basex, basey, str.toString());
		
		for(Unit m : minerals) {
			main.getGame().drawTextMap(m.getPosition(), "b"+number);
		}
		for(Unit g : gas) {
			main.getGame().drawTextMap(g.getPosition(), "b"+number);
		}
	}
	
	private void drawBoundaryBoxes() {
		TilePosition leftTop = new TilePosition(left, top);
		TilePosition rightBot = new TilePosition(right, bottom);
		main.getGame().drawBoxMap(leftTop.toPosition().getX(), leftTop.toPosition().getY(), rightBot.toPosition().getX(), rightBot.toPosition().getY(), Color.White);
		leftTop = new TilePosition(leftban, topban);
		rightBot = new TilePosition(rightban, bottomban);
		main.getGame().drawBoxMap(leftTop.toPosition().getX(), leftTop.toPosition().getY(), rightBot.toPosition().getX(), rightBot.toPosition().getY(), Color.White);
	}
	
	private void drawCircleCanBuildHere() {
		for(int i=left; i<=right; i++) {
			for(int j=top; j<=bottom; j++) {
				TilePosition tile = new TilePosition(i,j);
				if(main.getGame().canBuildHere(tile, UnitType.Zerg_Creep_Colony))
					main.getGame().drawCircleMap(tile.toPosition(), 5, Color.Green);
//					main.getGame().drawDotMap(tile.toPosition(), Color.Green);
				else
					main.getGame().drawCircleMap(tile.toPosition(), 5, Color.Red);
//					main.getGame().drawDotMap(tile.toPosition(), Color.Red);
			}
		}
	}

	public boolean upgrade(UpgradeType type) {
		main.refreshMarginResources();
		if (main.getSelf().minerals() >= type.mineralPrice() + main.getMarginMinerals()
		&& main.getSelf().gas() >= type.gasPrice() + main.getMarginGas()) {
			for(Unit b : main.getSelf().getUnits()) {
				if(b.getType().isBuilding()) {
					if(b.canUpgrade(type)) {
						if(b.upgrade(type)) {
							main.getGame().printf("Upgrade: " + type);
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean train(UnitType type) {
		main.refreshMarginResources();
		if (main.getSelf().supplyTotal() < (main.getSelf().supplyUsed() + type.supplyRequired())) {
			if (main.getSelf().minerals() >= UnitType.Zerg_Overlord.mineralPrice() + main.getMarginMinerals()
			&& main.getSelf().gas() >= UnitType.Zerg_Overlord.gasPrice() + main.getMarginGas()) {
				for (Unit h : hatcheries) {
					if(h.canTrain(UnitType.Zerg_Overlord)) {
						boolean isOverloadBeingTrained = false;
						for (Unit egg : main.getSelf().getUnits()) {
							if (egg.getType() == UnitType.Zerg_Egg && egg.getBuildType() == UnitType.Zerg_Overlord) {
								isOverloadBeingTrained = true;
								break;
							}
						}
						if (!isOverloadBeingTrained) {
							if(h.train(UnitType.Zerg_Overlord)) {
								main.getGame().printf("Train: " + UnitType.Zerg_Overlord);
								return true;
							}
						}
					}
				}
			}
		} else {
			if (main.getSelf().minerals() >= type.mineralPrice() + main.getMarginMinerals()
			&& main.getSelf().gas() >= type.gasPrice() + main.getMarginGas()) {
				for (Unit h : hatcheries) {
					if (h.canTrain(type)) {
						if(h.train(type)) {
							main.getGame().printf("Train: " + type);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
		
	
	public boolean expandBase() {
		main.refreshMarginResources();
		UnitType type = UnitType.Zerg_Hatchery;
		if (main.getSelf().minerals() >= type.mineralPrice() + main.getMarginMinerals()
				&& main.getSelf().gas() >= type.gasPrice() + main.getMarginGas()) {
			if (builderDrones.size() >= 1)
				return false;
			for (Unit builder : mineralMiningDrones) {
				for (BaseInfo expand : main.getBases()) {
					if (expand.getOwner() == null) {
						if (builder.build(type, expand.getBaseTilePosition())) {
							mineralMiningDrones.remove(builder);
							BuilderDrone builderDrone = new BuilderDrone(builder, type);
							builderDrones.add(builderDrone);
							main.getGame().printf("Expand to Base(" + expand.getNumber() + ")");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean buildExtractor() {
		UnitType type = UnitType.Zerg_Extractor;
		main.refreshMarginResources();
		if (main.getSelf().minerals() >= type.mineralPrice() + main.getMarginMinerals()
				&& main.getSelf().gas() >= type.gasPrice() + main.getMarginGas()) {
			if (builderDrones.size() >= 1)
				return false;
			for (Unit builder : mineralMiningDrones) {
				TilePosition vesTile = getVespeneTilePosition();
				if (vesTile != null && builder.build(type, vesTile)) {
					mineralMiningDrones.remove(builder);
					BuilderDrone builderDrone = new BuilderDrone(builder, type);
					builderDrones.add(builderDrone);
					TilePosition leftTop = vesTile;
					TilePosition rightBot = new TilePosition(vesTile.getX() + type.tileWidth(),
							vesTile.getY() + type.tileHeight());
					Box box = new Box(leftTop, rightBot);
					boxlist.add(box);
					main.getGame().printf("Build: " + type);
					return true;
				}
			}
		}
		return false;
	}
		
	
	public boolean build(UnitType type, TilePosition tile) {
		if(type==UnitType.Zerg_Extractor) {
			 return buildExtractor();
		}
		main.refreshMarginResources();
		if (main.getSelf().minerals() >= type.mineralPrice() + main.getMarginMinerals()
				&& main.getSelf().gas() >= type.gasPrice() + main.getMarginGas()) {
			if(builderDrones.size()>=1) return false;
			 
			for (Unit builder : mineralMiningDrones) {
				if (tile != null && builder.build(type, tile)) {
					mineralMiningDrones.remove(builder);
					BuilderDrone builderDrone = new BuilderDrone(builder, type);
					builderDrones.add(builderDrone);
					TilePosition leftTop = tile;
					TilePosition rightBot = new TilePosition(tile.getX() + type.tileWidth(),
							tile.getY() + type.tileHeight());
					Box box = new Box(leftTop, rightBot);
					boxlist.add(box);
					main.getGame().printf("Build: " + type);
					return true;
				}
			}
		}

		return false;
	}

	private void setBuildBoundary() {
		left = (baseTilePosition.getX() - 8) >= 0 ? (baseTilePosition.getX() - 8) : 0;
		right = (baseTilePosition.getX() + 8 + UnitType.Zerg_Hatchery.tileWidth()) <= main.getGame().mapWidth() ? (baseTilePosition.getX() + 8 + UnitType.Zerg_Hatchery.tileWidth()) : main.getGame().mapWidth();
		top = (baseTilePosition.getY() - 6) >= 0 ? (baseTilePosition.getY() - 6) : 0;
		bottom = (baseTilePosition.getY() + 6 + UnitType.Zerg_Hatchery.tileHeight()) <= main.getGame().mapWidth() ? (baseTilePosition.getY() + 6 + UnitType.Zerg_Hatchery.tileHeight()) : main.getGame().mapWidth();
		TilePosition leftTop = new TilePosition(left, top);
		TilePosition rightBot = new TilePosition(right, bottom);
		
		for (Unit gas : gas) {
			TilePosition gt = gas.getTilePosition();
			if (leftban == 0 || gt.getX() <= leftban) {
				leftban = gt.getX();
			}
			if (rightban == 0 || (gt.getX() + UnitType.Resource_Vespene_Geyser.tileWidth()) >= rightban) {
				rightban = (gt.getX() + UnitType.Resource_Vespene_Geyser.tileWidth());
			}
			if (topban == 0 || gt.getY() <= topban) {
				topban = gt.getY();
			}
			if (bottomban == 0 || (gt.getY() + UnitType.Resource_Vespene_Geyser.tileHeight()) >= bottomban) {
				bottomban = (gt.getY() + UnitType.Resource_Vespene_Geyser.tileHeight());
			}
		}

		for (Unit min : minerals) {
			TilePosition mt = min.getTilePosition();
			if (leftban == 0 || mt.getX() <= leftban) {
				leftban = mt.getX();
			}
			if (rightban == 0 || (mt.getX() + UnitType.Resource_Mineral_Field.tileWidth()) >= rightban) {
				rightban = (mt.getX() + UnitType.Resource_Mineral_Field.tileWidth());
			}
			if (topban == 0 || mt.getY() <= topban) {
				topban = mt.getY();
			}
			if (bottomban == 0 || (mt.getY() + UnitType.Resource_Mineral_Field.tileHeight()) >= bottomban) {
				bottomban = (mt.getY() + UnitType.Resource_Mineral_Field.tileHeight());
			}
		}

		leftban = leftban >= 0 ? leftban : 0;
		topban = topban >= 0 ? topban : 0;
		rightban = rightban <= main.getGame().mapWidth() ? rightban : main.getGame().mapWidth();
		bottomban = bottomban <= main.getGame().mapWidth() ? bottomban : main.getGame().mapWidth();
		
		leftTop = new TilePosition(leftban, topban);
		rightBot = new TilePosition(rightban, bottomban);

	}
	
	public TilePosition getBuildTile(UnitType buildingType) {
		for (int i = left; i <= right; i++) {
			for (int j = top; j <= bottom; j++) {
				if (i >= leftban && i <= rightban && j >= topban && j <= bottomban)
					continue;
				TilePosition tile = new TilePosition(i, j);
				if (main.getGame().canBuildHere(tile, buildingType)) {
					return tile;
				}
			}
		}
		return null;
	}
	
	private void setTiles() {
		int[] directions = { 1, 2, 3, 4 }; // east, west, south, north

		for (int cnt = 0; cnt < 4; cnt++) {
			if (directions[cnt] == vespeneGasDirection || directions[cnt] == mineralsDirection) {
				directions[cnt] = 0;
			}
		}

		for (int cnt = 0; cnt < 4; cnt++) {
			if (directions[cnt] == 1) {
				tilesEast.add(new TilePosition(baseTilePosition.getX()+5, baseTilePosition.getY()-4));
				tilesEast.add(new TilePosition(baseTilePosition.getX()+5, baseTilePosition.getY()-2));
				tilesEast.add(new TilePosition(baseTilePosition.getX()+5, baseTilePosition.getY()));
				tilesEast.add(new TilePosition(baseTilePosition.getX()+5, baseTilePosition.getY()+2));
				tilesEast.add(new TilePosition(baseTilePosition.getX()+5, baseTilePosition.getY()+4));
			}
			if (directions[cnt] == 2) {
				tilesWest.add(new TilePosition(baseTilePosition.getX()-5, baseTilePosition.getY()-4));
				tilesWest.add(new TilePosition(baseTilePosition.getX()-5, baseTilePosition.getY()-2));
				tilesWest.add(new TilePosition(baseTilePosition.getX()-5, baseTilePosition.getY()));
				tilesWest.add(new TilePosition(baseTilePosition.getX()-5, baseTilePosition.getY()+2));
				tilesWest.add(new TilePosition(baseTilePosition.getX()-5, baseTilePosition.getY()+4));
			}
			if (directions[cnt] == 3) {
				tilesSouth.add(new TilePosition(baseTilePosition.getX()-6, baseTilePosition.getY()+4));
				tilesSouth.add(new TilePosition(baseTilePosition.getX()-4, baseTilePosition.getY()+4));
				tilesSouth.add(new TilePosition(baseTilePosition.getX()-2, baseTilePosition.getY()+4));
				tilesSouth.add(new TilePosition(baseTilePosition.getX(), baseTilePosition.getY()+4));
				tilesSouth.add(new TilePosition(baseTilePosition.getX()+2, baseTilePosition.getY()+4));
				tilesSouth.add(new TilePosition(baseTilePosition.getX()+4, baseTilePosition.getY()+4));
				tilesSouth.add(new TilePosition(baseTilePosition.getX()+6, baseTilePosition.getY()+4));
			}
			if (directions[cnt] == 4) {
				tilesNorth.add(new TilePosition(baseTilePosition.getX()-6, baseTilePosition.getY()-4));
				tilesNorth.add(new TilePosition(baseTilePosition.getX()-4, baseTilePosition.getY()-4));
				tilesNorth.add(new TilePosition(baseTilePosition.getX()-2, baseTilePosition.getY()-4));
				tilesNorth.add(new TilePosition(baseTilePosition.getX(), baseTilePosition.getY()-4));
				tilesNorth.add(new TilePosition(baseTilePosition.getX()+2, baseTilePosition.getY()-4));
				tilesNorth.add(new TilePosition(baseTilePosition.getX()+4, baseTilePosition.getY()-4));
				tilesNorth.add(new TilePosition(baseTilePosition.getX()+6, baseTilePosition.getY()-4));
			}
		}
	}

	public void refresh() {
		// refresh minerals
		boolean allok = true;
		for (int cnt = 0; cnt < minerals.size(); cnt++) {
			for (Unit unit : minerals) {
				if (!unit.exists()) {
					main.getGame().printf("Delete minerals");
					minerals.remove(unit);
					allok = false;
					break;
				}
			}
			if (allok)
				break;
		}
		// refresh mineralMiningDrones
		allok = true;
		for (int cnt = 0; cnt < mineralMiningDrones.size(); cnt++) {
			for (Unit unit : mineralMiningDrones) {
				if (!unit.exists()) {
					main.getGame().printf("Delete mineralMiningDrones");
					mineralMiningDrones.remove(unit);
					allok = false;
					break;
				}
			}
			if (allok)
				break;
		}
		// refresh gasMiningDrones
		allok = true;
		for (int cnt = 0; cnt < gasMiningDrones.size(); cnt++) {
			for (Unit unit : gasMiningDrones) {
				if (!unit.exists()) {
					main.getGame().printf("Delete gasMiningDrones");
					gasMiningDrones.remove(unit);
					allok = false;
					break;
				}
			}
			if (allok)
				break;
		}
		if (gasMiningDrones.size() > 3) {
			dispatchDroneToMineralFromGas();
		}
		// refresh builderDrone
		allok = true;
		for (int cnt = 0; cnt < builderDrones.size(); cnt++) {
			for (BuilderDrone builderDrone : builderDrones) {
				if (builderDrone.getDrone().isBeingConstructed()) {
					main.getGame().printf("Delete builderDrones");
					builderDrones.remove(builderDrone);
					allok = false;
					break;
				} else if (!builderDrone.getDrone().exists()) {
					main.getGame().printf("Delete builderDrones");
					builderDrones.remove(builderDrone);
					allok = false;
					break;
				} else if(builderDrone.getDrone().getOrder()!=Order.PlaceBuilding) {
					main.getGame().printf("Delete builderDrones");
					builderDrones.remove(builderDrone);
					allok = false;
					break;
				}
			}
			if (allok)
				break;
		}
		// refresh owner
		List<Unit> list = main.getGame().getUnitsOnTile(baseTilePosition);
		for (Unit colony : list) {
			if (colony.getType() == UnitType.Zerg_Hatchery || colony.getType() == UnitType.Zerg_Lair || colony.getType() == UnitType.Zerg_Hive
					|| colony.getType() == UnitType.Terran_Command_Center
					|| colony.getType() == UnitType.Protoss_Nexus) {
				this.setOwner(colony.getPlayer());
				break;
			} else {
				this.setOwner(null);
			}
		}
		// refresh hatcheries
		allok = true;
		for (int cnt = 0; cnt < hatcheries.size(); cnt++) {
			for (Unit unit : hatcheries) {
				if (!unit.exists()) {
					main.getGame().printf("Delete hatcheries");
					hatcheries.remove(unit);
					allok = false;
					break;
				}
			}
			if (allok)
				break;
		}
		for(Unit u : main.getSelf().getUnits()) {
			if(u.getType()==UnitType.Zerg_Hatchery || u.getType()==UnitType.Zerg_Lair || u.getType()==UnitType.Zerg_Hive) {
				if(u.getDistance(basePosition)<=500) {
					boolean isContained = false;
					for(Unit ha : hatcheries) {
						if(ha.getID()==u.getID()) {
							isContained=true;
						}
					}
					if(!isContained) {
						hatcheries.add(u);
					}
				}
			}
		}
		// refresh creepColonies
		allok = true;
		for (int cnt = 0; cnt < creepColonies.size(); cnt++) {
			for (Unit unit : creepColonies) {
				if (!unit.exists()) {
					main.getGame().printf("Delete creepColonies");
					creepColonies.remove(unit);
					allok = false;
					break;
				}
			}
			if (allok)
				break;
		}
		for(Unit u : main.getSelf().getUnits()) {
			if(u.getType()==UnitType.Zerg_Creep_Colony) {
				if(u.getDistance(basePosition)<=500) {
					boolean isContained = false;
					for(Unit ha : creepColonies) {
						if(ha.getID()==u.getID()) {
							isContained=true;
						}
					}
					if(!isContained) {
						creepColonies.add(u);
					}
				}
			}
		}
	}
	
	public void gather() {
		for (Unit drone : mineralMiningDrones) {
			if (drone.isIdle()) {
				Unit closestMineral = null;

				// find the closest mineral
				for (Unit neutralUnit : main.getGame().neutral().getUnits()) {
					if (neutralUnit.getType().isMineralField()) {
						if (closestMineral == null
								|| basePosition.getDistance(neutralUnit) < basePosition.getDistance(closestMineral)) {
							closestMineral = neutralUnit;
						}
					}
				}

				// if a mineral patch was found, send the worker to gather it
				if (closestMineral != null) {
					drone.gather(closestMineral, false);
				}
			}
		}
		
		for (Unit gas : gas) {
			for (Unit drone : gasMiningDrones) {
				if (drone.isIdle()) {
					if (gas.getType() == UnitType.Zerg_Extractor) {

						drone.gather(gas);
					}
				}
			}
		}
		
		Iterator<Unit> it = gasMiningDrones.iterator();
		
		for (Unit gas : gas) {
			if (gas.getType() == UnitType.Zerg_Extractor) {
				for(int i=0; i<3; i++) {
					if(it.hasNext()) {
						Unit drone = it.next();
						if(drone.isIdle()) {
							drone.gather(gas);
						} else if(drone.getOrder()==Order.MiningMinerals || drone.getOrder()==Order.MoveToMinerals){
							drone.gather(gas);
						}
					}
				}
			}
		}
		
		for(int cnt=0; cnt<gasMiningDrones.size(); cnt++) {
			if(it.hasNext()) {
				Unit drone = it.next();
				if(drone.isIdle()) {
					Unit closestMineral = null;

					// find the closest mineral
					for (Unit neutralUnit : main.getGame().neutral().getUnits()) {
						if (neutralUnit.getType().isMineralField()) {
							if (closestMineral == null
									|| basePosition.getDistance(neutralUnit) < basePosition.getDistance(closestMineral)) {
								closestMineral = neutralUnit;
							}
						}
					}

					// if a mineral patch was found, send the worker to gather it
					if (closestMineral != null) {
						drone.gather(closestMineral, false);
					}
				} 
			} else {
				break;
			}
		}
		
	}
	
	public void addHatchery(Unit hatchery) {
		for(Unit u : hatcheries) {
			if(u.getID() == hatchery.getID()) {
				//main.getGame().printf("That hatchery is already contained");
				return;
			}
		}
		hatcheries.add(hatchery);
	}

	public void addMineralMiningDrone(Unit drone) {
		for (Unit u : mineralMiningDrones) {
			if (u.getID() == drone.getID()) {
				main.getGame().printf("That drone is already contained");
				return;
			}
		}
		mineralMiningDrones.add(drone);
	}

	public void removeMineralMiningDrone(Unit drone) {
		for (Unit u : mineralMiningDrones) {
			if (drone.getID() == u.getID()) {
				mineralMiningDrones.remove(u);
				return;
			}
		}
		main.getGame().printf("That drone is not contained");
	}

	public void addGasMiningDrone(Unit drone) {
		for (Unit u : gasMiningDrones) {
			if (u.getID() == drone.getID()) {
				main.getGame().printf("That drone is already contained");
				return;
			}
		}
		gasMiningDrones.add(drone);
	}

	public void removeGasMiningDrone(Unit drone) {
		for (Unit u : gasMiningDrones) {
			if (drone.getID() == u.getID()) {
				gasMiningDrones.remove(u);
				return;
			}
		}
		main.getGame().printf("That drone is not contained");
	}

	public void dispatchDroneToGasFromMineral() {
		if (mineralMiningDrones.size() == 0) {
			main.getGame().printf("no more drones");
			return;
		}
		for (Unit u : mineralMiningDrones) {
			u.stop();
			gasMiningDrones.add(u);
			mineralMiningDrones.remove(u);
			return;
		}
	}

	public void dispatchDroneToMineralFromGas() {
		if (gasMiningDrones.size() == 0) {
			main.getGame().printf("no more drones");
			return;
		}
		for (Unit u : gasMiningDrones) {
			u.stop();
			mineralMiningDrones.add(u);
			gasMiningDrones.remove(u);
			return;
		}
	}

	public TilePosition getBaseTilePosition() {
		return baseTilePosition;
	}

	public void setBaseTilePosition(TilePosition baseTilePosition) {
		this.baseTilePosition = baseTilePosition;
	}

	public Position getBasePosition() {
		return basePosition;
	}

	public void setBasePosition(Position basePosition) {
		this.basePosition = basePosition;
	}

	public int getVespeneGasDirection() {
		return vespeneGasDirection;
	}

	public void setVespeneGasDirection(int vespeneGasDirection) {
		this.vespeneGasDirection = vespeneGasDirection;
	}

	public int getMineralsDirection() {
		return mineralsDirection;
	}

	public void setMineralsDirection(int mineralsDirection) {
		this.mineralsDirection = mineralsDirection;
	}

	public List<Unit> getMinerals() {
		return minerals;
	}

	public void setMinerals(List<Unit> minerals) {
		this.minerals = minerals;
	}

	public List<Unit> getGas() {
		return gas;
	}

	public void setGas(List<Unit> gas) {
		this.gas = gas;
	}
	
	public List<Unit> getHatcheries() {
		return hatcheries;
	}
	
	public List<Unit> getCreepColonies() {
		return creepColonies;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}
	
	public int getNumber() {
		return number;
	}

	public List<Unit> getMineralMiningDrones() {
		for (int cnt = 0; cnt < mineralMiningDrones.size(); cnt++) {
			boolean allok = true;
			for (Unit drone : mineralMiningDrones) {
				if (!drone.exists()) {
					mineralMiningDrones.remove(drone);
					allok = false;
					break;
				}
			}
			if (allok)
				break;
		}

		return mineralMiningDrones;
	}

	public List<Unit> getGasMiningDrones() {
		for (int cnt = 0; cnt < gasMiningDrones.size(); cnt++) {
			boolean allok = true;
			for (Unit drone : gasMiningDrones) {
				if (!drone.exists()) {
					gasMiningDrones.remove(drone);
					allok = false;
					break;
				}
			}
			if (allok)
				break;
		}
		return gasMiningDrones;
	}

	public List<BuilderDrone> getBuilderDrones() {
		for (int cnt = 0; cnt < builderDrones.size(); cnt++) {
			boolean allok = true;
			for (BuilderDrone drone : builderDrones) {
				if (!drone.getDrone().exists()) {
					builderDrones.remove(drone);
					allok = false;
					break;
				}
			}
			if (allok)
				break;
		}
		return builderDrones;
	}

	public Unit getDroneFromMineral() {
		for (Unit drone : mineralMiningDrones) {
			return drone;
		}
		return null;
	}

	public TilePosition getVespeneTilePosition() {
		for (Unit ves : gas) {
			return ves.getTilePosition();
		}
		return null;
	}

	public List<TilePosition> getTilesEast() {
		return tilesEast;
	}

	public List<TilePosition> getTilesWest() {
		return tilesWest;
	}

	public List<TilePosition> getTilesSouth() {
		return tilesSouth;
	}

	public List<TilePosition> getTilesNorth() {
		return tilesNorth;
	}

	@Override
	public String toString() {
		String strmin = "";
		int cnt = 1;
		for (Unit min : minerals) {
			strmin += "	mineral" + (cnt++) + "[ " + min.getPosition().getX() + ", " + min.getPosition().getY() + " ]\n";
		}

		String strgas = "";
		cnt = 1;
		for (Unit gas : gas) {
			strgas += "	gas" + (cnt++) + "[ " + gas.getPosition().getX() + ", " + gas.getPosition().getY() + " ]\n";
		}

		return "BaseInfo \nbaseTilePosition=" + baseTilePosition + "\nvespeneGasDirection=" + vespeneGasDirection
				+ "\nmineralsDirection=" + mineralsDirection + "\nminerals=" + strmin + "\ngas=" + strgas + "]";
	}

}
