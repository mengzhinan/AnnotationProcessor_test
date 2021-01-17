package com.duke.viewinject;

/**
 * author: duke
 * version: 1.0
 * dateTime: 2021-01-17 15:37
 * description:
 */
class JavaPOETCoder {

//    public static void generateCode(List<MyBean> types) {
//        for (MyBean bean : types) {
//            generateJavaFileForOneType(bean);
//        }
//    }
//
//    public static void generateJavaFileForOneType(MyBean bean) {
//        CodeBlock.Builder codeBuilder = CodeBlock.builder();
//        for (int i = 0; i < bean.fields.size(); i++) {
//            //findViewById
//            Element variableElement = bean.fields.get(i);
//            ViewInject annotation = variableElement.getAnnotation(ViewInject.class);
//            codeBuilder.add(
//                    "target." + variableElement.getSimpleName().toString() + "=" +
//                            "$T.getViewById(rootView," + annotation.value() + ");\n"
//
//                    , UTILS);
//        }
//
//        //TODO onclick
//
//        MethodSpec method = MethodSpec
//                .constructorBuilder()
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(TypeName.get(typebean.typeElement.asType()), "target")
//                .addParameter(VIEW, "rootView")
//                .addCode(codeBuilder.build())
//                .build();
//        TypeSpec typeSpec = TypeSpec.classBuilder(typebean.simpleName + SUFFIX)
//                .addMethod(method)
//                .build();
//        writeJavaFile(typebean.packName, typeSpec);
//    }
//
//    /**
//     * @param types
//     * @param typeElement
//     * @param className   @return
//     */
//    public static TypeBean getTypeBeanByName(List<TypeBean> types, TypeElement typeElement, String className) {
//        for (int i = 0; i < types.size(); i++) {
//            if (types.get(i).equalsClass(className))
//                return types.get(i);
//        }
//        TypeBean typeBean = new TypeBean(typeElement, className);
//        types.add(typeBean);
//        return typeBean;
//    }
//
//    public static void writeJavaFile(String packName, TypeSpec typeSpec) {
//        JavaFile javaFile = JavaFile.builder(packName, typeSpec).build();
//        try {
//            javaFile.writeTo(filer);
//        } catch (IOException e) {
//            e.printStackTrace();
//            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
//        }
//    }

}
