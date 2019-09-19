import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;

import javax.json.stream.JsonParser;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Leadpony {

  public static void main( String[] args ) {
    JsonValidationService service = JsonValidationService.newInstance();

//    JsonSchema schema = service.readSchema( Paths.get("./Schemas/schema-draft4.json"));
    JsonSchema schema = service.readSchema( Paths.get("./examples/complex/petshop-api.json"));

    ProblemHandler handler = service.createProblemPrinter(System.out::println);

    Path path = Paths.get("./Schemas/asyncapi-2.0.0.json");
    try ( JsonParser parser = service.createParser(path, schema, handler)) {
      while (parser.hasNext()) {
        JsonParser.Event event = parser.next();
        // Do something useful here
      }
    }
  }
}
