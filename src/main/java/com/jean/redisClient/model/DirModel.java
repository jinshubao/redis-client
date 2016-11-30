package com.jean.redisClient.model;

/**
 * Created by jinshubao on 2016/11/28.
 */
public class DirModel extends DbModel {

    protected String dirName;
    protected String fullPath;

    public DirModel(DbModel dbModel, String dirName, String dirFullName) {
        super(dbModel, dbModel.dbIndex);
        this.dirName = dirName;
        this.fullPath = dirFullName;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    @Override
    public String toString() {
        return dirName == null ? "" : dirName;
    }

    public String location() {
        return super.location() + ":" + fullPath;
    }
}
