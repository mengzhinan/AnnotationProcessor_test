package com.duke.viewinject;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-17 15:37
 * description:
 */
class JavaPOETCoder {

    public static void generateCode(Filer filer, Map<String, AnnotationBean> fieldMap) {
        int size = fieldMap.size();
        for (int i = 0; i < size; i++) {
            try {
                generateJavaFileForOneType(filer, fieldMap.get(i));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static String innerCode(VariableElement variableElement) {

        int annotationId = variableElement.getAnnotation(ViewInject.class).value();
        String variableName = variableElement.getSimpleName().toString();
        String variableType = variableElement.asType().toString();

        StringBuilder builder = new StringBuilder();
        builder.append("        if (host instanceof android.app.Activity) {\n");
        builder.append("            host.").append(variableName).append(" = ")
                .append("(").append(variableType)
                .append(") (((android.app.Activity) view).findViewById(").append(annotationId).append("));\n");
        builder.append("        } else {\n");
        builder.append("            host.").append(variableName).append(" = ");
        builder.append("(").append(variableType)
                .append(") (((android.view.View) view).findViewById(").append(annotationId).append("));\n");
        builder.append("        }\n");

        return builder.toString();
    }

    private static void generateJavaFileForOneType(Filer filer, AnnotationBean bean) throws ClassNotFoundException {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        for (int i = 0; i < bean.fieldMap.size(); i++) {
            VariableElement variableElement = bean.fieldMap.get(i);
            codeBuilder.add(innerCode(variableElement));
        }

        Class view = Class.forName("android.view.View");
        MethodSpec method = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(bean.typeElement.asType()), "target")
                .addParameter(view, "rootView")
                .addCode(codeBuilder.build())
                .build();
        TypeSpec typeSpec = TypeSpec.classBuilder(bean.proxyClassSimpleName)
                .addMethod(method)
                .build();
        writeJavaFile(filer, bean.packageName, typeSpec);
    }

    private static void writeJavaFile(Filer filer, String packName, TypeSpec typeSpec) {
        JavaFile javaFile = JavaFile.builder(packName, typeSpec).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
