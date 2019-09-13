openapi-validator-maven-plugin
============================

A Maven plugin to support validation of and conversion (YAML vs JSON) to OpenAPI (swagger) files  using the [Swagger Parser library](https://github.com/swagger-api/swagger-parser).

It not only verifies whether the API specification files are valid, but also merges multiple files into a single YAML or JSON file. This is especially usefull where tools can not cope with JSON `$ref` references to external files.

Usage
============================

Add to your `build->plugins` section (default phase is `generate-sources` phase)
```xml
<plugin>
  <groupId>it.traeck.tools.openapi</groupId>
  <artifactId>openapi-validator-maven-plugin</artifactId>
  <version>0.1.0</version>
  <executions>
    <execution>
      <goals>
        <goal>generate</goal>
      </goals>
      <configuration>
        <inputSpec>${project.basedir}/src/main/resources/api.yaml</inputSpec>
        <outputPath>${project.build.directory}/generated-specification</outputPath>
        <outputFilename>openapi</outputFilename>
        <outputFormat>JSONANDYAML</outputFormat>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Followed by:

```
mvn clean compile
```

### General Configuration parameters

Parameter | Description | Required | Default
----------|-------------|----------|---------
`inputSpec` | OpenAPI Spec file path | true |
`outputPath` | target output path | false | `${project.build.directory}/generated-specification`
`outputFilename` | Output filename (without extension) | false | openapi
`outputFormat` | Output file format (`JSON`, `YAML`, `JSONANDYAML`) | false | JSON
`encoding` | encoding to use for output files | false | ${project.build.sourceEncoding}
`prettyPrint` | whether to pretty print (true) output or not | false | true

### Sample configuration

- Please see [an example configuration](examples) for using the plugin
