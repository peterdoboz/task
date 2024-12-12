package prewave.task;

import java.util.List;

public record Result(String alertId, String queryId, List<String> alertText, String queryText) {

}
