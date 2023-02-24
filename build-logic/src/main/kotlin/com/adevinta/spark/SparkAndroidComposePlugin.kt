/*
 * Copyright (c) 2023 Adevinta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adevinta.spark


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal class SparkAndroidComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.android")

            configureAndroidExtension {
                buildFeatures.compose = true
                composeOptions {
                    kotlinCompilerExtensionVersion = spark().versions.`androidx-compose-compiler`.toString()
                }
            }

            // Ignore [KaptGenerateStubsTask] types as they inherit arguments from standard tasks via
            // [KaptGenerateStubsTask.compileKotlinArgumentsContributor] and applying arguments to them could result in duplicates.
            target.tasks.withType<KotlinCompile>().matching { it !is KaptGenerateStubsTask }.configureEach {
                compilerOptions.freeCompilerArgs.addAll(
                    "-opt-in=com.adevinta.spark.InternalSparkApi",
                    "-opt-in=com.adevinta.spark.ExperimentalSparkApi",
                    "-opt-in=kotlin.RequiresOptIn",
                )
            }

            dependencies {
                add("implementation", platform(spark().libraries.`androidx-compose-bom`))
            }
        }
    }
}