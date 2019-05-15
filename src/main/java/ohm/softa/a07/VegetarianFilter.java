package ohm.softa.a07;

import ohm.softa.a07.model.Meal;

import java.util.LinkedList;
import java.util.List;

public abstract class VegetarianFilter {

	private VegetarianFilter(){

	}


	/**
	 * Filter the given meals to remove non vegetarian ones
	 *
	 * @param mealsToFilter meals to filter
	 * @return list of vegetarian meals
	 */
	public static List<Meal> filterForVegetarian(List<Meal> mealsToFilter) {


		List<Meal> result = new LinkedList<>();
		/* iterate all the meals */
		for (Meal m : mealsToFilter) {
			/* check if meal is vegetarian */
			if (m.isVegetarian() == true) {
				result.add(m);
			}
		}
		return result;
	}
}
