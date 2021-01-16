package com.duke.viewinject;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;


public class TypeBean {
    String className;

    String simpleName;
    String packName;

    TypeElement typeElement;

    List<VariableElement> fields;


    ExecutableElement onClickmethod;


    public TypeBean(TypeElement typeElement, String className) {
        init();
        this.typeElement = typeElement;
        this.className = className;
        if (className.contains(".")) {
            int i = className.lastIndexOf(".");
            simpleName = className.substring(i + 1, className.length());
            packName = className.substring(0, i);
        } else {
            simpleName = className;
            packName = "";
        }
    }

    private void init() {
        fields = new ArrayList<>();
    }

    public void addField(VariableElement e) {
        fields.add(e);
    }

    public boolean equalsClass(String className) {
        return this.className.equals(className);
    }

    @Override
    public String toString() {
        String files = "";
        for (int i = 0; i < fields.size(); i++) {
            files += fields.get(i).getSimpleName();
        }
        return "TypeBean{" +
                "className='" + className + '\'' +
                ", simpleName='" + simpleName + '\'' +
                ", packName='" + packName + '\'' +
                ", fields=" + files +
                ", onClickmethod=" + onClickmethod +
                '}';
    }
}
