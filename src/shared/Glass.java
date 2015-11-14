package shared;

public class Glass {

	Recipe recipe;
	int myIndex;
	
	public Glass(Recipe r, int i){
		recipe = r;
		myIndex = i;
	}
	
	public Recipe getRecipe(){
		return recipe;
	}
	public int getIndex(){
		return myIndex;
	}
	
}
