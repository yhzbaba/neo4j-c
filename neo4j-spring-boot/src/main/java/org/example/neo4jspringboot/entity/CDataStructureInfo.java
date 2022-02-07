package org.example.neo4jspringboot.entity;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import org.eclipse.cdt.core.dom.ast.*;
import org.example.neo4jspringboot.utils.ASTUtil;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.ArrayList;
import java.util.List;

@NodeEntity(label = "c-struct-node")
@Data
public class CDataStructureInfo {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String name;

    @Property
    private Boolean isEnum;

    @Property
    private String typedefName;

    private IASTSimpleDeclaration simpleDeclaration;

    private List<CFieldInfo> fieldInfoList = new ArrayList<>();

    public void initEnumFieldInfo() {
//        for (IASTNode node: simpleDeclaration.getChildren()) {
//            for (IASTNode node2: node.getChildren()) {
//                if (node2 instanceof IASTEnumerationSpecifier.IASTEnumerator) {
//                    CFieldInfo fieldInfo = new CFieldInfo();
//                    fieldInfo.setName(((IASTEnumerationSpecifier.IASTEnumerator) node2).getName().toString());
//                    fieldInfo.setType("int");
//                    fieldInfoList.add(fieldInfo);
//                }
//            }
//        }
    }

    public void initStructFieldInfo() {
        boolean isPointer;
        for (IASTNode node: simpleDeclaration.getChildren()) {
            for (IASTNode node2: node.getChildren()) {
                CFieldInfo fieldInfo = new CFieldInfo();
                StringBuilder name = new StringBuilder();
                StringBuilder type = new StringBuilder();
                isPointer = false;
                boolean isArray = false;
                for(IASTNode node3: node2.getChildren()) {
                    if (node3 instanceof IASTDeclarator) {
                        // 名字部分 目前的策略是只有直接的函数指针保存
                        if (ASTUtil.hasPointerType((IASTDeclarator)node3)){
                            // 指针
                            isPointer = true;
                            for (IASTNode node4: node3.getChildren()) {
                                if (node4 instanceof IASTDeclarator) {
                                    name.append(((IASTDeclarator) node4).getName().toString());
                                }
                            }
                        }
                        if (node3 instanceof IASTArrayDeclarator) {
                            isArray = true;
                        }
                    } else if (node3 instanceof IASTDeclSpecifier) {
                        // 类型部分 还没有处理函数指针
                        type.append(node3.getRawSignature());
                    }
                }
                if ("".equals(name.toString())) {
                    continue;
                }
                fieldInfo.setName(name.toString());
                if (isPointer) {
                    type.append("*");
                }
                if (isArray) {
                    type.append("[]");
                }
                fieldInfo.setType(type.toString());
                if(isPointer) {
                    fieldInfoList.add(fieldInfo);
                }
            }
        }
    }
}
