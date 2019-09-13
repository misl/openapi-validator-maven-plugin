package it.traeck.tools.openapi.validator;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Goal which validates OpenAPI based API specification files.
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class ValidatorMojo extends AbstractMojo {

  public enum Format {JSON, YAML, JSONANDYAML}

  // --------------------------------------------------------------------------
  // Mojo parameters
  // --------------------------------------------------------------------------

  /**
   * Location of the OpenAPI based specification, as URL or file.
   */
  @Parameter(name = "inputSpec", required = true)
  private String inputSpec;

  /**
   * Output folder where to write final OpenAPI specification file.
   */
  @Parameter(name = "outputPath", property = "openapi.validator.maven.plugin.outputPath",
      defaultValue = "${project.build.directory}/generated-specification")
  private String outputPath;

  /**
   * Name of the output file in which to write final OpenAPI specification.
   */
  @Parameter(name = "outputFileName", property = "openapi.validator.maven.plugin.outputFileName", defaultValue = "openapi")
  private String outputFileName = "openapi";

  /**
   * Format of the output file.
   * <p>
   * Allowed values are: JSON, YAML, JSONANDYAML
   */
  @Parameter(name = "outputFormat", property = "openapi.validator.maven.plugin.outputFormat", defaultValue = "JSON")
  private Format outputFormat = Format.JSON;

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Parameter(name = "encoding", property = "openapi.validator.maven.plugin.encoding")
  private String encoding;

  private String projectEncoding = "UTF-8";

  @Parameter(name = "prettyPrint", property = "openapi.validator.maven.plugin.prettyPrint")
  private Boolean prettyPrint = true;

  // --------------------------------------------------------------------------
  // Overriding AbstractMojo
  // --------------------------------------------------------------------------

  @Override
  public void execute() throws MojoExecutionException {
    getLog().info( String.format( "Reading OpenAPI specification: %s", inputSpec ) );
    ParseOptions options = new ParseOptions();
    options.setResolve( true );
    SwaggerParseResult result = new OpenAPIParser().readLocation( inputSpec, (List) null, options );
    if ( result == null || result.getMessages().size() > 0 ) {
      throw new MojoExecutionException( result == null ? "Failed for unknown reasons!" : Arrays.toString( result.getMessages().toArray() ) );
    }
    OpenAPI openAPI = result.getOpenAPI();

    try {
      determineEncoding();

      String openapiJson = null;
      String openapiYaml = null;
      if ( Format.JSON.equals( outputFormat ) || Format.JSONANDYAML.equals( outputFormat ) ) {
        if ( prettyPrint ) {
          openapiJson = Json.pretty( openAPI );
        } else {
          openapiJson = Json.mapper().writeValueAsString( openAPI );
        }
      }
      if ( Format.YAML.equals( outputFormat ) || Format.JSONANDYAML.equals( outputFormat ) ) {
        if ( prettyPrint ) {
          openapiYaml = Yaml.pretty( openAPI );
        } else {
          openapiYaml = Yaml.mapper().writeValueAsString( openAPI );
        }
      }

      Path path = Paths.get( outputPath, "temp" );
      final File parentFile = path.toFile().getParentFile();
      if ( parentFile != null ) {
        parentFile.mkdirs();
      }

      if ( openapiJson != null ) {
        path = Paths.get( outputPath, outputFileName + ".json" );
        getLog().info( String.format( "Writing: %s", path.toString() ) );
        Files.write( path, openapiJson.getBytes( Charset.forName( encoding ) ) );
      }
      if ( openapiYaml != null ) {
        path = Paths.get( outputPath, outputFileName + ".yaml" );
        getLog().info( String.format( "Writing: %s", path.toString() ) );
        Files.write( path, openapiYaml.getBytes( Charset.forName( encoding ) ) );
      }
    } catch ( IOException e ) {
      getLog().error( "Error writing API specification", e );
      throw new MojoExecutionException( "Failed to write API specification", e );
    } catch ( Exception e ) {
      getLog().error( "Error resolving API specification", e );
      throw new MojoExecutionException( e.getMessage(), e );
    }
  }

  private void determineEncoding() {
    if ( project != null ) {
      String pEnc = project.getProperties().getProperty( "project.build.sourceEncoding" );
      if ( StringUtils.isNotBlank( pEnc ) ) {
        projectEncoding = pEnc;
      }
    }
    if ( StringUtils.isBlank( encoding ) ) {
      encoding = projectEncoding;
    }
    getLog().info( String.format( "Using '%s' encoding to write output files.", encoding ) );
  }
}
