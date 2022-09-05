package web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import util.GraphData;
import util.WebSearch;

@Configuration
public class WebInitConfig {

	@Bean
	GraphData graphData() {
		return new GraphData();
	}
	
	@Bean
	WebSearch webSearch() {
		return new WebSearch();
	}
}
