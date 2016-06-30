package main;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestSuite {
	@Retention(RetentionPolicy.RUNTIME)
	@interface Ignore{}
	public static final String storage = "Storage";
	public static void test(String... sections){
		TestSuite suite = new TestSuite();
		List<String> includeCats;
		if(sections!=null){
			includeCats = new ArrayList<String>(Arrays.asList(sections));
		}
		else {
			includeCats = new ArrayList<String>();
		}
		Method[] methods = suite.getClass().getMethods();
		for(Method method:methods){
			if(method.getName().startsWith("test")){
				try {
					boolean markToInvoke = includeCats.isEmpty();
					boolean ignore = false;
					for(Annotation an:method.getAnnotations()){
						if(includeCats.contains(an.annotationType().getSimpleName().substring(1))){
							markToInvoke = true;
						}
						if(an.annotationType().getSimpleName().equals("Ignore")){
							ignore = true;
						}
					}
					if(markToInvoke&&!ignore){
						method.invoke(suite);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception oe){
					System.err.println(method.getName()+" threw an exception");
					if(Log.verbose){
						oe.printStackTrace();
					}
				}
			}
		}
	}
	private static void reportTestResult(String testName, Object result, Object answer){
		System.out.print(testName+" ");
		System.out.println(answer.equals(answer)?"success":"FAILURE");
	}
}
