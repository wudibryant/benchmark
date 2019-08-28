package com.wudi;

import com.wudi.annotations.Benchmark;
import com.wudi.annotations.Measurement;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

//运行Case
class CaseRunner{
    private final int DEFAULT_ITERATIONS=10;
    private final int DeFAULT_GROUP=5;
    private final List<Case> caseList;
    public CaseRunner(List<Case> caseList){
        this.caseList=caseList;
    }
    public void run(){
        for (Case bCase:caseList){
            int iterations=DEFAULT_ITERATIONS;
            int group=DeFAULT_GROUP;
            //先获取类级别的配置
            Measurement classMeasurement=bCase.getClass().getAnnotation(Measurement.class);
            if(classMeasurement!=null){
                iterations=classMeasurement.iterations();
                group=classMeasurement.group();
            }
            //找到对象中那些方法是测试的方法。
            Method[]methods=bCase.getClass().getMethods();
            for (Method method:methods){
                Benchmark benchmark=method.getAnnotation(Benchmark.class);
                if(benchmark==null){
                    continue;
                }
                Measurement methodMeasurement=method.getAnnotation(Measurement.class);
                if(methodMeasurement!=null){
                    iterations=methodMeasurement.iterations();
                    group=methodMeasurement.group();
                }
                runCase(bCase,method,iterations,group);
            }
        }
    }
    private void runCase(Case bCase,Method method,int itearations,int group){
        System.out.println(method.getName());
        for (int i=0;i<group;i++){
            System.out.printf("第%d次实验，",i);
            long t1=System.nanoTime();
            for (int j=0;j<itearations;j++){
                try {
                    method.invoke(bCase);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            long t2=System.nanoTime();
            System.out.printf("耗时：%d 纳秒%n",t2-t1);
        }
    }
}
//查找加载Case
public class CaseLoader {
    public CaseRunner load() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String pagDot="com.wudi.cases";
        String pkg="com/wudi/cases";
        List<String> list=new ArrayList<>();
        //1.根据一个固定类，找到类加载器
        //2.根据加载器找到类文件所在的路径
        //3.扫描路径的所有类文件
        ClassLoader classLoader=this.getClass().getClassLoader();
        Enumeration<URL> ulrs=classLoader.getResources(pkg);
        while (ulrs.hasMoreElements()){
            URL url=ulrs.nextElement();
            if(!url.getProtocol().equals("file")){
                //如果不是*.class文件，暂时不支持
                continue;
            }
            String dirname=URLDecoder.decode(url.getPath(),"UTF-8");
            File dir=new File(dirname);
            if(!dir.isDirectory()){
                //不是目录
                continue;
                }
            //扫描该目录下的所有*.class文件，作为所有的类文件。
            File[] files=dir.listFiles();
            if(files==null) {
                continue;
            }
                for (File file:files){
                    String filename=file.getName();
                    String classname=filename.substring(0,filename.length()-6);
                    list.add(classname);
                }
            }
            List<Case> caseList=new ArrayList<>();
            for (String className:list){
                Class<?> cls=Class.forName(pagDot+"."+className);
                if(hasInterface(cls,Case.class)){
                    caseList.add((Case)cls.newInstance());
                }
            }
        return new CaseRunner(caseList);
    }
    private boolean hasInterface(Class<?>cls,Class<?>intf){
        Class<?>[] intfs=cls.getInterfaces();
        for (Class<?> i:intfs){
            if(i==intf){
                return true;
            }
        }
        return false;
    }
}
