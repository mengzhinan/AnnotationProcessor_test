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
        for (String name : fieldMap.keySet()) {
            try {
                generateJavaFileForOneType(filer, fieldMap.get(name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static String innerCode(VariableElement variableElement) {

        ViewInject annotation = variableElement.getAnnotation(ViewInject.class);
        int id = annotation.value();
        String variableName = variableElement.getSimpleName().toString();
        String variableType = variableElement.asType().toString();

        StringBuilder builder = new StringBuilder();
        builder.append("        if (host instanceof android.app.Activity) {\n");
        builder.append("            host.").append(variableName).append(" = ")
                .append("(").append(variableType)
                .append(") (((android.app.Activity) view).findViewById(").append(id).append("));\n");
        builder.append("        } else {\n");
        builder.append("            host.").append(variableName).append(" = ");
        builder.append("(").append(variableType)
                .append(") (((android.view.View) view).findViewById(").append(id).append("));\n");
        builder.append("        }\n");

        return builder.toString();
    }

    private static void generateJavaFileForOneType(Filer filer, AnnotationBean bean) throws ClassNotFoundException {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        for (int id : bean.fieldMap.keySet()) {
            VariableElement variableElement = bean.fieldMap.get(id);
            codeBuilder.add(innerCode(variableElement));
        }

        MethodSpec method = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(bean.typeElement.asType()), "host")
                .addParameter(Class.forName("java.lang.Object"), "view")
                .addCode(codeBuilder.build())
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(bean.proxyClassSimpleName)
//                .addSuperinterface(IInjectInterface<bean.typeElement.asType()>)
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
