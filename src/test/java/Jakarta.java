import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.*;

public class Jakarta {

  public static void main( String[] args ) throws IOException {
    Path path = Paths.get("./Schemas/schema-draft4.json");
    Charset charset = Charset.forName("UTF-8");
    JsonObject schema = readJson2( path, charset );

//    JsonSchemaValidatorFactory factory = new JsonSchemaValidatorFactory();
//    JsonSchemaValidator validator = factory.newInstance( schema );

    path = Paths.get("./examples/complex/petshop-api.json");
    charset = Charset.forName("UTF-8");
    JsonObject document = readJson2( path, charset );

    System.out.println( "something" );
  }

  private static JsonObject readJson2( Path path, Charset charset ) throws IOException {
    InputStream stream = new BufferedInputStream(Files.newInputStream( path ));
    return Json.createReader( stream ).readObject();
  }

//  private static JsonObject readJson( Path path, Charset charset ) throws IOException {
//    ObjectMapper mapper = new ObjectMapper();
//    if (isYamlFile( path )) {
//      mapper = new ObjectMapper( new YAMLFactory() );
//    }
//    mapper.registerModule( new JSR353Module() );
//    InputStream stream = new BufferedInputStream(Files.newInputStream( path ));
//    return mapper.readValue( stream, JsonObject.class );
//  }

  private static boolean isYamlFile ( Path path ) {
    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:^.*\\.[Yy][Aa][Mm]?[Ll]$");
    // Assume json file unless .yaml or .yml extension.
    return matcher.matches( path );
  }
}
