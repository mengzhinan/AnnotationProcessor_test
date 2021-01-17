package com.duke.viewinject;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-17 12:11
 * description:
 */
class AnnotationBean {

    public static final String CLASS_PROXY = "$ViewInject";

    public TypeElement typeElement;
    public String packageName;
    public String proxyClassSimpleName;

    public final Map<Integer, VariableElement> fieldMap = new HashMap<>();
    public final Map<Integer, ExecutableElement> clickMethodMap = new HashMap<>();

    public AnnotationBean(Elements elementUtils, TypeElement classElement) {
        this.typeElement = classElement;

        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        this.packageName = packageElement.getQualifiedName().toString();

        String classSimpleName = classElement.getQualifiedName().toString().substring(packageName.length() + 1);
        this.proxyClassSimpleName = classSimpleName + CLASS_PROXY;
    }

    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify!\n");
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("import " + IInjectInterface.class.getCanonicalName() + ";\n");
        builder.append('\n');

        builder.append("public class ").append(proxyClassSimpleName)
                .append(" implements " + IInjectInterface.class.getSimpleName());
        builder.append(" {\n");

        generateMethods(builder);
        builder.append('\n');

        builder.append("}\n");
        return builder.toString();

    }


    private void generateMethods(StringBuilder builder) {

        builder.append("@Override\n ");
        builder.append("public void inject(" + typeElement.getQualifiedName() + " host, Object source ) {\n");


        for (int id : fieldMap.keySet()) {
            Element element = fieldMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append(" if(source instanceof android.app.Activity){\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)source).findViewById( " + id + "));\n");
            builder.append("\n}else{\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.view.View)source).findViewById( " + id + "));\n");
            builder.append("\n};");


        }
        builder.append("  }\n");

    }

}
