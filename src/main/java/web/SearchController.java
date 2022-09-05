package web;


import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import datatypes.Component;
import util.WebSearch;

@Controller
@RequestMapping("/search")
public class SearchController {
	
	@Autowired
	private WebSearch webSearch;
	
	@GetMapping
	public String processSearch(@ModelAttribute Query query , Model model) {
		
		webSearch.setQuery(query);
		Set<Component> recommendedLibraries = webSearch.search();
		model.addAttribute("recommendedLibraries", recommendedLibraries);
		
		return "search";
	}
	
	@GetMapping("/new")
	public String processNewSearch() {
		
		return "redirect:/";
	}
	

}
