package com.jean.redisClient.model;

/**
 * Created by jinshubao on 2016/11/28.
 */
public class DirModel extends DbModel {

    protected String dirName;

    public DirModel(DbModel dbModel, String dirName) {
        super(dbModel, dbModel.dbIndex);
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public String toString() {
        return dirName == null ? "" : dirName;
    }
}
