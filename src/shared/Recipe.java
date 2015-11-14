package shared;

import java.util.ArrayList;

public class Recipe {

	/** Machine Values: 10 Total
	 * 
	 * 0: Cutter
	 * 1: Breakout
	 * 2: Manual Breakout
	 * 3: Drill
	 * 4: Cross Seamer
	 * 5: Grinder
	 * 6: Washer
	 * 7: Painter
	 * 8: UV Lamp
	 * 9: Oven
	 * 
	 */
	private final int NUM_MACHINES = 10;
	String name = "default name";
	// each index represents a machine, true = do action, false = dont do action
	public Boolean[] recipe = new Boolean[NUM_MACHINES];
	public ArrayList <String> trueOps = new ArrayList <String>();
	public ArrayList <String> falseOps = new ArrayList <String>();

		
	public Recipe(){
		// default have everything be true
		trueOps = new ArrayList <String>();
		falseOps = new ArrayList <String>();
		
		for (int i = 0; i < NUM_MACHINES; i++){
			recipe[i] = new Boolean(true);
		}
		refreshOps();
		name = "myName";
	}



	// helper funtion
	public boolean doesMachineNeedToDoJob(int machineIndex){
		return recipe[machineIndex].booleanValue();
	}
	
	public String getDescription(){
		String descrip = "";
		for (int i = 0; i <recipe.length; i++){
			if (this.recipe[i]){
				descrip += trueOps.get(i);
			}
		}
		return descrip;
	}
	
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public void refreshOps(){
		trueOps.clear();
		falseOps.clear();


			String x = new String();
			for (int j = 0; j<recipe.length; j++){
				
				switch(j){
				case 0: x = "Cutter";
						break;
				case 1: x = "Breakout";
						break;
				case 2: x = "Manual Breakout";
				break;
				case 3: x = "Drill";
				break;
				case 4: x = "Cross Seamer";
				break;
				case 5: x = "Grinder";
				break;
				case 6: x = "Washer";
				break;
				case 7: x = "Painter";
				break;
				case 8: x = "UV Lamp";
				break;
				case 9: x = "Oven";
				break;
				default: x = "wtf";
				}
			
			
				if (recipe[j]){
					trueOps.add(x);
					System.out.println("added "+x+" to trueops");
				}
				else
					falseOps.add(x);
			}
			
	}



	public void setRecipe(String n, Boolean[] r) {
		name = n;
		recipe = r;
		refreshOps();
		System.out.println("set the rec");
		System.out.println(trueOps);
		
	}



	public ArrayList<String> getOpsList() {
		refreshOps();
		return trueOps;
	}
	
	public ArrayList<String> getNotOpsList() {
		refreshOps();
		return falseOps;
	}
	
	
	
}
