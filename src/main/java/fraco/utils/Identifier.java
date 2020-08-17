package fraco.utils;

import java.util.List;

public class Identifier {
    private String name;
    private Type type;
    private String qualifiedName;
    private List<String> parameterNames;
    private String className;

    public Identifier(String name, Type type, List<String> parameterNames){
        // by default, we set qualifiedName to name, and set className to null
        this(name, type, name, parameterNames, null);
    }

    public Identifier(String name, Type type, String qualifiedName, List<String> parameterNames, String className){
        this.name = name;
        this.type = type;
        this.qualifiedName = qualifiedName;
        this.parameterNames = parameterNames;
        this.className = className;
    }

    public String get_name() {
        return name;
    }

    public Type get_type() {
        return type;
    }

    public String get_fullQualifiedName() {
        return qualifiedName;
    }

    public List<String> get_parameterNames() {
        return parameterNames;
    }

    public String get_className() {
        return className;
    }
}
