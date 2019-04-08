package com.weiggle.github.apt_processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.weiggle.github.apt_api.BindView;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.*;

import static com.google.auto.common.MoreElements.getPackage;
import static javax.lang.model.element.Modifier.PUBLIC;

@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Types typeUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(BindView.class.getCanonicalName());
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    private Map<TypeElement, List<Element>> elementPackage = new HashMap<>();
    private static final String VIEW_TYPE = "android.view.View";
    private static final String VIEW_BIND = "com.weiggle.github.apt_api.ViewBinding";



    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (null == set || set.isEmpty()) {
            return false;
        }
        elementPackage.clear();
        Set<? extends Element> bindViewElement = roundEnvironment.getElementsAnnotatedWith(BindView.class);

        collectData(bindViewElement);
        generateCode();
        return false;
    }

    private void collectData(Set<? extends Element> elements) {
        Iterator<? extends Element> iterable = elements.iterator();
        while (iterable.hasNext()) {
            Element element = iterable.next();
            TypeMirror typeMirror = element.asType();
            TypeMirror viewTypeMirror = elementUtils.getTypeElement(VIEW_TYPE).asType();
            if (typeUtils.isSubtype(typeMirror, viewTypeMirror) || typeUtils.isSameType(typeMirror, viewTypeMirror)) {
                TypeElement parent = (TypeElement) element.getEnclosingElement();
                System.out.println("parent==>"+parent.getSimpleName().toString());
                List<Element> parentElments = elementPackage.get(parent);
                if (null == parentElments) {
                    parentElments = new ArrayList<>();
                    elementPackage.put(parent, parentElments);
                }
                parentElments.add(element);
            } else {
                throw new RuntimeException("错误处理， BindView 应该标注在类型是View的字段上");
            }
        }
    }


    private void generateCode(){
        Set<Map.Entry<TypeElement, List<Element>>> entrySet = elementPackage.entrySet();
        Iterator<Map.Entry<TypeElement, List<Element>>> iterator = entrySet.iterator();
        while (iterator.hasNext()){
            Map.Entry<TypeElement, List<Element>> entry = iterator.next();
            TypeElement parent = entry.getKey();
            List<Element> elements = entry.getValue();

            MethodSpec methodSpec = generateBindViewMethod(parent, elements);
            String packageName = getPackage(parent).getQualifiedName().toString();

            ClassName viewBinderInterface = ClassName.get(elementUtils.getTypeElement(VIEW_BIND));
            System.out.println("viewBinderInterface==>"+viewBinderInterface.simpleName());
            String className = parent.getQualifiedName().toString().substring(packageName.length()+1).replace(".","$");
            System.out.println("className==>"+className);
            ClassName bindingClassName = ClassName.get(packageName, className + "_ViewBinding");
            System.out.println("bindingClassName==>"+bindingClassName.simpleName());
            try {
                //生成 className_ViewBinding.java文件
                JavaFile.builder(packageName, TypeSpec.classBuilder(bindingClassName)
                        .addModifiers(PUBLIC)
                        .addSuperinterface(viewBinderInterface)
                        .addMethod(methodSpec)
                        .build()
                ).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private MethodSpec generateBindViewMethod(TypeElement parent, List<Element> elements) {
        ParameterSpec.Builder parameter = ParameterSpec.builder(TypeName.OBJECT, "target");
        MethodSpec.Builder bindViewMethod = MethodSpec.methodBuilder("bindView");
        bindViewMethod.addParameter(parameter.build());
        bindViewMethod.addModifiers(PUBLIC);
        bindViewMethod.addStatement("$T temp = ($T)target", parent, parent);
        for (Element element: elements) {
            int id = element.getAnnotation(BindView.class).value();
            bindViewMethod.addStatement("temp.$N = temp.findViewById($L)", element.getSimpleName().toString(), id);
        }
        return bindViewMethod.build();
    }


}
