/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.build.GeneratedFile
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.IncrementalCompilation
import org.jetbrains.kotlin.config.Services
import java.io.File


fun makeJsIncrementally(
        cachesDir: File,
        sourceRoots: Iterable<File>,
        args: K2JSCompilerArguments,
        messageCollector: MessageCollector = MessageCollector.NONE,
        reporter: ICReporter = EmptyICReporter
) {
    val versions = commonCacheVersions(cachesDir) + jsCacheVersion(cachesDir)
    val allKotlinFiles = sourceRoots.asSequence().flatMap { it.walk() }
            .filter { it.isFile && it.extension.equals("kt", ignoreCase = true) }.toList()

    withJsIC {
        val compiler = IncrementalJsCompilerRunner(cachesDir, versions, reporter)
        compiler.compile(allKotlinFiles, args, messageCollector) {
            it.inputsCache.sourceSnapshotMap.compareAndUpdate(allKotlinFiles)
        }
    }
}

inline fun <R> withJsIC(fn: ()->R): R {
    val isJsEnabledBackup = IncrementalCompilation.isEnabledForJs()
    IncrementalCompilation.setIsEnabledForJs(true)

    try {
        return withIC { fn() }
    }
    finally {
        IncrementalCompilation.setIsEnabledForJs(isJsEnabledBackup)
    }
}

class IncrementalJsCompilerRunner(
        workingDir: File,
        cacheVersions: List<CacheVersion>,
        reporter: ICReporter
) : IncrementalCompilerRunner<K2JSCompilerArguments, IncrementalJsCachesManager>(
        workingDir,
        "caches-js",
        cacheVersions,
        reporter,
        artifactChangesProvider = null,
        changesRegistry = null
) {
    override fun isICEnabled(): Boolean =
        IncrementalCompilation.isEnabled() && IncrementalCompilation.isEnabledForJs()

    override fun createCacheManager(args: K2JSCompilerArguments): IncrementalJsCachesManager =
        IncrementalJsCachesManager(cacheDirectory, reporter)

    override fun destionationDir(args: K2JSCompilerArguments): File {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun compareAndUpdateCache(caches: IncrementalJsCachesManager, generatedFiles: List<GeneratedFile>): CompilationResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun runCompiler(sourcesToCompile: Set<File>, args: K2JSCompilerArguments, caches: IncrementalJsCachesManager, services: Services.Builder, messageCollector: MessageCollector): ExitCode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}