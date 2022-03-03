package org.example.neo4jspringboot.service;

import org.eclipse.core.runtime.CoreException;
import org.example.neo4jspringboot.dao.*;
import org.example.neo4jspringboot.entity.CFunctionInfo;
import org.example.neo4jspringboot.entity.CProjectInfo;
import org.example.neo4jspringboot.utils.FunctionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectEntry {
    @Autowired
    CCodeFileRepository cCodeFileRepository;

    @Autowired
    CFunctionRepository cFunctionRepository;

    @Autowired
    CDataStructureRepository cDataStructureRepository;

    @Autowired
    CFieldRepository cFieldRepository;

    @Autowired
    CVariableRepository cVariableRepository;

    public void run() throws IOException, CoreException {
        for (int i = 0; i < FunctionUtil.SIZE_OF_FUNCTION_HASH_SET; i++) {
            FunctionUtil.FUNCTION_HASH_LIST[i] = new ArrayList<>();
        }
        CProjectInfo projectInfo = new CProjectInfo();
//        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/phd/ungraduate/cJSON/");
        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/Code/C++/csp");
//        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/phd/ungraduate/ideaWorkspace/test2/src/main/resources");
//        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/phd/ungraduate/qemu-master");
//        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/phd/ungraduate/kernel_liteos_a-master");
//        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/phd/ungraduate/linux-master/arch");
//        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/phd/ungraduate/linux-master/block");

        projectInfo.getCodeFileInfoMap().values().forEach(cCodeFileInfo -> {
            cCodeFileRepository.save(cCodeFileInfo);
        });

        final int[] numberOfFirstSeqFiles = {0};
        final int numberOfFiles = projectInfo.getNumberOfFiles();
        projectInfo.getCodeFileInfoMap().values().forEach(cCodeFileInfo -> {
            long sTime = System.currentTimeMillis();
            cCodeFileInfo.initIncludeCodeFiles();
            cCodeFileInfo.initFunctions();
            cCodeFileInfo.initDataStructures();
            cCodeFileInfo.initVariables();
            // 构建CodeFileIncludeCodeFile关系
            cCodeFileInfo.getIncludeCodeFileList().forEach(key -> {
                if(projectInfo.getCodeFileInfoMap().containsKey(key)) {
                    cCodeFileRepository.createCodeFileIncludeCodeFileR(cCodeFileInfo.getFileName(), key, "include");
                }
            });
            cCodeFileInfo.getFunctionInfoList().forEach(cFunctionInfo -> {
                cFunctionRepository.save(cFunctionInfo);
                cCodeFileRepository.createCodeFileDefineFunctionR(cCodeFileInfo.getFileName(),
                        cCodeFileInfo.getFileName() + cFunctionInfo.getName(), "define");
            });

            cCodeFileInfo.getDataStructureList().forEach(cDataStructureInfo -> {
                cDataStructureRepository.save(cDataStructureInfo);
                cCodeFileRepository.createCodeFileDefineDataStructureR(cCodeFileInfo.getFileName(), cDataStructureInfo.getName(), "define");
                cDataStructureInfo.getFieldInfoList().forEach(cFieldInfo -> {
                    cFieldRepository.save(cFieldInfo);
                    cFieldRepository.createFieldMemberOfDataStructureR(cFieldInfo.getName(), cDataStructureInfo.getName(), "memberof");
                });
            });
            cCodeFileInfo.getVariableInfoList().forEach(cVariableInfo -> {
                cVariableRepository.save(cVariableInfo);
                cCodeFileRepository.createCodeFileDefineVariableR(cCodeFileInfo.getFileName(), cVariableInfo.getName(), "define");
            });

            long eTime = System.currentTimeMillis();
            numberOfFirstSeqFiles[0]++;
            if(eTime - sTime > 9999) {
                System.out.printf("%s 执行时间：%d 毫秒, \t\t进度：%d / %d\n", cCodeFileInfo.getFileName(), (eTime - sTime),
                        numberOfFirstSeqFiles[0], numberOfFiles);
            } else {
                System.out.printf("进度：%d / %d\n", numberOfFirstSeqFiles[0], numberOfFiles);
            }
        });

        System.out.println("!!!!第一个大括号结束了");

        // bug 作为函数参数调用 改了
        final int[] numberOfSecondSeqFiles = {0};
        projectInfo.getCodeFileInfoMap().values().forEach(cCodeFileInfo -> {
            long sTime = System.currentTimeMillis();
            cCodeFileInfo.getFunctionInfoList().forEach(CFunctionInfo::initCallFunctionNameList);
            cCodeFileInfo.getFunctionInfoList().forEach(cFunctionInfo -> {
                List<String> newFilter = new ArrayList<>();
                List<String> old = cFunctionInfo.getCallFunctionNameList();
                assert old != null;
                for (String s : old) {
                    List<CFunctionInfo> tempList = FunctionUtil.FUNCTION_HASH_LIST[FunctionUtil.hashFunc(s)];
                    if(tempList.size() > 1) {
                        List<String> includeCodeFileList = cCodeFileInfo.getIncludeCodeFileList();
                        for (CFunctionInfo info : tempList) {
                            if (cFunctionInfo.getBelongTo().equals(info.getBelongTo())) {
                                // (2)
                                newFilter.add(info.getBelongTo() + s);
                            } else {
                                for (String includeFileName : includeCodeFileList) {
                                    if(includeFileName.contains(info.getBelongTo())) {
                                        // (1)
                                        newFilter.add(info.getBelongTo() + s);
                                    }
                                }
                            }
                        }
                    } else if (tempList.size() == 1) {
                        CFunctionInfo only = tempList.get(0);
                        // 只查到了一个那就直接扔进去 不然也没啥意义了
                        newFilter.add(only.getBelongTo() + s);
                    }
                }
                cFunctionInfo.setCallFunctionNameList(newFilter);
            });

            cCodeFileInfo.getFunctionInfoList().forEach(cFunctionInfo -> {
                cFunctionInfo.getCallFunctionNameList().forEach(name -> {
                    cFunctionRepository.createFunctionInvokeFunctionR(cFunctionInfo.getBelongTo() + cFunctionInfo.getName(),
                            name, "invoke");
                });
            });
            long eTime = System.currentTimeMillis();
            numberOfSecondSeqFiles[0]++;
            if(eTime - sTime > 9999) {
                System.out.printf("second %s 执行时间：%d 毫秒, \t\t进度：%d / %d\n", cCodeFileInfo.getFileName(), (eTime - sTime),
                        numberOfSecondSeqFiles[0], numberOfFiles);
            } else {
                System.out.printf("进度：%d / %d\n", numberOfSecondSeqFiles[0], numberOfFiles);
            }
        });
    }
}
