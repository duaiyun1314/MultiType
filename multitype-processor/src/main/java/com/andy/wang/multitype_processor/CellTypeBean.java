package com.andy.wang.multitype_processor;


public class CellTypeBean {
    public String pkgName;
    public String clsName;
    public int cellType;
    public String doc;

    @Override
    public String toString() {
        return "CellTypeBean{" +
                "pkgName='" + pkgName + '\'' +
                ", clsName='" + clsName + '\'' +
                ", cellType=" + cellType +
                ", doc='" + doc + '\'' +
                '}';
    }
}
