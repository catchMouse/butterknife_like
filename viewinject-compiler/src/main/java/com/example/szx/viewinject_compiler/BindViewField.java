package com.example.szx.viewinject_compiler;

import com.example.szx.viewinject_annotation.BindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class BindViewField {
    private VariableElement mVariableElement;
    private int mresId;

    public BindViewField(Element element) throws IllegalArgumentException{
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(String.format("Only fields can be annotated with @%s",
                    BindView.class.getSimpleName()));
        }
        //表示一个字段、enum 常量、方法或构造方法的参数、局部变量或异常参数。
        mVariableElement = (VariableElement) element;

        BindView bindView = mVariableElement.getAnnotation(BindView.class);
        mresId = bindView.value();
        if (mresId < 0) {
            throw new IllegalArgumentException(
                    String.format("value() in %s for field %s is not valid !", BindView.class.getSimpleName(),
                            mVariableElement.getSimpleName()));
        }
    }

    /**
     * 获取变量名称
     * @return
     */
    public Name getFieldName() {
        return mVariableElement.getSimpleName();
    }

    /**
     * 获取变量id
     * @return
     */
    public int getResId() {
        return mresId;
    }

    /**
     * 获取变量类型
     * @return
     */
    public TypeMirror getFieldType() {
        return mVariableElement.asType();
    }

}
