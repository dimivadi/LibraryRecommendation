
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import miners.*;
import datatypes.*;
import evaluation.*;


public class MainClass{
	

	
	public static void main(String[] args) throws IOException{
		
	
		Evaluate evaluate = new HitRate("jEdit", "test", "java");
		evaluate.run();

		
		
	}
}









