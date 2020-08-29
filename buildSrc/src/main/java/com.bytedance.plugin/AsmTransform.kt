package com.bytedance.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import java.io.File

class AsmTransform: Transform() {
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
        return true
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

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
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
                    Status.NOTCHANGED -> return
                    Status.CHANGED,Status.ADDED -> {
                        FileUtils.touch(destFile)
                        FileUtils.copyFile(directory,destFile)
                    }
                    Status.REMOVED -> {
                        if (destFile.exists()) {
                            FileUtils.forceDelete(destFile)
                        }
                    }
                }
            }
        } else {
            FileUtils.copyDirectory(directoryInput.file,directory)
        }
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
}