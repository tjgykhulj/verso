package verso.mapper.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import verso.mapper.MappedResult;

public class MappedBeanResult implements MappedResult 
{
	private Class<?> clazz;
	private Map<String, Field> propMap = new HashMap<>();
	
	public MappedBeanResult(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public void put(String key, Field value) {
		propMap.put(key, value);
	}
	
	public Object getClassInstance() throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}
	public Field get(String key) {
		return propMap.get(key);
	}
	
	@Override
	public Type getResultType() {
		return Type.BEAN;
	}
	
	@Override
	public Object getResult(ResultSet rs, Class<?> returnType) throws Exception 
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		//获取返回的各列名字
		List<String> columns = new ArrayList<>();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			String name = rsmd.getColumnLabel(i);
			if (get(name) != null)
				columns.add(name);
		}
		if (returnType == List.class || returnType.isArray()) {
			List<Object> ans = new ArrayList<>();
			while (rs.next()) {
				Object obj = getClassInstance();
				for (String name : columns) {
					Field field = get(name);
					field.setAccessible(true);
					field.set(obj, rs.getObject(name));
				}
				ans.add(obj);
			}
			if (returnType.isArray())
				return ans.toArray();
			return ans;
		} else {
			while (rs.next()) {
				Object obj = getClassInstance();
				for (String name : columns) {
					Field field = get(name);
					field.setAccessible(true);
					field.set(obj, rs.getObject(name));
				}
				return obj;
			}
			return null;
		}
	}
}
