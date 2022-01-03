package org.example.neo4jspringboot.utils;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectFilesReader {
    File curFile;

    List<ProjectFilesReader> fileComponentList = new ArrayList<>();

    public ProjectFilesReader(File file) {
        curFile = file;

        File[] childFiles = curFile.listFiles();

        if(null != childFiles && childFiles.length > 0) {
            for (File child : childFiles) {
                ProjectFilesReader childFileComp = new ProjectFilesReader(child);
                fileComponentList.add(childFileComp);
            }
        }
    }

    public ProjectFilesReader(String filePath) {
        this(new File(filePath));
    }

    public List<File> getAllFilesAndDirsList() {
        List<File> files = new ArrayList<>();
        files.add(curFile);
        for (ProjectFilesReader child: fileComponentList) {
            files.addAll(child.getAllFilesAndDirsList());
        }
        return files;
    }
}
