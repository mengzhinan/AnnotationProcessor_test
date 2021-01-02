package com.duke.inject_processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
@SupportedAnnotationTypes("com.duke.inject_processor.ViewInject")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ViewInjectProcess extends AbstractProcessor {

    public static final String SUFFIX = "_ViewInject";

    ClassName VIEW = ClassName.get("android.view", "View");

    ClassName UTILS = ClassName.get("com.duke.inject_api", "Utils");

    private Messager messager;

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {

        List<TypeBean> types = findAnnotations(roundEnv);

        if (types.size() == 0) {
            return false;
        } else {
            generateJavaFile(types);
        }

        return false;
    }


    /**
     * @param roundEnv
     * @return
     */
    public List<TypeBean> findAnnotations(RoundEnvironment roundEnv) {
        List<TypeBean> types = new ArrayList<>();

        Set<? extends Element> eFilds = roundEnv
                .getElementsAnnotatedWith(ViewInject.class);
        Set<? extends Element> eMethods = roundEnv
                .getElementsAnnotatedWith(ViewOnClick.class);
        for (Element e : eFilds) {
            TypeElement typeElement = (TypeElement) e.getEnclosingElement();
            String qualifiedName = typeElement.getQualifiedName().toString();
            TypeBean typeBean = getTypeBeanByName(types, typeElement, qualifiedName);

            typeBean.addField((VariableElement) e);
        }

        for (Element e : eMethods) {
            TypeElement typeElement = (TypeElement) e.getEnclosingElement();
            String qualifiedName = typeElement.getQualifiedName().toString();
            TypeBean typeBean = getTypeBeanByName(types, typeElement, qualifiedName);
            if (typeBean.onClickmethod != null) {
//                error(e,msg);
            } else {
                typeBean.onClickmethod = (ExecutableElement) e;
            }
        }
        return types;
    }


    /**
     * @param types
     */
    private void generateJavaFile(List<TypeBean> types) {
        for (TypeBean typebean : types) {
            generateJavaFileForOneType(typebean);
        }
    }

    /**
     * @param typebean
     */
    private void generateJavaFileForOneType(TypeBean typebean) {

        CodeBlock.Builder codeBuilder = CodeBlock.builder();

        for (int i = 0; i < typebean.fields.size(); i++) {
            //findViewById
            VariableElement variableElement = typebean.fields.get(i);
            ViewInject annotation = variableElement.getAnnotation(ViewInject.class);
            codeBuilder.add(
                    "target." + variableElement.getSimpleName().toString() + "=" +
                            "$T.getViewById(rootView," + annotation.value() + ");\n"

                    , UTILS);
        }

        //TODO onclick

        MethodSpec method = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(typebean.typeElement.asType()), "target")
                .addParameter(VIEW, "rootView")
                .addCode(codeBuilder.build())
                .build();
        TypeSpec typeSpec = TypeSpec.classBuilder(typebean.simpleName + SUFFIX)
                .addMethod(method)
                .build();
        writeJavaFile(typebean.packName, typeSpec);
    }

    /**
     * @param types
     * @param typeElement
     * @param className   @return
     */
    public TypeBean getTypeBeanByName(List<TypeBean> types, TypeElement typeElement, String className) {
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).equalsClass(className))
                return types.get(i);
        }
        TypeBean typeBean = new TypeBean(typeElement, className);
        types.add(typeBean);
        return typeBean;
    }


    public void writeJavaFile(String packName, TypeSpec typeSpec) {
        JavaFile javaFile = JavaFile.builder(packName, typeSpec).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }
}
