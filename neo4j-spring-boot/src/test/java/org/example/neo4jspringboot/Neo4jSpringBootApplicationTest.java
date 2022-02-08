package org.example.neo4jspringboot;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.example.neo4jspringboot.dao.*;
import org.example.neo4jspringboot.entity.*;
import org.example.neo4jspringboot.utils.FunctionUtil;
import org.example.neo4jspringboot.utils.GetTranslationUnitUtil;
import org.example.neo4jspringboot.utils.ProjectFilesReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Neo4jSpringBootApplicationTest {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PersonRelationshipRepository personRelationshipRepository;

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

    @Test
    public void testCreate(){
//        Optional<Person> byId = personRepository.findById(123L);
//        byId.orElse(null);
        Person person = new Person();
        person.setName("yhz1");
        personRepository.save(person);
    }

    @Test
    public void testQuery(){
//        Optional<Person> byId = personRepository.findById(0L);
//        byId.orElse(null);
        long time1 = System.currentTimeMillis();
        List<CFunctionInfo> tempList = cFunctionRepository.getFunctionFromId("12408");
        long time2 = System.currentTimeMillis();
        System.out.println(tempList);
        System.out.printf("time2 - time1 = %d 毫秒\n", (time2 - time1));
    }

    @Test
    public void testDelete(){
        Optional<Person> byId = personRepository.findById(0L);
        byId.orElse(null);
        personRepository.deleteById(0L);
    }

    @Test
    public void buildRelationship(){
        Person person1 = new Person();
        person1.setName("杨戬");
        Person person2 = new Person();
        person2.setName("玉鼎真人");
        personRepository.save(person1);
        personRepository.save(person2);

        PersonRelationship relationship = new PersonRelationship();
        relationship.setParent(person1);
        relationship.setChild(person2);
        relationship.setRelation("师傅");

        personRelationshipRepository.save(relationship);
    }

    @Test
    public void buildRelationship2(){
        personRepository.createRelationship("yhz", "haha", "yhz1");
    }

    @Test
    public void getLines() throws IOException {
        long lines = 0;
//        ProjectFilesReader fileComponent = new ProjectFilesReader("/Users/yhzbaba/Documents/phd/ungraduate/kernel_liteos_a-master");
        ProjectFilesReader fileComponent = new ProjectFilesReader("/Users/yhzbaba/Documents/phd/ungraduate/linux-master/net");
        List<File> files = fileComponent.getAllFilesAndDirsList();
        for (File file: files) {
            if (file.isFile()){
                String fileFullName = file.getAbsolutePath();
                String fileName = file.getName();
                if(fileName.contains(".")) {
                    String substring = fileName.substring(fileName.lastIndexOf("."));
                    if(".c".equals(substring) || ".h".equals(substring)){
                        long tempLine = GetTranslationUnitUtil.getLines(file);
                        lines += tempLine;
                        System.out.println(fileFullName + "->" + tempLine + "->" + lines);
                    }
                }
            }
        }
        System.out.println(lines);
//        File file = new File("/Users/yhzbaba/Documents/phd/ungraduate/qemu-master/net/eth.c");
//        System.out.println(GetTranslationUnitUtil.getLines(file));
    }

    @Test
    public void testStringHash() {
//        List<CFunctionInfo>[] ls = new ArrayList[1111113];
//        for (int i = 0; i < 1111113; i++) {
//            ls[i] = new ArrayList<>();
//        }
//        String name = "alloc_pvd";
//        CFunctionInfo info = new CFunctionInfo();
//        info.setName(name);
//        ls[hashFunc(name)].add(info);
//
//        List<CFunctionInfo> list = ls[hashFunc(name)];
//        System.out.println(list);
        System.out.println(hashFunc("RTL"));
    }

    public static int hashFunc(String key){
        int arraySize = 1111113; 			//数组大小一般取质数
        int hashCode = 0;
        for(int i = 0; i < key.length(); i++){        //从字符串的左边开始计算
            int letterValue = key.charAt(i) - 40;//将获取到的字符串转换成数字，比如a的码值是97，则97-96=1 就代表a的值，同理b=2；
            hashCode = ((hashCode << 5) + letterValue + arraySize) % arraySize;//防止编码溢出，对每步结果都进行取模运算
        }
        return hashCode;
    }

    @Test
    public void readAllFiles() throws IOException, CoreException {
        for (int i = 0; i < FunctionUtil.SIZE_OF_FUNCTION_HASH_SET; i++) {
            FunctionUtil.FUNCTION_HASH_LIST[i] = new ArrayList<>();
        }
        CProjectInfo projectInfo = new CProjectInfo();
//        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/phd/ungraduate/cJSON/");
//        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/Code/C++/csp");
//        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/phd/ungraduate/ideaWorkspace/test2/src/main/resources");
        projectInfo.makeTranslationUnits("/Users/yhzbaba/Documents/phd/ungraduate/qemu-master");
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
                    // #include "include/abcd.h" 完整路径
                    // 看类图挨个处理
                    List<CFunctionInfo> tempList = FunctionUtil.FUNCTION_HASH_LIST[FunctionUtil.hashFunc(s)];
//                    List<CFunctionInfo> tempList = cFunctionRepository.getFunctionFromName(s);
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
