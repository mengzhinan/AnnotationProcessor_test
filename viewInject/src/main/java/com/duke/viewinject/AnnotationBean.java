package com.duke.viewinject;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
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
        builder.append("import ").append(IInjectInterface.class.getCanonicalName()).append(";\n\n");

        builder.append("public class ").append(proxyClassSimpleName).append(" implements ")
                .append(IInjectInterface.class.getSimpleName()).append("<")
                .append(typeElement.getQualifiedName().toString())
                .append(">").append(" {\n\n");

        generateMethods(builder);

        builder.append("\n}\n");
        return builder.toString();
    }


    private void generateMethods(StringBuilder builder) {

        builder.append("    @Override\n");
        builder.append("    public void inject(").append(typeElement.getQualifiedName()).append(" host, Object view) {\n");

        for (int id : fieldMap.keySet()) {
            Element element = fieldMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();

            builder.append("        if (host instanceof android.app.Activity) {\n");
            builder.append("            host.").append(name).append(" = ")
                    .append("(").append(type)
                    .append(") (((android.app.Activity) view).findViewById(").append(id).append("));\n");
            builder.append("        } else {\n");
            builder.append("            host.").append(name).append(" = ");
            builder.append("(").append(type)
                    .append(") (((android.view.View) view).findViewById(").append(id).append("));\n");
            builder.append("        }\n");
        }
        builder.append("    }\n");
    }

}
