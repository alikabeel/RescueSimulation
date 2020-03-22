package exceptions;

import model.disasters.Disaster;

public class BuildingAlreadyCollapsedException extends DisasterException {

	public BuildingAlreadyCollapsedException(Disaster disaster) {
		super(disaster);
		}
		public BuildingAlreadyCollapsedException(Disaster disaster,String message) {
			super(disaster,message);
			
			}

}
