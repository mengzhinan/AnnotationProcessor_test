package com.duke.viewinject.newp;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-03 21:33
 * description:
 */
@AutoService(Processor.class)
public class ViewInjectProcessor extends AbstractProcessor {
    private Messager messager;
    private Elements elements;
    private Map<String, ParseDataInfo> mProxyMap = new HashMap<String, ParseDataInfo>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(Bind.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "process...");
        mProxyMap.clear();

        Set<? extends Element> elesWithBind = roundEnv.getElementsAnnotatedWith(Bind.class);
        for (Element element : elesWithBind) {
            checkAnnotationValid(element, Bind.class);

            VariableElement variableElement = (VariableElement) element;
            //class type
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            //full class name
            String fqClassName = classElement.getQualifiedName().toString();

            ParseDataInfo parseDataInfo = mProxyMap.get(fqClassName);
            if (parseDataInfo == null) {
                parseDataInfo = new ParseDataInfo(elements, classElement);
                mProxyMap.put(fqClassName, parseDataInfo);
            }

            Bind bindAnnotation = variableElement.getAnnotation(Bind.class);
            int id = bindAnnotation.value();
            parseDataInfo.injectVariables.put(id, variableElement);
        }

        for (String key : mProxyMap.keySet()) {
            ParseDataInfo parseDataInfo = mProxyMap.get(key);
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        parseDataInfo.getProxyClassFullName(),
                        parseDataInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(parseDataInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                error(parseDataInfo.getTypeElement(),
                        "Unable to write injector for type %s: %s",
                        parseDataInfo.getTypeElement(), e.getMessage());
            }

        }
        return true;
    }

    private boolean checkAnnotationValid(Element annotatedElement, Class clazz) {
        String k = annotatedElement.getKind().name();
        int k1 = annotatedElement.getKind().ordinal();
        String kkk = ElementKind.FIELD.name();
        int kkk1 = ElementKind.FIELD.ordinal();
        if (annotatedElement.getKind() != ElementKind.FIELD) {
            error(annotatedElement, "%s must be declared on field.", clazz.getSimpleName());
            return false;
        }
        if (ViewInjectUtil.isPrivate(annotatedElement)) {
            error(annotatedElement, "%s() must can not be private.", annotatedElement.getSimpleName());
            return false;
        }

        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }
}
