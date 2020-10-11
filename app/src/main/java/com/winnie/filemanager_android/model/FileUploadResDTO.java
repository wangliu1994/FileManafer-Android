package com.winnie.filemanager_android.model;

/**
 * @Description: 上传结果
 * @author: winnie
 * @date: 2020年10月11日
 */
public class FileUploadResDTO {
    /**
     * 单号
     */
    private String number;

    /**
     * 归档日期
     */
    private Long date;

    /**
     * 归档地址
     */
    private String filePath;


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
