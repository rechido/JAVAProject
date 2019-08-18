import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.w3c.dom.ranges.DocumentRange;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;

public class Main extends DefaultBWListener {

	private Mirror mirror = new Mirror();

	private Game game;

	private Player self;
	
	private List<BaseInfo> bases;
	private List<BaseInfo> myBases;
	private List<BaseInfo> enemyBases;
	private List<Unit> drones;
	private List<Unit> zerglings;
	private List<Unit> overloads;
	private List<Unit> hydralisks;
	private List<Unit> lurkers;
	private List<Unit> mutalisks;
	private List<Unit> guardians;
	private List<Unit> devourers;
	private List<Unit> queens;
	private List<Unit> scourges;
	private List<Unit> defilers;
	private List<Unit> ultralisks;
	
	int marginMinerals;
	int marginGas;
	int phase = 0;
	
	private final int FRAME_SKIP=24;
	int frame;
	
	public void run() {
		mirror.getModule().setEventListener(this);
		mirror.startGame();
	}

	@Override
	public void onUnitCreate(Unit unit) {
		System.out.println("New unit discovered " + unit.getType());
	}

	@Override
	public void onStart() {
		game = mirror.getGame();
		self = game.self();
		bases = new ArrayList<>();
		myBases = new ArrayList<>();
		enemyBases = new ArrayList<>();
		drones = new ArrayList<>();
		zerglings = new ArrayList<>();
		overloads = new ArrayList<>();
		hydralisks = new ArrayList<>();
		lurkers = new ArrayList<>();
		mutalisks = new ArrayList<>();
		guardians = new ArrayList<>();
		devourers = new ArrayList<>();
		queens = new ArrayList<>();
		scourges = new ArrayList<>();
		defilers = new ArrayList<>();
		ultralisks = new ArrayList<>();
		frame=0;
		phase=0;
		marginMinerals=0;
		marginGas=0;
		
		// Use BWTA to analyze map
		// This may take a few minutes if the map is processed first time!
		System.out.println("Analyzing map...");
		BWTA.readMap();
		BWTA.analyze();
		System.out.println("Map data ready");
		
		game.enableFlag(1);
		game.setLocalSpeed(12);
		game.sendText("black sheep wall");
		//game.sendText("show me the money");
		
		int num=1;
		for (BaseLocation b : BWTA.getBaseLocations()) {
			TilePosition baseTilePosition = b.getTilePosition();
			Position basePosition = b.getPosition();
			BaseInfo base = new BaseInfo(baseTilePosition, basePosition, this, num++);
			bases.add(base);
		}
		
		for(BaseInfo b : bases) {
			for(Unit u : game.getUnitsOnTile(b.getBaseTilePosition())) {
				if(u.getType()==UnitType.Zerg_Hatchery && u.getPlayer()==self) {
					myBases.add(b);
				}
			}
		}
		
		for(BaseInfo b : myBases) {
			for(Unit u : self.getUnits()) {
				if(u.getType()==UnitType.Zerg_Drone) {
					b.addMineralMiningDrone(u);
				}
				if(u.getType()==UnitType.Zerg_Hatchery) {
					game.printf("Hatchery");
					b.addHatchery(u);
				}
			}
		}
		
		System.out.println("Mybase: " + myBases.size());
		
		// set Expansion Queue
		List<BaseInfo> expansionQueue = new ArrayList<>();
		
		int size = bases.size();
		for(int cnt=0; cnt<size; cnt++) {
			if(bases.size()==0) break;
			BaseInfo closestBase = null;
			for(BaseInfo b : bases) {
				if(closestBase==null || myBases.get(0).getBaseTilePosition().getDistance(b.getBaseTilePosition())<myBases.get(0).getBaseTilePosition().getDistance(closestBase.getBaseTilePosition())) {
					closestBase = b;
				}
			}
			expansionQueue.add(closestBase);
			bases.remove(closestBase);
		}
		
		bases = expansionQueue;
		
		for(BaseInfo b : bases) {
			double distance = myBases.get(0).getBaseTilePosition().getDistance(b.getBaseTilePosition());
			System.out.println("Base(" + b.getNumber() + ") : " + distance);
		}
	}

	@Override
	public void onFrame() {
		draw();

		if (frame < FRAME_SKIP) {
			frame++;
			return;
		}

		refresh();
		gather();

		if (phase == 0) {

			if (myBases.get(0).getMineralMiningDrones().size() + myBases.get(0).getGasMiningDrones().size() < 12)
				myBases.get(0).train(UnitType.Zerg_Drone);
			else {
				if (myBases.size() < 2) {
					myBases.get(0).expandBase();
				} else {
					Unit pool = null;
					for (Unit u : self.getUnits()) {
						if (u.getType() == UnitType.Zerg_Spawning_Pool) {
							pool = u;
							break;
						}
					}
					if (pool == null) {
						boolean build = false;
						for (TilePosition tile : myBases.get(0).getTilesEast()) {
							if (myBases.get(0).build(UnitType.Zerg_Spawning_Pool, tile)) {
								build = true;
								return;
							}
						}
						for (TilePosition tile : myBases.get(0).getTilesWest()) {
							if (myBases.get(0).build(UnitType.Zerg_Spawning_Pool, tile)) {
								build = true;
								return;
							}
						}
					} else {
						boolean gasExists = false;
						for (Unit u : myBases.get(0).getGas()) {
							if (u.getType() == UnitType.Resource_Vespene_Geyser) {
								gasExists = true;
							}
						}
						if (gasExists) {
							myBases.get(0).buildExtractor();
						} else {
							phase++;						
						}
					}
				}
			}
		}
		
		if (phase == 1) {
			int mineralCnt = 0;
			int gasCnt = 0;
			for(BaseInfo b : myBases) {
				mineralCnt += b.getMinerals().size();
				gasCnt += b.getGas().size();
				break;
			}
			if(drones.size()<mineralCnt*2 + gasCnt*3) {
				for(BaseInfo b : myBases) {
					b.train(UnitType.Zerg_Drone);
				}
			}
			
			boolean gasExists = false;
			for (Unit u : myBases.get(1).getGas()) {
				if (u.getType() == UnitType.Resource_Vespene_Geyser) {
					gasExists = true;
				}
			}
			if (gasExists) {
				myBases.get(1).buildExtractor();
			}
			
			// build sunkens for defense
			int sunkenCnt = 4;
			if(myBases.get(1).getCreepColonies().size() < sunkenCnt){
				boolean build = false;
				for (TilePosition tile : myBases.get(1).getTilesNorth()) {
					if (myBases.get(0).build(UnitType.Zerg_Creep_Colony, tile)) {
						build = true;
						return;
					}
				}
				for (TilePosition tile : myBases.get(1).getTilesSouth()) {
					if (myBases.get(0).build(UnitType.Zerg_Creep_Colony, tile)) {
						build = true;
						return;
					}
				}
				for (TilePosition tile : myBases.get(1).getTilesEast()) {
					if (myBases.get(0).build(UnitType.Zerg_Creep_Colony, tile)) {
						build = true;
						return;
					}
				}
				for (TilePosition tile : myBases.get(1).getTilesWest()) {
					if (myBases.get(0).build(UnitType.Zerg_Creep_Colony, tile)) {
						build = true;
						return;
					}
				}
			} else {
				int sunken = 0;
				for(Unit u : self.getUnits()) {
					if(u.getType() == UnitType.Zerg_Sunken_Colony) {
						sunken++;
					}
				}
				if(sunken<sunkenCnt) {
					for (Unit cc : myBases.get(1).getCreepColonies()) {
						refreshMarginResources();
						if (self.minerals() >= UnitType.Zerg_Sunken_Colony.mineralPrice() + this.getMarginMinerals()
								&& self.gas() >= UnitType.Zerg_Sunken_Colony.gasPrice() + this.getMarginGas()) {
							cc.morph(UnitType.Zerg_Sunken_Colony);
						}
					}
				}
			}
			
			// build hydra den and chamber for attack
			Unit den = null;
			for (Unit u : self.getUnits()) {
				if (u.getType() == UnitType.Zerg_Hydralisk_Den) {
					den = u;
					break;
				}
			}
			if (den == null) {
				boolean build = false;
				for (TilePosition tile : myBases.get(0).getTilesEast()) {
					if (myBases.get(0).build(UnitType.Zerg_Hydralisk_Den, tile)) {
						build = true;
						return;
					}
				}
				for (TilePosition tile : myBases.get(0).getTilesWest()) {
					if (myBases.get(0).build(UnitType.Zerg_Hydralisk_Den, tile)) {
						build = true;
						return;
					}
				}
			} else {
				Unit cham = null;
				for (Unit u : self.getUnits()) {
					if (u.getType() == UnitType.Zerg_Evolution_Chamber) {
						cham = u;
						break;
					}
				}
				if (cham == null) {
					boolean build = false;
					for (TilePosition tile : myBases.get(0).getTilesEast()) {
						if (myBases.get(0).build(UnitType.Zerg_Evolution_Chamber, tile)) {
							build = true;
							return;
						}
					}
					for (TilePosition tile : myBases.get(0).getTilesWest()) {
						if (myBases.get(0).build(UnitType.Zerg_Evolution_Chamber, tile)) {
							build = true;
							return;
						}
					}
				} else {
					int sunken = 0;
					for(Unit u : self.getUnits()) {
						if(u.getType() == UnitType.Zerg_Sunken_Colony) {
							sunken++;
						}
					}
					if(sunken>=sunkenCnt) {
						phase++;
					}
				}
			}
		}
		
		if(phase == 2) {
			if(hydralisks.size()<12) {
				for (BaseInfo b : myBases) {
					b.train(UnitType.Zerg_Hydralisk);
				}
			} else {
				phase++;
			}
			
			stayAtBase(hydralisks, myBases.get(1));
		}
		
		if (phase == 3) {
			int mineralCnt = 0;
			int gasCnt = 0;
			
			int cnt = 0;
			for(BaseInfo b : myBases) {
				mineralCnt += b.getMinerals().size();
				gasCnt += b.getGas().size();
				cnt++;
				if(cnt==2) break;
			}
			if(drones.size()<mineralCnt*2 + gasCnt*3) {
				for(BaseInfo b : myBases) {
					b.train(UnitType.Zerg_Drone);
				}
			} 
			
			for(BaseInfo b : myBases) {
				boolean gasExists = false;
				for (Unit u : b.getGas()) {
					if (u.getType() == UnitType.Resource_Vespene_Geyser) {
						gasExists = true;
					}
				}
				if (gasExists) {
					b.buildExtractor();
				}
			}
			
			Unit hatchery = myBases.get(0).getHatcheries().get(0);
			if(hatchery.getType()==UnitType.Zerg_Hatchery) {
				refreshMarginResources();
				if (self.minerals() >= UnitType.Zerg_Lair.mineralPrice() + this.getMarginMinerals()
						&& self.gas() >= UnitType.Zerg_Lair.gasPrice() + this.getMarginGas()) {
					hatchery.morph(UnitType.Zerg_Lair);
				}
			} else if(hatchery.getType()==UnitType.Zerg_Lair) {
				if (self.getUpgradeLevel(UpgradeType.Pneumatized_Carapace) == 0) {
					myBases.get(0).upgrade(UpgradeType.Pneumatized_Carapace);
				}
			}
			
			for(Unit u : self.getUnits()) {
				if(u.getType()==UnitType.Zerg_Hydralisk_Den) {
					if (self.getUpgradeLevel(UpgradeType.Muscular_Augments) == 0) {
						myBases.get(0).upgrade(UpgradeType.Muscular_Augments);
					} else if (self.getUpgradeLevel(UpgradeType.Grooved_Spines) == 0) {
						myBases.get(0).upgrade(UpgradeType.Grooved_Spines);
					}
				}
				if(u.getType()==UnitType.Zerg_Evolution_Chamber) {
					if (self.getUpgradeLevel(UpgradeType.Zerg_Missile_Attacks) == 0) {
						myBases.get(0).upgrade(UpgradeType.Zerg_Missile_Attacks);
					} else if (self.getUpgradeLevel(UpgradeType.Zerg_Carapace) == 0) {
						myBases.get(0).upgrade(UpgradeType.Zerg_Carapace);
					} else if (self.getUpgradeLevel(UpgradeType.Zerg_Missile_Attacks) == 1) {
						myBases.get(0).upgrade(UpgradeType.Zerg_Missile_Attacks);
					} else if (self.getUpgradeLevel(UpgradeType.Zerg_Carapace) == 1) {
						myBases.get(0).upgrade(UpgradeType.Zerg_Carapace);
					} if (self.getUpgradeLevel(UpgradeType.Zerg_Missile_Attacks) == 2) {
						myBases.get(0).upgrade(UpgradeType.Zerg_Missile_Attacks);
					} else if (self.getUpgradeLevel(UpgradeType.Zerg_Carapace) == 2) {
						myBases.get(0).upgrade(UpgradeType.Zerg_Carapace);
					}
				}
			}
			
			if(self.minerals() >= 300 + marginMinerals) {
				if(myBases.size()<=10) {
					myBases.get(0).expandBase();
				}
			} 
			if(self.supplyUsed()<400) {
				for (BaseInfo b : myBases) {
					b.train(UnitType.Zerg_Hydralisk);
				}
			}
			
			if(hydralisks.size()>60) {
				attackEnemy(hydralisks, findAttackPosition());
				attackEnemy(overloads, findAttackPosition());
			} else if(hydralisks.size()>24) {
				boolean isEnemyAttaking = false;
				for(Unit e : game.enemy().getUnits()) {
					if(e.isAttacking()) {
						attackEnemy(hydralisks, e.getPosition());
						attackEnemy(overloads, e.getPosition());
						isEnemyAttaking = true;
						break;
					}
				} 
				if(!isEnemyAttaking) {
					gatheringAtMapCenter(hydralisks);
					gatheringAtMapCenter(overloads);
				}
			} else {
				stayAtBase(hydralisks, myBases.get(1));
			}
		}
	}
	
	private void stayAtBase(List<Unit> group, BaseInfo b) {
		for (Unit unit : group) {
			if (unit.isIdle()) {
				if(unit.canAttack())
					unit.attack(b.getBasePosition());
				else
					unit.move(b.getBasePosition());
			}
		}
	}
	
	private void gatheringAtMapCenter(List<Unit> group) {
		Position cen = getPositionMapCenter();
		for (Unit unit : group) {
			if (unit.isIdle()) {
				if(unit.canAttack())
					unit.attack(cen);
				else
					unit.move(cen);
			}
		}
	}

	private Position getPositionMapCenter() {
		int mapTileWidhth = game.mapWidth();
		int mapTileHeight = game.mapHeight();

		TilePosition mapCenter = new TilePosition(mapTileWidhth / 2, mapTileWidhth / 2);
		return mapCenter.toPosition();
	}
	
	private void attackEnemy(List<Unit> group, Position attackPosition) {
		for(Unit u : group) {
			if(u.canAttack())
				u.attack(attackPosition);
			else
				u.move(attackPosition);
		}
	}
	
	private Position findAttackPosition() {
		boolean enemyBaseExist = false;
		for(BaseInfo b : bases) {
			if(b.getOwner()==game.enemy()) {
				return b.getBasePosition();
			}
		}
		if(!enemyBaseExist) {
			Unit closestEnemy = null;
			for (Unit en : game.enemy().getUnits()) {
				if (!en.getType().isBuilding()) {
					if (closestEnemy == null || (myBases.get(0).getBasePosition().getDistance(en) < myBases.get(0).getBasePosition().getDistance(closestEnemy))) {
						closestEnemy = en;
					}
				}
			}
			if (closestEnemy != null) {
				return closestEnemy.getPosition();
			}
		}
		
		return null;
	}
	
	private void gather() {
		for(BaseInfo b : myBases) {
			b.gather();
		}
	}
	
	private void refresh() {
		for (BaseInfo b : bases) {
			b.refresh();
		}
		refreshMyBases();
		refreshList(UnitType.Zerg_Drone, drones);
		refreshList(UnitType.Zerg_Zergling, zerglings);
		refreshList(UnitType.Zerg_Overlord, overloads);
		refreshList(UnitType.Zerg_Hydralisk, hydralisks);
		refreshList(UnitType.Zerg_Lurker, lurkers);
		refreshList(UnitType.Zerg_Mutalisk, mutalisks);
		refreshList(UnitType.Zerg_Guardian, guardians);
		refreshList(UnitType.Zerg_Devourer, devourers);
		refreshList(UnitType.Zerg_Queen, queens);
		refreshList(UnitType.Zerg_Scourge, scourges);
		refreshList(UnitType.Zerg_Defiler, defilers);
		refreshList(UnitType.Zerg_Ultralisk, ultralisks);
		refreshDrones();
		frame = 0;
	}
	
	public void refreshMarginResources() {
		marginMinerals=0;
		marginGas=0;
		for(BaseInfo b : myBases) {
			for(BuilderDrone bd : b.getBuilderDrones()) {
				marginMinerals+=bd.getBuildingInOrder().mineralPrice();
				marginGas+=bd.getBuildingInOrder().gasPrice();
			}
		}
	}
	
	private void refreshDrones() {
		for(Unit u : self.getUnits()) {
			if(u.getType()==UnitType.Zerg_Drone && u.isIdle()) {
				boolean isContained = false;
				for(BaseInfo b : myBases) {
					for(Unit d : b.getMineralMiningDrones()) {
						if(u.getID()==d.getID()) isContained = true;
					}
					for(Unit d : b.getGasMiningDrones()) {
						if(u.getID()==d.getID()) isContained = true;
					}
				}
				if(!isContained) {
					for(BaseInfo b : myBases) {
						if(b.getGasMiningDrones().size()<b.getGas().size()*3) {
							b.getGasMiningDrones().add(u);
							break;
						}
						else if(b.getMineralMiningDrones().size()<b.getMinerals().size()*2) {
							b.getMineralMiningDrones().add(u);
							break;
						} 
					}
				}
			}
		}
	}
	
	private void refreshMyBases() {
//		for(BaseInfo b : bases) {
//			for(Unit u : game.getUnitsOnTile(b.getBaseTilePosition())) {
//				if(u.getType()==UnitType.Zerg_Hatchery && u.getPlayer()==self) {
//					boolean isContained = false;
//					for(BaseInfo my : myBases) {
//						if(my.getNumber()==b.getNumber()) {
//							isContained = true;
//						}
//					}
//					if(!isContained)
//						myBases.add(b);
//				}
//			}
//		}
		
		for(BaseInfo b : bases) {
			if(b.getOwner()==self) {
				boolean isContained = false;
				for(BaseInfo my : myBases) {
					if(my.getNumber()==b.getNumber()) {
						isContained = true;
						break;
					}
				}
				if(!isContained)
					myBases.add(b);
			} else {
				for(BaseInfo my : myBases) {
					if(my.getNumber()==b.getNumber()) {
						myBases.remove(my);
						break;
					}
				}
			}
		}
		
		
	}
	
	private void refreshList(UnitType type, List<Unit> list) {
		for(Unit u : self.getUnits()) {
			if(u.getType()==type) {
				boolean isContained = false;
				for(Unit z : list) {
					if(u.getID() == z.getID())
						isContained = true;
				}
				if(!isContained) 
					list.add(u);
			}
		}
		int size = list.size();
		for(int cnt=0; cnt<size; cnt++) {
			boolean allok = true;
			for(Unit u : list) {
				if(!u.exists() || u.getType()!=type) {
					list.remove(u);
					allok = false;
					break;
				}
			}
			if(allok)
				break;
		}
	}
	
	private void draw() {
		for (Unit unit : game.getAllUnits()) {
//			int unitWidth = unit.getType().width();
//			int unitHeight = unit.getType().height();
//			int unitx = unit.getPosition().getX();
//			int unity = unit.getPosition().getY();
//			game.drawBoxMap(unitx-unitWidth/2, unity-unitHeight/2, unitx+unitWidth/2, unity+unitHeight/2, Color.White);
			
			String str = "";
			str += unit.getType() + "\n";
			str += unit.getOrder() + "\n";
			str += unit.getOrderTarget() + "\n";
			str += unit.getBuildType();
//			str += "tile[" + unit.getTilePosition().getX() + "," + unit.getTilePosition().getY() + "]" + "\n";
//			str += "pos[" + unit.getPosition().getX() + "," + unit.getPosition().getY() + "]" + "\n";

			//game.drawTextMap(unit.getPosition(), str);
			
			Unit target = unit.getOrderTarget();
			if(target!=null) {
				int width = target.getType().width();
				int height = target.getType().height();
				Position targetPosition = unit.getOrderTargetPosition();
//				game.drawBoxMap(target.getX(), target.getY(), target.getX() + unitType.width(), target.getY() + unitType.height(), Color.Orange);
				game.drawBoxMap(targetPosition.getX()-width/2, targetPosition.getY()- height/2, targetPosition.getX() + width/2, targetPosition.getY() + height/2, Color.Orange);
						
			}
			
			UnitType buildType = unit.getBuildType();
			if(buildType!=UnitType.None && unit.getTargetPosition()!=Position.None) {
				int width = buildType.width();
				int height = buildType.height();
				Position targetPosition = unit.getTargetPosition();
				if(unit.getOrder()==Order.PlaceBuilding)
					game.drawBoxMap(targetPosition.getX()-width/2, targetPosition.getY()- height/2 + 8, targetPosition.getX() + width/2, targetPosition.getY() + height/2 + 8, Color.Purple);
				if(unit.getOrder()==Order.IncompleteBuilding)
					game.drawBoxMap(targetPosition.getX()-width/2, targetPosition.getY()- height/2, targetPosition.getX() + width/2, targetPosition.getY() + height/2, Color.Purple);
			}
			
			if(unit.getTargetPosition()!=Position.None)
				game.drawLineMap(unit.getPosition(), unit.getTargetPosition(), Color.White);
		}
		
		for(BaseInfo b : bases) {
			b.draw();
		}
		
		String strScn = "";
		strScn += "Phase: " + phase + "\n";
		strScn += "drones: " + drones.size() + "\n";
		strScn += "zerglings: " + zerglings.size() + "\n";
		strScn += "overloads: " + overloads.size() + "\n";
		strScn += "hydralisks: " + hydralisks.size() + "\n";
		strScn += "lurkers: " + lurkers.size() + "\n";
		strScn += "mutalisks: " + mutalisks.size() + "\n";
		strScn += "guardians: " + guardians.size() + "\n";
		strScn += "devourers: " + devourers.size() + "\n";
		strScn += "queens: " + queens.size() + "\n";
		strScn += "scourges: " + scourges.size() + "\n";
		strScn += "defilers: " + defilers.size() + "\n";
		strScn += "ultralisks: " + ultralisks.size() + "\n";
		strScn += "marginMinerals: " + marginMinerals + "\n";
		strScn += "marginGas: " + marginGas + "\n";
		strScn += "Base: ";
		for(BaseInfo b : myBases) {
			strScn += b.getNumber() + " ";
		}
		strScn += "\n";
		
		
		game.drawTextScreen(10, 10, strScn);
		
	}

	public Game getGame() {
		return game;
	}

	public Player getSelf() {
		return self;
	}
	
	public int getMarginMinerals() {
		return marginMinerals;
	}

	public int getMarginGas() {
		return marginGas;
	}
	
	public List<BaseInfo> getBases() {
		return bases;
	}
	
	public List<BaseInfo> getMyBases() {
		return myBases;
	}
	
	

	public static void main(String[] args) {
		new Main().run();
	}
}