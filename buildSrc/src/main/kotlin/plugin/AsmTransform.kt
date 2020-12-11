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
        private const val TAG = "BaseTransform"
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

    abstract fun getClassVisitor(classWriter: ClassWriter): ClassVisitor

    private val mWaitableExecutor by lazy {
        WaitableExecutor.useGlobalSharedThreadPool().apply {
            waitForTasksWithQuickFail<Any>(true)
        }
    }

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        println("-----------------$name start--------------------")
        val transformOutputProvider = transformInvocation.outputProvider
        val isIncremental = transformInvocation.isIncremental

        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                //处理jar
                mWaitableExecutor.execute {
                    processJarInput(jarInput, transformOutputProvider,isIncremental)
                }
            }

            input.directoryInputs.forEach { directoryInput ->
                //处理源码文件
                mWaitableExecutor.execute {
                    processDirectoryInput(directoryInput, transformOutputProvider,isIncremental)
                }
            }
        }
        mWaitableExecutor.waitForTasksWithQuickFail<Any>(true)
    }

    private fun processDirectoryInput(directoryInput: DirectoryInput, outputProvider: TransformOutputProvider,isIncremental: Boolean) {
        val directory = outputProvider.getContentLocation(directoryInput.name,directoryInput.contentTypes,directoryInput.scopes,Format.DIRECTORY)
        FileUtils.forceMkdir(directory)
        if (isIncremental) {
            val srcPath = directoryInput.file.absolutePath
            val destPath = directory.absolutePath
            val fileStatusMap = directoryInput.changedFiles
            fileStatusMap.entries.forEach { changeFile ->
                val status = changeFile.value
                val inputFile = changeFile.key
                val destFilePath = inputFile.absolutePath.replace(srcPath,destPath)
                val destFile = File(destFilePath)
                when (status) {
                    Status.NOTCHANGED -> {

                    }
                    Status.CHANGED,Status.ADDED -> {
                        FileUtils.touch(destFile)
                        transformSingleFile(inputFile, destFile)
                        FileUtils.copyFile(inputFile,destFile)
                    }
                    Status.REMOVED -> {
                        if (destFile.exists()) {
                            FileUtils.forceDelete(destFile)
                        }
                    }
                }
            }
        } else {
            transformDirectory(directoryInput,directory)
        }
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
            println("inputFile name == ${inputFile.name}")
            val outputFullPath = inputFile.absolutePath.replace(inputFilePath, outputFilePath)
            val outputFile = File(outputFullPath)
            if (inputFile.isDirectory) {
                transformDirectory(inputFile as DirectoryInput,outputFile)
            } else {
                //创建文件
                FileUtils.touch(outputFile)
                //单个单个地复制文件
                transformSingleFile(inputFile, outputFile)
                FileUtils.copyFile(inputFile,outputFile)
            }
        }
        FileUtils.copyDirectory(directoryInput.file,dest)
    }

    private fun processJarInput(jarInput: JarInput, outputProvider: TransformOutputProvider,isIncremental: Boolean) {
        val dest = outputProvider.getContentLocation(jarInput.name,jarInput.contentTypes,jarInput.scopes,Format.JAR)
        if (isIncremental) {
            when(jarInput.status) {
                Status.NOTCHANGED -> {
                    return
                }

                Status.ADDED,Status.CHANGED -> {
                    FileUtils.copyFile(jarInput.file,dest)
                }

                Status.REMOVED -> {
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest)
                    }
                }
            }
        } else {
            FileUtils.copyFile(jarInput.file,dest)
        }
    }

    private fun transformSingleFile(inputFile: File, destFile: File) {
        println("扫描单个文件")
        if (isNeedTraceClass(destFile.name)) {
            traceFile(inputFile.inputStream(), FileOutputStream(destFile))
        }
    }

    private fun traceFile(inputStream: FileInputStream, outputStream:FileOutputStream) {

        val classReader = ClassReader(IOUtils.toByteArray(inputStream))
        val classWriter = ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
        classReader.accept(getClassVisitor(classWriter), ClassReader.EXPAND_FRAMES)
        outputStream.write(classWriter.toByteArray())

        inputStream.close()
        outputStream.close()
    }

    private fun isNeedTraceClass(name: String):Boolean {
        val result = name.endsWith(".class") &&
                !(name.startsWith("R$") || name.startsWith("R."))
        println("输入文件为：$name,isNeedTraceClass: $result")
        return result
    }
}