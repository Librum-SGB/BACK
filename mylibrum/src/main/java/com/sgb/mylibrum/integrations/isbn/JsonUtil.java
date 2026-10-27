package com.sgb.mylibrum.integrations.isbn;

import java.util.ArrayList;
import java.util.List;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

final class JsonUtil {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private JsonUtil() {
    }

    static JsonNode ler(String json) {
        return MAPPER.readTree(json);
    }

    static String texto(JsonNode no, String campo) {
        if (no == null) {
            return null;
        }
        JsonNode v = no.get(campo);
        if (v == null || v.isNull() || !v.isValueNode()) {
            return null;
        }
        String t = v.asString().trim();
        return t.isEmpty() ? null : t;
    }

    static List<String> textos(JsonNode array, String campoObjeto) {
        List<String> r = new ArrayList<>();
        if (array == null || !array.isArray()) {
            return r;
        }
        for (JsonNode item : array) {
            String t = item.isValueNode() ? item.asString().trim() : texto(item, campoObjeto);
            if (t != null && !t.isEmpty()) {
                r.add(t);
            }
        }
        return r;
    }

    /** Extrai o primeiro número de 4 dígitos (ano) de uma data livre, ex.: "2008-05-01", "May 2008". */
    static Integer ano(String data) {
        if (data == null) {
            return null;
        }
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d{4})").matcher(data);
        return m.find() ? Integer.valueOf(m.group(1)) : null;
    }

    static Integer inteiro(JsonNode no, String campo) {
        if (no == null) {
            return null;
        }
        JsonNode v = no.get(campo);
        if (v == null || !v.isNumber()) {
            return null;
        }
        int i = v.asInt();
        return i > 0 ? i : null;
    }
}
