
import java.io.IOException;

import evaluation.*;
import examples.EvaluateFromFiles;


public class MainClass{
	

	
	public static void main(String[] args) throws IOException{
		
//		Evaluate evaluate = new HitRate("jEdit", "test", "java");
//		Evaluate evaluate = new AreaUnderCurve("jEdit", "test", "java");
//		evaluate.run();

		EvaluationDataProvider evaluationDataProvider = new EvaluateFromFiles("jEdit", "test", "java");
		Evaluate evaluate = new AreaUnderCurve(evaluationDataProvider);
		evaluate.run();
		
	}
}









