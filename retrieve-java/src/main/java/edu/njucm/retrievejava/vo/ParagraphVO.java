package edu.njucm.retrievejava.vo;


public class ParagraphVO {
    private String type;

    private String section;

    private String secNum;

    private String text;

    public ParagraphVO(String type, String section, String secNum, String text) {
        this.type = type;
        this.section = section;
        this.secNum = secNum;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getSection() {
        return section;
    }

    public String getSecNum() {
        return secNum;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "ParagraphVO{" +
                "type='" + type + '\'' +
                ", section='" + section + '\'' +
                ", secNum='" + secNum + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
