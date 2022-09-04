package web;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Query {

	@NotNull
	@Size(min=2)
	private String keywords;
	private String numOfRecommendations;
	private String lang;

	
}
