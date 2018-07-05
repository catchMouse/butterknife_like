package com.example.szx.viewinject_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

//代表包含注解元素的类
public class AnnotatedClass {
    private TypeElement mTypeElement;
    private ArrayList<BindViewField> mFields; //添加类中含有BindView注解的成员
    private ArrayList<OnClickMethod> mMethods; //添加类中含有OnClick注解的方法
    private Elements mElements;

    public AnnotatedClass(TypeElement typeElement, Elements elements) {
        mTypeElement = typeElement;
        mElements = elements;
        mFields = new ArrayList<>();
        mMethods = new ArrayList<>();
    }

    public String getFullClassName() {
        return mTypeElement.getQualifiedName().toString();
    }

    public void addField(BindViewField field) {
        mFields.add(field);
    }

    public void addMethod(OnClickMethod method) {
        mMethods.add(method);
    }


    /**
     public class MainActivity$$ViewInject implements com.example.szx.inject.Inject<MainActivity> {
        public void inject(final MainActivity host, Object source, com.example.szx.inject.Provider provider) {
            host.text1 = (TextView)(provider.findView(TextView, R.id.id_text1))
            host.text2 = (TextView)(provider.findView(TextView, R.id.id_text2))

            View.OnClickListener listener = new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    host.handleClick();
                }
            }
            provider.findView(source, R.id.id_text1).setOnClickListener(listener);
            provider.findView(source, R.id.id_text2).setOnClickListener(listener);
            //other OnClickMethod handles:...

        }
     }

     */

    public JavaFile generateFile() {
        //generateMethod  public void inject(final MainActivity host, Object source, com.example.szx.inject.Provider provider) {}
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(mTypeElement.asType()), "host", Modifier.FINAL)
                .addParameter(TypeName.OBJECT, "source")
                .addParameter(TypeUtil.PROVIDER,"provider");

        for(BindViewField field : mFields){
            // find views
            /**
             host.text1 = (TextView)(provider.findView(TextView, R.id.id_text1))
             host.text2 = (TextView)(provider.findView(TextView, R.id.id_text2))
             */
            injectMethod.addStatement("host.$N = ($T)(provider.findView(source, $L))",
                    field.getFieldName(), ClassName.get(field.getFieldType()), field.getResId());
        }
        /**
         View.OnClickListener listener = new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                host.handleClick();
            }
         }
         provider.findView(source, R.id.id_text1).setOnClickListener(listener);
         provider.findView(source, R.id.id_text2).setOnClickListener(listener);

         //other OnClickMethod handles:...
         */
        for(OnClickMethod method :mMethods){
            //匿名内部类对象
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeUtil.ANDROID_ON_CLICK_LISTENER)
                    .addMethod(MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(TypeUtil.ANDROID_VIEW, "view")
                            .addStatement("host.$N()", method.getMethodName())
                            .build())
                    .build();
            injectMethod.addStatement("View.OnClickListener listener = $L ", listener);
            for (int id : method.getResIds()) {
                // set listeners
                injectMethod.addStatement("provider.findView(source, $L).setOnClickListener(listener)", id);
            }
        }

        //generaClass  public class MainActivity$$ViewInject implements com.example.szx.inject.Inject<MainActivity> {}
        TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + "$$ViewInject")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJET, TypeName.get(mTypeElement.asType())))
                .addMethod(injectMethod.build())
                .build();

        //获取包名
        String packgeName = mElements.getPackageOf(mTypeElement).getQualifiedName().toString();

        return JavaFile.builder(packgeName, injectClass).build();
    }

}
