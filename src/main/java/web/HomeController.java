package web;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	private static final org.slf4j.Logger log = 
			org.slf4j.LoggerFactory.getLogger(HomeController.class);
	
	@GetMapping("/")
	public String showSearchForm(Model model) {
		
		String[] allLanguages = Arrays.stream(Language.values())
				.map(Enum::toString)
				.map(x -> (x.substring(0, 1).toUpperCase() + x.substring(1).toLowerCase()))
				.toArray(String[]::new);
		Query modelQuery = new Query();
		modelQuery.setLang("Android");
		modelQuery.setNumOfRecommendations("10");
		model.addAttribute("query", modelQuery);
		model.addAttribute("allLanguages", allLanguages);
//		model.addAttribute("allLanguages", Query.Language.values());
		
		return "home";
	}
		
}
