package com.dc.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonSupport {
	private static ObjectMapper	mapper	= new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
	public static Object toObject(String json, Object type) {
		Object obj;
		try {
			obj = mapper.readValue(json.getBytes(), type.getClass());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error occurred while converting json to object.", e);
		}
		return obj;
	}

	public static String toJson(Object obj) {
		String json;
		try {
			json = mapper.writer().writeValueAsString(obj);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error occurred while converting object to json.", e);
		}
		return json;
	}

	public static void main(String srgs[]) {
		Map<String, String> map = new HashMap<>();

		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		map.put("key4", "value4");

		//System.out.println(convertToOutputMapString(map));


        try {
            long startTime = System.nanoTime();
            ObjectMapper	objMapper	= new ObjectMapper();
            long endTime = System.nanoTime();
            System.out.println("Total Time : " + (endTime - startTime));
            String json = objMapper.writeValueAsString(map);
            System.out.println(json);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

	public static String convertToOutputMapString(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Iterator<String> iterator = map.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			if (i > 0) {
				sb.append(",");
			}
			String id = iterator.next();
			String output = map.get(id);
			sb.append("{\"id\":\"").append(id).append("\",\"output\":\"").append(output).append("\"}");
			i++;
		}
		sb.append("]");
		return sb.toString();
	}

}
