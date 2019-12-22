package com.gb.dcloud.common;

import java.util.ArrayList;
import java.util.List;

public class FileList extends AbstractMessage {
    private List<String> list = new ArrayList<>();

    public List<String> getData() {
        return list;
    }

    public FileList (List<String> list1){
        this.list = list1;
    }
}
