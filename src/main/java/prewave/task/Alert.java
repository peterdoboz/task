package prewave.task;

import java.util.List;

public record Alert(String id, String date, String inputType, List<AlertContent> contents) {

}
