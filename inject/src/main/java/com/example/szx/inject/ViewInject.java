package com.example.szx.inject;

import android.app.Activity;
import android.util.ArrayMap;
import android.view.View;

public class ViewInject {
    private static final ActivityProvider activityProvider = new ActivityProvider();

    private static final ViewProvider viewProvider = new ViewProvider();
    private static final ArrayMap<String, Inject> injectMap = new ArrayMap<>();

    public static void inject(Activity activity) {
        inject(activity, activity, activityProvider);
    }

    public static void inject(View view) {
        inject(view, view);
    }

    private static void inject(Object host, View view) {
        inject(host, view, viewProvider);
    }

    /**
     *
     * @param host host 表示注解 View 变量所在的类，也就是注解类
     * @param object object 表示查找 View 的地方，Activity & View 自身就可以查找，Fragment 需要在自己的 itemView 中查找
     * @param provider provider 是一个接口，定义了不同对象（比如 Activity、View 等）如何去查找目标 View，项目中分别为 Activity、View 实现了 Provider 接口（具体实现参考项目代码）
     */
    private static void inject(Object host, Object object, Provider provider) {
        String className = host.getClass().getName();
        try {
            Inject inject = injectMap.get(className);

            if (inject == null) {
                Class<?> aClass = Class.forName(className + "$$ViewInject");   //运行时查找该class文件
                inject = (Inject) aClass.newInstance();  //MainActivity$$ViewInject 这个对象
                injectMap.put(className, inject);  //关联
            }
            inject.inject(host, object, provider);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
