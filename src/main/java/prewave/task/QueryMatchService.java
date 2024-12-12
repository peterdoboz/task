package prewave.task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryMatchService {

	public static ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			false);

	public static List<String> getAlertContents(Alert alert) {
		return alert.contents().stream().map(AlertContent::text).toList();
	}

	public static boolean findMatch(Alert alert, QueryTerm query) {

		return alert.contents().stream().anyMatch(c -> doMatch(c.text(), query.text(), query.keepOrder()));
	}

	public static List<QueryTerm> getQueryTerms()
			throws IOException, InterruptedException, JsonProcessingException, JsonMappingException {
		String termQueryUrl = "https://services.prewave.ai/adminInterface/api/testQueryTerm?"
				+ "key=peterd:a358e2fae7e12548344921937f2f9992aa39678594f56c84c9405ddaffab915a";

		HttpResponse<String> response = makeRestCall(termQueryUrl);

		TypeReference<List<QueryTerm>> jacksonTypeReference = new TypeReference<List<QueryTerm>>() {
		};

		return mapper.readValue(response.body(), jacksonTypeReference);
	}

	public static List<Alert> getAlerts()
			throws IOException, InterruptedException, JsonProcessingException, JsonMappingException {
		String termQueryUrl = "https://services.prewave.ai/adminInterface/api/testAlerts?"
				+ "key=peterd:a358e2fae7e12548344921937f2f9992aa39678594f56c84c9405ddaffab915a";

		HttpResponse<String> response = makeRestCall(termQueryUrl);

		TypeReference<List<Alert>> jacksonTypeReference = new TypeReference<List<Alert>>() {
		};

		return mapper.readValue(response.body(), jacksonTypeReference);
	}

	private static HttpResponse<String> makeRestCall(String termQueryUrl) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(termQueryUrl)).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response;
	}

	public static boolean doMatch(String text, String term, boolean keepOrder) {

		String query;
		if (keepOrder) {
			query = term;
		} else {
			query = term.replace(" ", "|");
		}

		String patternString = "(?i:\\b" + query + "\\b)";

		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			return true;
		}
		return false;

	}

}
