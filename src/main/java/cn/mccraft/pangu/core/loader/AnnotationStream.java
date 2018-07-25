package cn.mccraft.pangu.core.loader;

import com.google.common.collect.Sets;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A simple way to operate ASMDataTable.ASMData
 * @since 1.0.2
 */
public class AnnotationStream<T extends Annotation> {
    private final String typeName;
    private final Set<ASMDataTable.ASMData> asmDatas;

    public AnnotationStream(String typeName) {
        this.typeName = typeName;
        this.asmDatas = AnnotationInjector.INSTANCE.getDiscoverer().getASMTable().getAll(typeName);
    }

    public static <T extends Annotation> AnnotationStream<T> of(String typeName) {
        return new AnnotationStream<>(typeName);
    }

    public static <T extends Annotation> AnnotationStream<T> of(Class<T> annoClass) {
        return new AnnotationStream<>(annoClass.getName());
    }

    public String getTypeName() {
        return typeName;
    }

    public Set<ASMDataTable.ASMData> getASMDatas() {
        return asmDatas;
    }

    /**
     * Returns a sequential {@code Stream} with the native ASMData
     */
    public Stream<ASMDataTable.ASMData> stream() {
        return asmDatas.stream();
    }

    /**
     * Returns a sequential {@code Stream} with classes from element's declaring class.
     */
    public Stream<? extends Class<?>> classStream() {
        return asmDatas
                .stream()
                // get class name
                .map(ASMDataTable.ASMData::getClassName)
                .distinct()
                // map class entity
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (Exception e) {
                        return null;
                    }
                })
                // clean class could get instance
                .filter(Objects::nonNull);
    }

    /**
     * Returns a sequential {@code Stream} with classes annotated
     */
    @SuppressWarnings("unchecked")
    public Stream<? extends Class<?>> typeStream() {
        return asmDatas
                .stream()
                // filter that clean non-class object
                .filter(it -> it.getClassName().equals(it.getObjectName()))
                // get class name
                .map(ASMDataTable.ASMData::getClassName)
                .distinct()
                // map class entity
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (Exception e) {
                        return null;
                    }
                })
                // clean class could get instance
                .filter(Objects::nonNull);
    }

    /**
     * Returns a sequential {@code Stream} with fields annotated
     */
    public Stream<Field> fieldStream() {
        return asmDatas
                .stream()
                // filter that clean non-class object
                .filter(it -> !it.getClassName().equals(it.getObjectName()) && !it.getObjectName().contains("("))
                // map class entity
                .map(it -> {
                    try {
                        return Class.forName(it.getClassName()).getDeclaredField(it.getObjectName());
                    } catch (Exception e) {
                        return null;
                    }
                })
                // clean class could get instance
                .filter(Objects::nonNull);
    }
    /**
     * Returns a sequential {@code Stream} with method annotated
     */
    public Stream<Method> methodStream() {
        Set<Method> methods = Sets.newHashSet();
        classStream().forEach(
                clazz -> Arrays.stream(clazz.getDeclaredMethods())
                        .filter(method -> Arrays.stream(method.getAnnotations()).anyMatch(it -> typeName.equals(it.annotationType().getName())))
                        .forEach(methods::add)
        );
        return methods.stream();
    }
}