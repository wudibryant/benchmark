package com.wudi;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

//通过反射获取类中定义的方法
public class Main {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
       CaseLoader loader=new CaseLoader();
       loader.load().run();
    }

}
