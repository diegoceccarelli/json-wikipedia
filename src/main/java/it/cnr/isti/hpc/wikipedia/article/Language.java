/**
 * Autogenerated by Avro
 *
 * <p>DO NOT EDIT DIRECTLY
 */
package it.cnr.isti.hpc.wikipedia.article;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public enum Language {
  CA,
  DA,
  DE,
  EN,
  ES,
  FA,
  IT,
  PT;
  public static final org.apache.avro.Schema SCHEMA$ =
      new org.apache.avro.Schema.Parser()
          .parse(
              "{\"type\":\"enum\",\"name\":\"Language\",\"namespace\":\"it.cnr.isti.hpc.wikipedia.article\",\"symbols\":[\"CA\",\"DA\",\"DE\",\"EN\",\"ES\",\"FA\",\"IT\",\"PT\"]}");

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }
}
