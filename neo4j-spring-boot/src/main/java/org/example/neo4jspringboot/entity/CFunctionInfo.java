package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.eclipse.cdt.core.dom.ast.*;
import org.example.neo4jspringboot.dao.CFunctionRepository;
import org.example.neo4jspringboot.utils.FunctionUtil;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NodeEntity(label = "c-function-node")
public class CFunctionInfo {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 函数名
     */
    @Property
    private String name;

    /**
     * 完整函数声明
     */
    @Property
    private String fullName;

//    /**
//     * 返回值类型
//     */
//    @Property
//    private String returnType;

    /**
     * 某个结构体定义的函数，或是写在最外层的函数
     */
    @Property
    private String belongTo;

    /**
     * 参数列表
     */
    @Property
    private List<String> fullParams = new ArrayList<>();

    /**
     * 是否为inline
     */
    @Property
    private Boolean isInline;

    /**
     * 是否为const
     */
    @Property
    private Boolean isConst;

    /**
     * 是否是一个宏函数
     */
    @Property
    private Boolean isDefine;

    /**
     * 用作标识符
     */
    @Property
    private String belongToName;

    /**
     * 当isDefine为true此属性才有效
     */
    private IASTPreprocessorFunctionStyleMacroDefinition macroDefinition;

    /**
     * 当isDefine为false此属性才有效
     */
    private IASTFunctionDefinition functionDefinition;

    /**
     * 这个函数所调用的函数名的列表,初始化里面装的是调用的函数名列表，二次装的是belongToName
     */
    private List<String> callFunctionNameList = new ArrayList<>();

    /**
     * 处理调用函数名列表
     */
    public void initCallFunctionNameList() {
        if (!isDefine) {
            // 这不是宏函数
            IASTCompoundStatement compoundStatement = (IASTCompoundStatement) functionDefinition.getBody();
            IASTStatement[] statements = compoundStatement.getStatements();
//            List<String> finalResult = new ArrayList<>();
            Set<String> finalResult = new HashSet<>();
            for (IASTStatement statement : statements) {
                if (statement instanceof IASTReturnStatement) {
                    // return语句 可能出现函数调用 return fun1(2); return fun1(2) < fun2 (4);
                    finalResult.addAll(FunctionUtil.getFunctionNameFromReturnStatement((IASTReturnStatement) statement));
                } else if (statement instanceof IASTDeclarationStatement) {
                    // int res = test1();
                    finalResult.addAll(FunctionUtil.getFunctionNameFromDeclarationStatement((IASTDeclarationStatement) statement));
                } else if (statement instanceof IASTExpressionStatement) {
                    // fun1(2)
                    finalResult.addAll(FunctionUtil.getFunctionNameFromExpressionStatement((IASTExpressionStatement) statement));
                } else if (statement instanceof IASTForStatement) {
                    FunctionUtil.getFunctionNameAndUpdate(statement.getChildren(), finalResult);
                } else if (statement instanceof IASTWhileStatement) {
                    FunctionUtil.getFunctionNameAndUpdate(statement.getChildren(), finalResult);
                } else if (statement instanceof IASTIfStatement) {
                    FunctionUtil.getFunctionNameAndUpdate(statement.getChildren(), finalResult);
                } else if (statement instanceof IASTSwitchStatement) {
                    FunctionUtil.getFunctionNameAndUpdate(statement.getChildren(), finalResult);
                }
            }
            List<String> filtered = finalResult.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
            setCallFunctionNameList(filtered);
        }
    }
}
