package com.duke.viewinject;

import com.duke.viewinject.newp.Bind;
import com.duke.viewinject.newp.ParseDataInfo;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-17 00:05
 * description:
 */
@AutoService(Processor.class)
public class ViewInjectAbstractProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private final Map<String, ParseDataInfo> mProxyMap = new HashMap<String, ParseDataInfo>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
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

    /**
     * Processes a set of annotation types on type elements
     * originating from the prior round and returns whether or not
     * these annotation types are claimed by this processor.  If {@code
     * true} is returned, the annotation types are claimed and subsequent
     * processors will not be asked to process them; if {@code false}
     * is returned, the annotation types are unclaimed and subsequent
     * processors may be asked to process them.  A processor may
     * always return the same boolean value or may vary the result
     * based on chosen criteria.
     *
     * <p>The input set will be empty if the processor supports {@code
     * "*"} and the root elements have no annotations.  A {@code
     * Processor} must gracefully handle an empty set of annotations.
     *
     * @param annotations the annotation types requested to be processed
     * @param roundEnv    environment for information about the current and prior round
     * @return whether or not the set of annotation types are claimed by this processor
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        MessageHelper.printLog(messager, Diagnostic.Kind.NOTE, "process...");
        mProxyMap.clear();
        findAnnotations(roundEnv);
        if (mProxyMap.size() > 0) {
            generateJavaCode();
            mProxyMap.clear();
            MessageHelper.printLog(messager, Diagnostic.Kind.NOTE, "processed");
            return true;
        }
        MessageHelper.printLog(messager, Diagnostic.Kind.NOTE, "processed");
        return false;
    }

    private void findAnnotations(RoundEnvironment roundEnv) {
        Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(Bind.class);
        for (Element element : elementSet) {
            checkAnnotationValid(element, Bind.class);

            VariableElement variableElement = (VariableElement) element;
            //class type
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            //full class name
            String fqClassName = classElement.getQualifiedName().toString();

            ParseDataInfo parseDataInfo = mProxyMap.get(fqClassName);
            if (parseDataInfo == null) {
                parseDataInfo = new ParseDataInfo(elementUtils, classElement);
                mProxyMap.put(fqClassName, parseDataInfo);
            }

            Bind bindAnnotation = variableElement.getAnnotation(Bind.class);
            int id = bindAnnotation.value();
            parseDataInfo.injectVariables.put(id, variableElement);
        }
    }

    private void generateJavaCode() {
        if (processingEnv == null) {
            return;
        }
        Filer filer = processingEnv.getFiler();
        if (filer == null) {
            return;
        }
        for (String key : mProxyMap.keySet()) {
            ParseDataInfo parseDataInfo = mProxyMap.get(key);
            if (parseDataInfo == null) {
                continue;
            }
            try {

                // 生成代码文件
                JavaFileObject jfo = filer.createSourceFile(
                        parseDataInfo.getProxyClassFullName(),
                        parseDataInfo.getTypeElement());

                Writer writer = jfo.openWriter();
                // 填充文件内容
                writer.write(parseDataInfo.generateJavaCode());
                writer.flush();
                writer.close();

            } catch (IOException e) {
                String error = String.format("Unable to write injector for type %s: %s",
                        parseDataInfo.getTypeElement(),
                        e.getMessage());
                MessageHelper.printLog(messager,
                        Diagnostic.Kind.ERROR,
                        error,
                        parseDataInfo.getTypeElement());
            }
        }
    }

    /**
     * 检查注解有效性
     *
     * @param field
     * @param clazz
     */
    private void checkAnnotationValid(Element field, Class clazz) {
        // 必须使用在属性上
        if (field.getKind() != ElementKind.FIELD) {
            String error = String.format("Unable to write injector for type %s: %s",
                    field,
                    clazz.getSimpleName());
            MessageHelper.printLog(messager,
                    Diagnostic.Kind.ERROR,
                    error);
        } else if (field.getModifiers().contains(Modifier.PRIVATE)) {
            // 不能是私有的
            String error = String.format("%s() must can not be mask private.",
                    field.getSimpleName());
            MessageHelper.printLog(messager,
                    Diagnostic.Kind.ERROR,
                    error);
        }
    }

}
