package miners;

import java.util.Map;
import datatypes.Component;

interface PersonalizedScoringAlgorithm {
	
	Map<Component, Double> getScores();
	
}
