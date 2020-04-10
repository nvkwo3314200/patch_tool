package com.peak.db;

public enum ObjectEnum {
    Function("function", "IF", "表值函数"),
    Function2("function", "FN", "标量值函数"),
    View("view", "V", "视图"),
    Table("table", "U", "表"),
    Package("package", "PK", "包"),
    Procedure("procedure", "P", "存储过程"),
    Type("type", "T", "自定义数据类型"),
    Trigger("trigger", "TR", "触发器"),
    Job("job", "J", "数据库作业"),
    Sequence("sequence", "S", "序列"),
    Synonym("synonym", "Sn", "同义词"),
    Index("index", "I", "索引"),
    Constraint("constraint", "C", "约束"),
    User("user", "USER", "用户"),
    Materialized_View("materialized view", "MV", "物化视图"),
    ;
    private ObjectEnum(String name, String code, String desc) {
        this.name = name;
        this.code = code;
        this.desc = desc;
    }
    private String name;
    private String code;
    private String desc;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
