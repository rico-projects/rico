package org.gradle.sample.transform.javamodules;

import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.jar.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/**
 * An artifact transform that applies additional information to Jars without module information.
 * The transformation fails the build if a Jar does not contain information and no extra information
 * was defined for it. This way we make sure that all Jars are turned into modules.
 */
abstract public class ExtraModuleInfoTransform implements TransformAction<ExtraModuleInfoPluginExtension> {

    private static final Pattern modulePattern = Pattern.compile("(?:META-INF/versions/\\d+/){0,1}module-info.class");
    private static final Pattern digits = Pattern.compile("\\d");
    private static final Pattern surplusAndJarEnding = Pattern.compile("(-|\\\\.)+(jar)*$");


    @InputArtifact
    protected abstract Provider<FileSystemLocation> getInputArtifact();

    // this is a very! simple name mangling for modules. It might not reflect what java does
    private String transformToModuleName(String originalJarName) {
        String beforeDigits = digits.split(originalJarName, 2)[0];
        String withoutEndingAndSurplus =
                surplusAndJarEnding.split(beforeDigits, 2)[0].replace('-', '.');
        if (originalJarName.contains("api") && !withoutEndingAndSurplus.contains("api")) {
            return withoutEndingAndSurplus+".api";
        } else {
            return withoutEndingAndSurplus;
        }
    }

    @Override
    public void transform(TransformOutputs outputs) {
        Map<String, ModuleInfo> moduleInfo = getParameters().getModuleInfo();
        Map<String, String> automaticModules = getParameters().getAutomaticModules();
        File originalJar = getInputArtifact().get().getAsFile();
        boolean autoConvert = getParameters().getAutoConvert();

        String originalJarName = originalJar.getName();

        if (isModule(originalJar)) {
            outputs.file(originalJar);
        } else if (moduleInfo.containsKey(originalJarName)) {
            addModuleDescriptor(originalJar, getModuleJar(outputs, originalJar), moduleInfo.get(originalJarName));
        } else if (isAutoModule(originalJar)) {
            outputs.file(originalJar);
        } else if (automaticModules.containsKey(originalJarName)) {
            addAutomaticModuleName(originalJar, getModuleJar(outputs, originalJar), automaticModules.get(originalJarName));
        } else if (autoConvert) {
            addAutomaticModuleName(originalJar, getModuleJar(outputs, originalJar), transformToModuleName(originalJarName));
        } else {
            throw new RuntimeException("Not a module and no mapping defined: " + originalJarName);
        }
    }

    private boolean isModule(File jar) {
        // This does not fully check multi-release jars
        try (JarInputStream inputStream =  new JarInputStream(new FileInputStream(jar))) {
            ZipEntry next = inputStream.getNextEntry();
            while (next != null) {
                if (modulePattern.matcher(next.getName()).matches()) {
                    return true;
                }
                next = inputStream.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean isAutoModule(File jar) {
        try (JarInputStream inputStream = new JarInputStream(new FileInputStream(jar))) {
            return inputStream.getManifest().getMainAttributes().getValue("Automatic-Module-Name") != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException npe) {
            return false;
        }
    }

    private File getModuleJar(TransformOutputs outputs, File originalJar) {
        return outputs.file(originalJar.getName().substring(0, originalJar.getName().lastIndexOf('.')) + "-module.jar");
    }

    private static void addAutomaticModuleName(File originalJar, File moduleJar, String moduleName) {
        try (JarInputStream inputStream = new JarInputStream(new FileInputStream(originalJar))) {
            Manifest manifest = inputStream.getManifest();
            if (manifest == null) {
                manifest = new Manifest();
            }
            manifest.getMainAttributes().put(new Attributes.Name("Automatic-Module-Name"), moduleName);
            try (JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(moduleJar), manifest)) {
                copyEntries(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addModuleDescriptor(File originalJar, File moduleJar, ModuleInfo moduleInfo) {
        try (JarInputStream inputStream = new JarInputStream(new FileInputStream(originalJar))) {
            try (JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(moduleJar), inputStream.getManifest())) {
                copyEntries(inputStream, outputStream);
                outputStream.putNextEntry(new JarEntry("module-info.class"));
                outputStream.write(addModuleInfo(moduleInfo));
                outputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyEntries(JarInputStream inputStream, JarOutputStream outputStream) throws IOException {
        JarEntry jarEntry = inputStream.getNextJarEntry();
        while (jarEntry != null) {
            outputStream.putNextEntry(jarEntry);
            outputStream.write(inputStream.readAllBytes());
            outputStream.closeEntry();
            jarEntry = inputStream.getNextJarEntry();
        }
    }

    private static byte[] addModuleInfo(ModuleInfo moduleInfo) {
        ClassWriter classWriter = new ClassWriter(0);
        classWriter.visit(Opcodes.V9, Opcodes.ACC_MODULE, "module-info", null, null, null);
        ModuleVisitor moduleVisitor = classWriter.visitModule(moduleInfo.getModuleName(), Opcodes.ACC_OPEN, moduleInfo.getModuleVersion());
        for (String packageName : moduleInfo.getExports()) {
            moduleVisitor.visitExport(packageName.replace('.', '/'), 0);
        }
        moduleVisitor.visitRequire("java.base", 0, null);
        for (String requireName : moduleInfo.getRequires()) {
            moduleVisitor.visitRequire(requireName, 0, null);
        }
        for (String requireName : moduleInfo.getRequiresTransitive()) {
            moduleVisitor.visitRequire(requireName, Opcodes.ACC_TRANSITIVE, null);
        }
        moduleVisitor.visitEnd();
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }
}
