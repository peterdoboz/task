package prewave.task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Run main method to receive the results of matcher
 *
 */
public class App {

	private static ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			false);

	public static void main(String[] args) throws IOException, InterruptedException {
		List<QueryTerm> terms = getQueryTerms();
		System.out.println("Terms --");
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(terms));
		List<Alert> alerts = getAlerts();
		System.out.println("Alerts --");
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(alerts));

		System.out.println("results ---");
		Set<Result> results = new HashSet<Result>();
		terms.stream().forEach(query -> {
			results.addAll(alerts.stream().filter(a -> findMatch(a, query))
					.map(a -> new Result(a.id(), query.id(), getAlertContents(a), query.text())).toList());
		});

		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results));
	}

	/**
	 * Collect list of alert contents as a list of strings.
	 * 
	 * @param alert
	 * @return
	 */
	private static List<String> getAlertContents(Alert alert) {
		return alert.contents().stream().map(AlertContent::text).toList();
	}

	/**
	 * Find a match for alert and query.
	 * 
	 * @param alert
	 * @param query
	 * @return
	 */
	private static boolean findMatch(Alert alert, QueryTerm query) {
		return alert.contents().stream().anyMatch(c -> doMatch(c.text(), query.text(), query.keepOrder()));
	}

	/**
	 * Get Query terms from the REST api.
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	private static List<QueryTerm> getQueryTerms()
			throws IOException, InterruptedException, JsonProcessingException, JsonMappingException {
		String termQueryUrl = "https://services.prewave.ai/adminInterface/api/testQueryTerm?"
				+ "key=peterd:a358e2fae7e12548344921937f2f9992aa39678594f56c84c9405ddaffab915a";

		HttpResponse<String> response = httpGet(termQueryUrl);

		TypeReference<List<QueryTerm>> jacksonTypeReference = new TypeReference<List<QueryTerm>>() {
		};

		return mapper.readValue(response.body(), jacksonTypeReference);
	}

	/**
	 * Get Alerts from the REST api.
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	private static List<Alert> getAlerts()
			throws IOException, InterruptedException, JsonProcessingException, JsonMappingException {
		String termQueryUrl = "https://services.prewave.ai/adminInterface/api/testAlerts?"
				+ "key=peterd:a358e2fae7e12548344921937f2f9992aa39678594f56c84c9405ddaffab915a";

		HttpResponse<String> response = httpGet(termQueryUrl);

		TypeReference<List<Alert>> jacksonTypeReference = new TypeReference<List<Alert>>() {
		};

		return mapper.readValue(response.body(), jacksonTypeReference);
	}

	private static HttpResponse<String> httpGet(String termQueryUrl) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(termQueryUrl)).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response;
	}

	/**
	 * Create match pattern for query term and find matches within the text.
	 * 
	 * @param text
	 * @param term
	 * @param keepOrder
	 * @return
	 */
	private static boolean doMatch(String text, String term, boolean keepOrder) {

		String query;
		if (keepOrder) {
			query = term;
		} else {
			query = term.replace(" ", "|");
		}

		String patternString = "(?i:\\b" + query + "\\b)";

		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(text);

		return matcher.find();

	}
}
