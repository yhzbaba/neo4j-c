package org.example.neo4jspringboot.entity;

import lombok.Data;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.c.ICASTTypedefNameSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTParameterDeclaration;
import org.example.neo4jspringboot.utils.ASTUtil;
import org.example.neo4jspringboot.utils.FunctionUtil;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NodeEntity(label = "c-code-file-node")
public class CCodeFileInfo implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String fileName;

    private String tailFileName;

    private IASTTranslationUnit unit;

    /**
     * 为了构建CodeFileDefineFunction的关系
     */
    private List<CFunctionInfo> functionInfoList = new ArrayList<>();

    /**
     * 为了构建CodeFileIncludeCodeFile的关系，这里范型变成String是因为我们在初始化项目的时候CodeFile的node已经建立了
     */
    private List<String> includeCodeFileList = new ArrayList<>();

    /**
     * 为了构建CodeFileDefineDataStructure的关系
     */
    private List<CDataStructureInfo> dataStructureList = new ArrayList<>();

    /**
     * 为了构建CodeFileDefineVariable的关系
     */
    private List<CVariableInfo> variableInfoList = new ArrayList<>();

    public CCodeFileInfo(String fileName, String tailFileName, IASTTranslationUnit unit) {
        this.fileName = fileName;
        this.unit = unit;
        this.tailFileName = tailFileName;
    }

    /**
     * 处理函数声明
     * 暂时不包括宏函数
     */
    public void initFunctions(){
        IASTDeclaration[] declarations = unit.getDeclarations();
        for(IASTDeclaration declaration : declarations) {
            if(declaration instanceof IASTFunctionDefinition) {
                CFunctionInfo functionInfo = new CFunctionInfo();
                IASTFunctionDefinition functionDefinition = (IASTFunctionDefinition)declaration;
                functionInfo.setFunctionDefinition(functionDefinition);
                IASTDeclSpecifier declSpecifier = functionDefinition.getDeclSpecifier();
                IASTDeclarator declarator = functionDefinition.getDeclarator();
                String functionName = declarator.getName().toString();
                functionInfo.setName(functionName);
                String fullFunctionName = declarator.getRawSignature();
                functionInfo.setFullName(fullFunctionName);
//                functionInfo.setContent(functionDefinition.getBody().getRawSignature());
                functionInfo.setBelongTo(fileName);
                functionInfo.setBelongToName(fileName + functionName);
                for (IASTNode child : declarator.getChildren()) {
                    if(child instanceof CASTParameterDeclaration) {
                        CASTParameterDeclaration childP = (CASTParameterDeclaration)child;
                        functionInfo.getFullParams().add(childP.getRawSignature());
                    }
                }
                functionInfo.setIsInline(declSpecifier.isInline());
                functionInfo.setIsConst(declSpecifier.isConst());
                functionInfo.setIsDefine(false);
                FunctionUtil.FUNCTION_HASH_LIST[FunctionUtil.hashFunc(functionName)].add(functionInfo);
                functionInfoList.add(functionInfo);
            }
        }
    }

    /**
     * 处理结构体、枚举
     */
    public void initDataStructures() {
        IASTDeclaration[] declarations = unit.getDeclarations();
        for(IASTDeclaration declaration : declarations) {
            if (declaration instanceof IASTSimpleDeclaration) {
                IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration)declaration;
                IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
                if (declSpecifier instanceof IASTEnumerationSpecifier) {
                    // 这块区域是enum，包括typedef
                    CDataStructureInfo structureInfo = new CDataStructureInfo();
                    structureInfo.setSimpleDeclaration(simpleDeclaration);
                    structureInfo.setIsEnum(true);
                    structureInfo.setName(((IASTEnumerationSpecifier) declSpecifier).getName().toString());
                    structureInfo.setTypedefName("");
                    if (ASTUtil.isTypeDef(declSpecifier)) {
                        // 是typedef enum
                        for(IASTDeclarator declarator: simpleDeclaration.getDeclarators()) {
                            structureInfo.setTypedefName(declarator.getName().toString());
                        }
                    }
                    structureInfo.initEnumFieldInfo();
                    dataStructureList.add(structureInfo);
                } else if (declSpecifier instanceof IASTCompositeTypeSpecifier) {
                    // 结构体 包括typedef
                    CDataStructureInfo structureInfo = new CDataStructureInfo();
                    structureInfo.setSimpleDeclaration(simpleDeclaration);
//                    structureInfo.setContent(simpleDeclaration.getRawSignature());
                    structureInfo.setIsEnum(false);
                    structureInfo.setName(((IASTCompositeTypeSpecifier) declSpecifier).getName().toString());
                    structureInfo.setTypedefName("");
                    if (ASTUtil.isTypeDef(declSpecifier)) {
                        // 是typedef struct
                        for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
                            structureInfo.setTypedefName(declarator.getName().toString());
                        }
                    }
                    structureInfo.initStructFieldInfo();
                    dataStructureList.add(structureInfo);
                }
            }
        }
    }

    /**
     * 处理文件声明的变量
     *
     */
    public void initVariables() {
        IASTDeclaration[] declarations = unit.getDeclarations();
        for(IASTDeclaration declaration : declarations) {
            if (declaration instanceof IASTSimpleDeclaration) {
                IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
                IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
                if (declSpecifier instanceof IASTSimpleDeclSpecifier) {
                    IASTSimpleDeclSpecifier simpleDeclSpecifier = (IASTSimpleDeclSpecifier)declSpecifier;
                    CVariableInfo variableInfo = new CVariableInfo();
                    variableInfo.setSpecifier(simpleDeclSpecifier);
                    variableInfo.setSimpleDeclaration(simpleDeclaration);
                    for(IASTDeclarator declarator: simpleDeclaration.getDeclarators()) {
                        variableInfo.setName(declarator.getName().toString());
                    }
                    variableInfo.setBelongTo(fileName);
                    // typedef long long ll;
                    variableInfo.setIsDefine(ASTUtil.isTypeDef(declSpecifier));
                    variableInfo.setContent(declaration.getRawSignature());
                    variableInfo.setIsStructVariable(false);
                    variableInfoList.add(variableInfo);
                } else if (declSpecifier instanceof IASTElaboratedTypeSpecifier) {
                    // 不使用typedef名字进行声明的结构体变量
                    IASTElaboratedTypeSpecifier elaboratedTypeSpecifier = (IASTElaboratedTypeSpecifier)declSpecifier;
                    CVariableInfo variableInfo = new CVariableInfo();
                    variableInfo.setSpecifier(elaboratedTypeSpecifier);
                    variableInfo.setSimpleDeclaration(simpleDeclaration);
                    variableInfo.setBelongTo(fileName);
                    // typedef long long ll;
                    variableInfo.setIsDefine(ASTUtil.isTypeDef(declSpecifier));
                    for(IASTDeclarator declarator: simpleDeclaration.getDeclarators()) {
                        variableInfo.setName(declarator.getName().toString());
                    }
                    variableInfo.setContent(declaration.getRawSignature());
                    variableInfo.setIsStructVariable(true);
                    variableInfoList.add(variableInfo);
                } else if (declSpecifier instanceof ICASTTypedefNameSpecifier) {
                    // 使用typedef名字进行声明的结构体变量
                    ICASTTypedefNameSpecifier typedefNameSpecifier = (ICASTTypedefNameSpecifier)declSpecifier;
                    CVariableInfo variableInfo = new CVariableInfo();
                    variableInfo.setSpecifier(typedefNameSpecifier);
                    variableInfo.setSimpleDeclaration(simpleDeclaration);
                    variableInfo.setBelongTo(fileName);
                    // typedef long long ll;
                    variableInfo.setIsDefine(ASTUtil.isTypeDef(declSpecifier));
                    for(IASTDeclarator declarator: simpleDeclaration.getDeclarators()) {
                        variableInfo.setName(declarator.getName().toString());
                    }
                    variableInfo.setContent(declaration.getRawSignature());
                    variableInfo.setIsStructVariable(true);
                    variableInfoList.add(variableInfo);
                }
            }
        }
    }

    /**
     * 这个地方我暂时先只处理非系统文件，宏函数在这里，我认为宏函数的函数模式定义没啥用
     */
    public void initIncludeCodeFiles(){
        IASTPreprocessorStatement[] ps = unit.getAllPreprocessorStatements();
        for(IASTPreprocessorStatement statement : ps) {
            if (statement instanceof IASTPreprocessorFunctionStyleMacroDefinition) {
                // 宏函数
                IASTPreprocessorFunctionStyleMacroDefinition macroDefinition
                        = (IASTPreprocessorFunctionStyleMacroDefinition)statement;
                CFunctionInfo functionInfo = new CFunctionInfo();
                functionInfo.setMacroDefinition(macroDefinition);
                functionInfo.setIsDefine(true);
                functionInfo.setName(macroDefinition.getName().toString());
                for (IASTFunctionStyleMacroParameter parameter : macroDefinition.getParameters()){
                    functionInfo.getFullParams().add(parameter.toString());
                }
                functionInfo.setFullName("");
                functionInfo.setIsConst(false);
                functionInfo.setIsInline(true);
//                functionInfo.setContent(macroDefinition.getRawSignature());
                functionInfo.setBelongTo(fileName);
                functionInfo.setBelongToName(fileName + macroDefinition.getName().toString());
                functionInfoList.add(functionInfo);
            }
            if (statement instanceof IASTPreprocessorIncludeStatement) {
                IASTPreprocessorIncludeStatement includeStatement = (IASTPreprocessorIncludeStatement)statement;
                if (!includeStatement.isSystemInclude()) {
                    includeCodeFileList.add(fileName.substring(0, fileName.lastIndexOf('/') + 1) + includeStatement.getName().toString());
                }
            }
        }
    }
}
