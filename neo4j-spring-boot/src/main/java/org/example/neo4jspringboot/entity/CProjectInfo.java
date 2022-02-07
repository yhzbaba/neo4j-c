package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.example.neo4jspringboot.utils.GetTranslationUnitUtil;
import org.example.neo4jspringboot.utils.ProjectFilesReader;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CProjectInfo {
    private Map<String, CCodeFileInfo> codeFileInfoMap = new HashMap<>();

    private int numberOfFiles;

    /**
     * 项目路径下所有文件构造TranslationUnit，构造对应CCodeFileInfo,这个函数结束codeFileInfoMap就构建完成了
     * @param dir 项目路径
     * @throws IOException
     * @throws CoreException
     */
    public void makeTranslationUnits(String dir) throws IOException, CoreException {
        ProjectFilesReader fileComponent = new ProjectFilesReader(dir);
        List<File> files = fileComponent.getAllFilesAndDirsList();
        for (File file: files) {
            if (file.isFile()){

                String fileFullName = file.getAbsolutePath();
                String fileName = file.getName();
                if(fileName.contains(".")) {
                    String substring = fileName.substring(fileName.lastIndexOf("."));
                    if(!".c".equals(substring) && !".h".equals(substring)){
                        continue;
                    }
                } else {
                    continue;
                }
                System.out.println(fileFullName);
                numberOfFiles++;
                IASTTranslationUnit translationUnit = GetTranslationUnitUtil.getASTTranslationUnit(new File(fileFullName));
                CCodeFileInfo codeFileInfo = new CCodeFileInfo(fileFullName, fileName, translationUnit);
                codeFileInfoMap.put(fileFullName, codeFileInfo);
            }
        }
    }
}
