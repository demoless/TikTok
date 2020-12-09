package com.four.buildsrc.hotfix

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.internal.impldep.org.apache.ivy.util.FileUtil
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

abstract class AsmTransform: Transform() {
    companion object {
        private const val TAG = "AsmTransform"
    }
    override fun getName(): String {
        return TAG
    }


    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        //需要处理的数据类型,这里表示class文件
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        //是否增量编译
        return false
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        //作用范围
        return TransformManager.SCOPE_FULL_PROJECT
    }

    private val mWaitableExecutor by lazy {
        WaitableExecutor.useGlobalSharedThreadPool().apply {
            waitForTasksWithQuickFail<Any>(true)
        }
    }

    abstract fun getClassVisitor(classWriter: ClassWriter): ClassVisitor

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        val inputs: Collection<TransformInput>? = transformInvocation.inputs
        val outputProvider: TransformOutputProvider? = transformInvocation.outputProvider
        val isIncremental = transformInvocation.isIncremental

        if(!isIncremental) {
            //非增量编译 删除之前的所有文件
            outputProvider?.deleteAll()
        }
        inputs?.forEach { input ->

            if(outputProvider == null) {
                println("--------------outputProvider == null-----------------")
            }
            input.jarInputs.forEach { jarInput ->
                try {
                    //处理jar
                    mWaitableExecutor.execute {
                        processJarInput(jarInput, outputProvider!!, isIncremental)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            /*input.directoryInputs.forEach { directoryInput ->
                try {
                    //处理源码文件
                    mWaitableExecutor.execute {
                        processDirectoryInput(directoryInput, outputProvider!!, isIncremental)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }*/
        }
        mWaitableExecutor.waitForTasksWithQuickFail<Any>(true)
    }

    private fun processDirectoryInput(directoryInput: DirectoryInput, outputProvider: TransformOutputProvider,isIncremental: Boolean) {
        /*if(directoryInput.file.isDirectory) {
            val extensions = arrayOf("class")
            //递归地去获取该文件夹下面所有的文件
            val fileList = FileUtils.listFiles(directoryInput.file,extensions,true)

            fileList.forEach { inputFile ->
                val classReader = ClassReader(inputFile.readBytes())
                val classWriter = ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
                classReader.accept(HelloWorldClassVisitor(classWriter), ClassReader.EXPAND_FRAMES)
                val outputStream = FileOutputStream(inputFile.parentFile.absolutePath + File.separator + inputFile.name)
                outputStream.write(classWriter.toByteArray())
                outputStream.close()
            }
        }
        val directory = outputProvider.getContentLocation(directoryInput.name,directoryInput.contentTypes,directoryInput.scopes,Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file,directory)*/
        val directory = outputProvider.getContentLocation(directoryInput.name,directoryInput.contentTypes,directoryInput.scopes,Format.DIRECTORY)
        if (isIncremental) {
            val srcPath = directoryInput.file.absolutePath
            val destPath = directory.absolutePath
            val fileStatusMap = directoryInput.changedFiles
            fileStatusMap.entries.forEach { changeFile ->
                val status = changeFile.value
                val inputFile = changeFile.key
                val destFilePath = inputFile.absolutePath.replace(srcPath,destPath)
                val destFile = File(destFilePath)
                if(status == Status.REMOVED && destFile.exists()) {
                    FileUtils.forceDelete(destFile)
                } else if (status == Status.CHANGED || status == Status.ADDED) {
                    FileUtils.touch(destFile)
                    transformSingleFile(inputFile,destFile)
                }
            }
        } else {
            transformDirectory(directoryInput, directory)
            FileUtils.copyDirectory(directoryInput.file,directory)
        }
    }

    private fun processJarInput(jarInput: JarInput, outputProvider: TransformOutputProvider,isIncremental: Boolean) {

        var jarName = jarInput.name
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0,jarName.length - 4)
        }

        //临时文件存放正在操作中的文件
        val tempFile = File(jarInput.file.parent,File.separator + "temp_file.jar")
        //之前的缓存存在避免重复插桩
        if (tempFile.exists()) {
            tempFile.delete()
        }

        val tempOutputStream = JarOutputStream(FileOutputStream(tempFile))

        val jarFile = JarFile(jarInput.file)
        val jarFileEntries = jarFile.entries()
        while (jarFileEntries.hasMoreElements()) {
            val jarEntry = jarFileEntries.nextElement()
            val entryName = jarEntry.name
            val zipEntry = ZipEntry(entryName)
            if (zipEntry.isDirectory) continue
            //读取jar中的输入流
            val inputStream = jarFile.getInputStream(zipEntry)

            tempOutputStream.putNextEntry(zipEntry)

            println("--------------当前文件为:$entryName-----------------")
            if (entryName.endsWith(".class") && !(entryName.startsWith("R\$")
                            || entryName.startsWith("R.") || "BuildConfig.class".equals(entryName))) {
                //执行插桩
                val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                val classWriter = ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
                classReader.accept(getClassVisitor(classWriter), ClassReader.EXPAND_FRAMES)
                tempOutputStream.write(classWriter.toByteArray())
            } else {
                tempOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            tempOutputStream.closeEntry()
        }
        tempOutputStream.close()
        jarFile.close()
        val dest = outputProvider.getContentLocation(jarInput.name,jarInput.contentTypes,jarInput.scopes,Format.JAR)
        FileUtils.copyFile(tempFile,dest)
        tempFile.delete()
    }

    private fun transformDirectory(directoryInput: DirectoryInput,dest: File) {
        if (dest.exists()) {
            FileUtils.forceDelete(dest)
        }
        FileUtils.forceMkdir(dest)
        val extensions = arrayOf("class")
        //递归地去获取该文件夹下面所有的文件
        val fileList = FileUtils.listFiles(directoryInput.file,extensions,true)
        val outputFilePath = dest.absolutePath
        val inputFilePath = directoryInput.file.absolutePath
        fileList.forEach { inputFile ->
            val outputFullPath = inputFile.absolutePath.replace(inputFilePath, outputFilePath)
            val outputFile = File(outputFullPath)
            if (inputFile.isDirectory) {
                transformDirectory(inputFile as DirectoryInput,outputFile)
            } else {
                //创建文件
                FileUtils.touch(outputFile)
                //单个单个地复制文件
                transformSingleFile(inputFile, outputFile)
            }
        }
    }

    private fun transformSingleFile(inputFile: File, destFile: File) {
        println("扫描单个文件")
        traceFile(inputFile, destFile)
    }

    private fun traceFile(inputFile:File, outputFile:File) {
        if (isNeedTraceClass(inputFile)) {
            println("${inputFile.name} ---- 需要插桩 ----")
            val inputStream = FileInputStream(inputFile)
            val outputStream = FileOutputStream(outputFile)

            val classReader = ClassReader(inputStream)
            val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
            classReader.accept(getClassVisitor(classWriter), ClassReader.EXPAND_FRAMES)
            outputStream.write(classWriter.toByteArray())

            inputStream.close()
            outputStream.close()
        }
    }

    private fun isNeedTraceClass(inputFile: File):Boolean {
        val name = inputFile.name
        val result = name.endsWith(".class") &&
                !(name.startsWith("R$") || name.startsWith("R.") || "BuildConfig.class".equals(name))
        println("输入文件为：$name,isNeedTraceClass: $result")
        return result
    }
}