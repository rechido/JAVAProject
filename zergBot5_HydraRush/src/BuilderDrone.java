

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;

public class BuilderDrone {

	private Unit drone;
	private UnitType buildingInOrder;

	public BuilderDrone(Unit drone, UnitType buildingInOrder) {
		super();
		this.drone = drone;
		this.buildingInOrder = buildingInOrder;
	}

	public Unit getDrone() {
		return drone;
	}

	public void setDrone(Unit drone) {
		this.drone = drone;
	}

	public UnitType getBuildingInOrder() {
		return buildingInOrder;
	}

	public void setBuildingInOrder(UnitType buildingInOrder) {
		this.buildingInOrder = buildingInOrder;
	}

	@Override
	public String toString() {
		return "BuilderDrone [drone=" + drone + ", buildingInOrder=" + buildingInOrder + "]";
	}

}
