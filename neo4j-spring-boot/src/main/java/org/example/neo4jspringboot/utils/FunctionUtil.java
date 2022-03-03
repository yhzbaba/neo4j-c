package org.example.neo4jspringboot.utils;

import org.eclipse.cdt.core.dom.ast.*;
import org.example.neo4jspringboot.entity.CFunctionInfo;

import java.util.ArrayList;
import java.util.List;

public class FunctionUtil {
    public static int SIZE_OF_FUNCTION_HASH_SET = 1111113;

    public static List<CFunctionInfo>[] FUNCTION_HASH_LIST = new ArrayList[SIZE_OF_FUNCTION_HASH_SET];

    /**
     * 同时考虑了fun1() book.fun1() bookPtr->fun1() printf(fun1())
     * @param expression
     * @return
     */
    public static List<String> getFunctionNameFromFunctionCallExpression(IASTFunctionCallExpression expression) {
        List<String> result = new ArrayList<>();
        IASTInitializerClause[] arguments = expression.getArguments();
        // 处理参数
        for (IASTInitializerClause clause : arguments) {
            if (clause instanceof IASTFunctionCallExpression) {
                result.addAll(getFunctionNameFromFunctionCallExpression((IASTFunctionCallExpression)clause));
            }
        }
        for (IASTNode node : expression.getChildren()) {
            if(node instanceof IASTIdExpression) {
                result.add(node.getRawSignature());
            }else if(node instanceof IASTFieldReference) {
                for (IASTNode node1 : node.getChildren()) {
                    if(node1 instanceof IASTName) {
                        result.add(node1.getRawSignature());
                    }
                }
            }
        }
        return result;
    }

    public static List<String> getFunctionNameFromBinaryExpression(IASTBinaryExpression binaryExpression) {
        List<String> nameResult = new ArrayList<>();
        for (IASTNode node : binaryExpression.getChildren()) {
            if(node instanceof IASTFunctionCallExpression) {
                nameResult.addAll(getFunctionNameFromFunctionCallExpression((IASTFunctionCallExpression)node));
            }
        }

        return nameResult;
    }

    public static List<String> getFunctionNameFromExpressionStatement(IASTExpressionStatement statement) {
        List<String> result = new ArrayList<>();
        for (IASTNode node : statement.getChildren()) {
            if(node instanceof IASTFunctionCallExpression) {
                IASTFunctionCallExpression functionCallExpression = (IASTFunctionCallExpression)node;
                // 直接的函数调用语句
                // 获取函数名
                return getFunctionNameFromFunctionCallExpression(functionCallExpression);
            }
        }
        return result;
    }

    public static List<String> getFunctionNameFromReturnStatement(IASTReturnStatement statement) {
        List<String> result = new ArrayList<>();
        for (IASTNode node : statement.getChildren()) {
            if(node instanceof IASTFunctionCallExpression) {
                // 直接的函数调用语句
                result.addAll(getFunctionNameFromFunctionCallExpression((IASTFunctionCallExpression)node));
            }else if(node instanceof IASTBinaryExpression) {
                List<String> tResult = getFunctionNameFromBinaryExpression((IASTBinaryExpression)node);
                result.addAll(tResult);
            }
        }
        return result;
    }

    public static List<String> getFunctionNameFromDeclarationStatement(IASTDeclarationStatement statement) {
        List<String> result = new ArrayList<>();
        for (IASTNode node : statement.getChildren()) {
            for (IASTNode node1 : node.getChildren()) {
                if(node1 instanceof IASTDeclarator) {
                    for (IASTNode node2 : node1.getChildren()) {
                        if(node2 instanceof IASTEqualsInitializer) {
                            for (IASTNode node3 : node2.getChildren()) {
                                if(node3 instanceof IASTFunctionCallExpression) {
                                    result.addAll(getFunctionNameFromFunctionCallExpression((IASTFunctionCallExpression)node3));
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }

    public static List<String> getFunctionNameFromCompoundStatement(IASTCompoundStatement compoundStatement) {
        List<String> result = new ArrayList<>();
        IASTStatement[] statements = compoundStatement.getStatements();
        for (IASTStatement statement : statements) {
            if(statement instanceof IASTReturnStatement) {
                // return语句 可能出现函数调用 return fun1(2); return fun1(2) < fun2 (4);
                List<String> returnResult = getFunctionNameFromReturnStatement((IASTReturnStatement)statement);
                result.addAll(returnResult);
            } else if (statement instanceof IASTDeclarationStatement) {
                // int res = test1();
                result.addAll(getFunctionNameFromDeclarationStatement((IASTDeclarationStatement)statement));
            } else if (statement instanceof IASTExpressionStatement) {
                // fun1(2)
                result.addAll(getFunctionNameFromExpressionStatement((IASTExpressionStatement)statement));
            } else if (statement instanceof IASTForStatement) {
                for (IASTNode node : statement.getChildren()) {
                    if(node instanceof IASTBinaryExpression) {
                        List<String> binaryResult = getFunctionNameFromBinaryExpression((IASTBinaryExpression)node);
                        result.addAll(binaryResult);
                    } else if (node instanceof IASTCompoundStatement) {
                        List<String> compoundResult = getFunctionNameFromCompoundStatement((IASTCompoundStatement)node);
                        result.addAll(compoundResult);
                    }
                }
            } else if (statement instanceof IASTWhileStatement) {
                for (IASTNode node : statement.getChildren()) {
                    if(node instanceof IASTBinaryExpression) {
                        List<String> binaryResult = getFunctionNameFromBinaryExpression((IASTBinaryExpression)node);
                        result.addAll(binaryResult);
                    } else if (node instanceof IASTCompoundStatement) {
                        List<String> compoundResult = getFunctionNameFromCompoundStatement((IASTCompoundStatement)node);
                        result.addAll(compoundResult);
                    }
                }
            }
        }
        return result;
    }

    public static int hashFunc(String key){
        int arraySize = SIZE_OF_FUNCTION_HASH_SET;
        int hashCode = 0;
        for(int i = 0; i < key.length(); i++){
            int letterValue = key.charAt(i) - 40;
            hashCode = ((hashCode << 5) + letterValue + arraySize) % arraySize;
        }
        return hashCode;
    }
}
