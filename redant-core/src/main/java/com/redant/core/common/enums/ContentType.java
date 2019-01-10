package com.redant.core.common.enums;

public enum ContentType {

    APPLICATION_ATOM_XML("application/atom+xml"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_JSON("application/json"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_SVG_XML("application/svg+xml"),
    APPLICATION_XHTML_XML("application/xhtml+xml"),
    APPLICATION_XML("application/xml")
    ;

    private String content;

    ContentType(String content){
        this.content = content;
    }
    
    @Override
    public String toString() {
        return content;
    }
}
