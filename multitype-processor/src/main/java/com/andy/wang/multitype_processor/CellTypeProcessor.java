package com.andy.wang.multitype_processor;

import com.andy.wang.multitype_annotations.CellType;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)//注册注解处理器
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CellTypeProcessor extends AbstractProcessor {

    private Messager mMessage;
    private Types mTypeUtils;
    private static final String CELL_TYPE_TARGET = "androidx.recyclerview.widget.RecyclerView.ViewHolder";
    private Filer mFiler;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> types = new LinkedHashSet<>();
        types.add(CellType.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessage = processingEnv.getMessager();
        mTypeUtils = processingEnv.getTypeUtils();
        mFiler = processingEnv.getFiler();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessage.printMessage(Diagnostic.Kind.WARNING, "================  process start ================");
        for (TypeElement annotation : annotations) {
            mMessage.printMessage(Diagnostic.Kind.WARNING, "annotation:" + annotation.getQualifiedName());
            if (annotation.getQualifiedName().toString().equals(CellType.class.getCanonicalName())) {
                List<CellTypeBean> cellTypeBeans = generateCellTypeBeanList(roundEnv.getElementsAnnotatedWith(annotation));
                generateJavaFile(cellTypeBeans);
            }
        }
        mMessage.printMessage(Diagnostic.Kind.WARNING, "================  process end ================");
        return false;
    }

    /**
     * 生成celltype与viewholder的映射表的类
     *
     * @param cellTypeBeans
     */
    private void generateJavaFile(List<CellTypeBean> cellTypeBeans) {
        try {
            CodeBlock.Builder builder = CodeBlock.builder();
            for (CellTypeBean cellTypeBean : cellTypeBeans) {
                builder.addStatement("source.put($L, $T.class)", cellTypeBean.cellType, ClassName.get(cellTypeBean.pkgName, cellTypeBean.clsName));
            }

            ClassName sparseArray = ClassName.get("android.util", "SparseArray");
            MethodSpec methodSpec = MethodSpec.methodBuilder("loadCellTypeMap")
                    .addParameter(sparseArray, "source")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(builder.build())
                    .build();

            TypeSpec typeSpec = TypeSpec.classBuilder("CellTypeMap")
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get("com.andy.wang.multitype", "ICellTypeMap"))
                    .addMethod(methodSpec)
                    .build();
            JavaFile javaFile = JavaFile.builder("com.andy.wang", typeSpec).build();
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();
            mMessage.printMessage(Diagnostic.Kind.ERROR, "生成celltype映射类失败：" + e.getMessage());
        }
    }

    /**
     * 将所有信息解析成bean
     *
     * @param elementsAnnotatedWith
     * @return
     */
    private List<CellTypeBean> generateCellTypeBeanList(Set<? extends Element> elementsAnnotatedWith) {
        List<CellTypeBean> cellTypeBeanList = new ArrayList<>();
        for (Element element : elementsAnnotatedWith) {
            if (!(element instanceof TypeElement)) {
                mMessage.printMessage(Diagnostic.Kind.ERROR, "CellType注解只能用于class");
                break;
            }
            TypeElement typeElement = (TypeElement) element;
            if (!checkCellTypeTarget(typeElement)) {
                break;
            }
            CellTypeBean cellTypeBean = new CellTypeBean();
            CellType cellTypeAnnotation = element.getAnnotation(CellType.class);
            cellTypeBean.cellType = cellTypeAnnotation.value();
            cellTypeBean.doc = cellTypeAnnotation.doc();
            cellTypeBean.pkgName = extractPkgName(typeElement.getQualifiedName().toString());
            cellTypeBean.clsName = typeElement.getSimpleName().toString();
            cellTypeBeanList.add(cellTypeBean);
        }
        return cellTypeBeanList;
    }

    /**
     * 检测CellType注解的是否是ViewHolder
     *
     * @param typeElement
     * @return
     */
    private boolean checkCellTypeTarget(TypeElement typeElement) {
        TypeMirror superClass = typeElement.getSuperclass();
        if (superClass.getKind() == TypeKind.NONE) {
            return false;
        } else if (superClass.toString().equals(CELL_TYPE_TARGET)) {
            return true;
        } else {
            return checkCellTypeTarget((TypeElement) mTypeUtils.asElement(superClass));
        }
    }

    /**
     * 截取包名
     *
     * @param fullClassName
     * @return
     */
    protected String extractPkgName(String fullClassName) {
        int index = fullClassName.lastIndexOf(".");
        return index != -1 ? fullClassName.substring(0, index) : "";
    }
}
