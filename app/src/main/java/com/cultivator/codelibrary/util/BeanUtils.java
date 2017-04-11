package com.cultivator.codelibrary.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class BeanUtils {

	private static HashMap<Class<?>, Method[]> beanMethodCache = new HashMap<Class<?>, Method[]>();
	private static HashMap<Class<?>, HashMap<String, Method>> fromBeanMethodCache = new HashMap<Class<?>, HashMap<String, Method>>();
   /*为beanClass 类 设置一个参数 的函数 */
	public static <T> T copyProperties(Class<T> beanClass, Object fromBean) {

		Object[] objects = new Object[0];
		try {
			T bean = beanClass.newInstance();
			Class<?> formBeanClass = fromBean.getClass();

			Method[] beanMethods = beanMethodCache.get(beanClass);
			if (beanMethods == null) {
				beanMethods = beanClass.getMethods();
				beanMethodCache.put(beanClass,beanMethods);
			}

			
			
			HashMap<String, Method> fromBeanMethods = fromBeanMethodCache
					.get(formBeanClass);
			if (fromBeanMethods == null){
				fromBeanMethods = new HashMap<String, Method>();
				Method[] methods = formBeanClass.getMethods();
				String getMethodName = null;
				for (Method method : methods) {
					getMethodName = method.getName();
					if (getMethodName.startsWith("get")
							|| getMethodName.startsWith("is"))
						fromBeanMethods.put(getMethodName, method);
				}
				fromBeanMethodCache.put(formBeanClass, fromBeanMethods);
			}

			String methodName = null;
			String getMethodName = null;
			Class<?>[] paramsType = null;
			Class<?> paramType = null;
			for (Method method : beanMethods){
				methodName = method.getName();
				if (methodName.startsWith("set")) {
					paramsType = method.getParameterTypes();
					if (paramsType.length != 1)
						continue;

					paramType = paramsType[0];
					if (paramType.toString().equals(boolean.class.toString())) {
						getMethodName = "is" + methodName.substring(3);
					} else {
						getMethodName = "get" + methodName.substring(3);
					}
					Method formBeanGetMethod = fromBeanMethods
							.get(getMethodName);
					if (formBeanGetMethod != null) {
						if (paramType.equals(formBeanGetMethod.getReturnType())) {
							
							Object value = formBeanGetMethod.invoke(fromBean,
									objects);
							if (value != null) {
								method.invoke(bean, new Object[] {value });
							}
						}
					}
				}
			}
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void copyProperties(Object fromBean, Object tagBean) {

		
		try {
			Class beanClass = tagBean.getClass();

			Class<?> formBeanClass = fromBean.getClass();

			Method[] beanMethods = beanMethodCache.get(beanClass);
			if (beanMethods == null) {
				beanMethods = beanClass.getMethods();
				beanMethodCache.put(beanClass, beanMethods);
			}

			HashMap<String, Method> fromBeanMethods = fromBeanMethodCache
					.get(formBeanClass);
			if (fromBeanMethods == null) {
				fromBeanMethods = new HashMap<String, Method>();
				Method[] methods = formBeanClass.getMethods();
				String getMethodName = null;
				for (Method method : methods) {
					getMethodName = method.getName();
					if (getMethodName.startsWith("get")
							|| getMethodName.startsWith("is"))
						fromBeanMethods.put(getMethodName, method);
				}
				fromBeanMethodCache.put(formBeanClass, fromBeanMethods);
			}

			String methodName = null;
			String getMethodName = null;
			Class<?>[] paramsType = null;
			Class<?> paramType = null;
			for (Method method : beanMethods) {
				methodName = method.getName();
				if (methodName.startsWith("set")) {
					paramsType = method.getParameterTypes();
					if (paramsType.length != 1)
						continue;

					paramType = paramsType[0];
					//类型判断并不会执行,boolean类型取消is方法,采用get
					if (paramType.getClass().getName().equals(boolean.class.getName())||paramType.isInstance(Boolean.class)||paramType.isInstance(boolean.class)) {
						getMethodName = "is" + methodName.substring(3);
					} else {
						getMethodName = "get" + methodName.substring(3);
					}
					Method formBeanGetMethod = fromBeanMethods
							.get(getMethodName);
					if (formBeanGetMethod != null) {
						if (paramType.equals(formBeanGetMethod.getReturnType())) {
							Object[] objects = new Object[0];
							Object value = formBeanGetMethod.invoke(fromBean,
									objects);
							if (value != null) {
								method.invoke(tagBean, new Object[] { value });
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取属性值
	 * @param obj
	 * @param valueClass
	 * @param fieldName
	 * @return
	 */
	public static <T> T getFieldValue(Object obj, Class<T> valueClass, String fieldName) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(obj);
		} catch (Exception ex) {
			return null;
		}
	}

}