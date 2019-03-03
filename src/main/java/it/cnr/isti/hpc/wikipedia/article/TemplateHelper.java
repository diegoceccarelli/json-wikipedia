package it.cnr.isti.hpc.wikipedia.article;

import avro.shaded.com.google.common.collect.Lists;
import it.cnr.isti.hpc.wikipedia.Template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class TemplateHelper {
  private final static char KEY_VALUE_SEPARATOR = '=';

  public static LinkedHashMap<String, String> getTemplateAsMap(Template template){
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    for (String desc : template.getDescription()){
      int pos = desc.indexOf(KEY_VALUE_SEPARATOR);
      if (pos >= 0){
        String key = desc.substring(0,pos).trim();
        String value = desc.substring(pos+1).trim();
        map.put(key, value);
      }
    }
    return map;
  }

  public static List<String> getSchema(Template template) {
    List<String> schema = new ArrayList<>();
    for (String desc : template.getDescription()) {
      int pos = desc.indexOf(KEY_VALUE_SEPARATOR);
      if (pos >= 0) {
        String key = desc.substring(0, pos).trim();
        schema.add(key);
      }
    }
    return schema;
  }



}
