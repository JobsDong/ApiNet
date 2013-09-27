/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-8-2
 */
package com.weizoom.apiserver.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.weizoom.apiserver.common.ImmutableList;

/**
 * 
 * @author chuter
 *
 */
public class ObjectsFactory {
	private ObjectsFactory() {	}
	
	/**
	 * 给定对象的类名集合和类别信息，创建对应的实例集合<br>
	 * @see #createObject(Class)
	 * @param <T>
	 * @param classStrs 类名集合
	 * @param clazz 类别信息
	 * @return 实例集合
	 * @throws Exception 
	 */
	public static final <T> List<? extends T> createObjects(Collection<String> classStrs, Class<? extends T> clazz) throws Exception {
		Preconditions.checkNotNull(clazz);
		
		if (null == classStrs || classStrs.size() == 0) {
			return ImmutableList.EMPTY_LIST;
		}
		
		List<T> instances = new ArrayList<T>();
		for (String classStr : classStrs) {
			Class<? extends T> subClazz = (Class<? extends T>) Classes.getDefaultClassLoader().loadClass(classStr);
			instances.add(createObject(subClazz));
		}
		
		return instances;
	}
	
	/**
	 * 给定对象的类信息集合，创建对应的实例集合<br>
	 * @see #createObject(Class)
	 * @param <T>
	 * @param classes 类信息集合
	 * @return 实例集合
	 * @throws Exception 
	 */
	public static final <T> List<? extends T> createObjects(Collection<Class<? extends T>> classes) throws Exception {
		if (null == classes || classes.size() == 0) {
			return ImmutableList.EMPTY_LIST;
		}
		
		List<T> instances = new ArrayList<T>();
		for (Class<? extends T> clazz : classes) {
			instances.add(createObject(clazz));
		}
		return instances;
	}
	
	/**
	 * 给定类信息，创建对应的实例<br>
	 * 刚方法首先尝试通过没有任何参数的构造器来创建实例，然后尝试使用静态方法get()来创建
	 * 
	 * @param <T>
	 * @param clazz 类信息
	 * @return 实例
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public static final <T> T createObject(Class<? extends T> clazz) throws Exception {
		Preconditions.checkNotNull(clazz);
		
		Constructor constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(null);
		} catch (Exception ignore) {
		}
		
		if (constructor != null) {
			if (! constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			
			return (T) constructor.newInstance(null);
		} else {
			Method getInstanceMethod = clazz.getDeclaredMethod("get", null);
			if (getInstanceMethod != null && Modifier.isStatic(getInstanceMethod.getModifiers()) && clazz.isAssignableFrom(getInstanceMethod.getReturnType())) {
				return (T) getInstanceMethod.invoke(null, null);
			}
		}
		
		return null;
	}
	
	/**
	 * 给定类名创建对应的实例
	 * @param classStr 类名，例如com.weizoom.apiserver.Apiserver
	 * @return 类名对应的实例
	 * @throws ClassNotFoundException
	 */
	public static final Object createObject(String classStr) throws Exception {
		Preconditions.checkNotNull(classStr);
		
		Class clazz = Class.forName(classStr);
		return createObject(clazz);
	}
	
}
