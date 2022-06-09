/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.utils;

import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import org.lwjgl.system.macosx.LibC;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

/**
 * Adds some utilities to ensure that the JVM was started with the
 * {@code -XstartOnFirstThread} argument, which is required on macOS for LWJGL 3
 * to function.
 *
 * @author damios
 * @see <a href=
 *      "http://www.java-gaming.org/topics/starting-jvm-on-mac-with-xstartonfirstthread-programmatically/37697/view.html">Based
 *      on http://www.java-gaming.org/topics/-/37697/view.html</a>
 *
 */
public class StartOnFirstThreadHelper {

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    private StartOnFirstThreadHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Starts a new JVM if the application was started on macOS without the
     * {@code -XstartOnFirstThread} argument. Returns whether a new JVM was started
     * and thus no code should be executed.
     * <p>
     * <u>Usage:</u>
     *
     * <pre>
     * public static void main(String... args) {
     * 	if (StartOnFirstThreadHelper.startNewJvmIfRequired()) {
     * 		return; // don't execute any code
     * 	}
     * 	// the actual main method code
     * }
     * </pre>
     *
     * @param redirectOutput whether the output of the new JVM should be rerouted to
     *                       the new JVM, so it can be accessed in the same place;
     *                       keeps the old JVM running if enabled
     * @return whether a new JVM was started and thus no code should be executed in
     *         this one
     */
    public static boolean startNewJvmIfRequired(boolean redirectOutput) {
        if (!UIUtils.isMac) {
            return false;
        }

        long pid = LibC.getpid();

        // check whether -XstartOnFirstThread is enabled
        if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
            return false;
        }

        // check whether the JVM was previously restarted
        // avoids looping, but most certainly leads to a crash
        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println(
                    "There was a problem evaluating whether the JVM was started with the -XstartOnFirstThread argument.");
            return false;
        }

        // Restart the JVM with -XstartOnFirstThread
        ArrayList<String> jvmArgs = new ArrayList<String>();
        String separator = System.getProperty("file.separator");
        // TODO Java 9: ProcessHandle.current().info().command();
        String javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java";
        if (!(new File(javaExecPath)).exists()) {
            System.err.println(
                    "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the -XstartOnFirstThread argument manually!");
            return false;
        }
        jvmArgs.add(javaExecPath);
        jvmArgs.add("-XstartOnFirstThread");
        jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        jvmArgs.add("-cp");
        jvmArgs.add(System.getProperty("java.class.path"));
        jvmArgs.add(System.getenv("JAVA_MAIN_CLASS_" + pid));

        try {
            if (!redirectOutput) {
                ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
                processBuilder.start();
            } else {
                Process process = (new ProcessBuilder(jvmArgs)).redirectErrorStream(true).start();
                BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = processOutput.readLine()) != null) {
                    System.out.println(line);
                }

                process.waitFor();
            }
        } catch (Exception e) {
            System.err.println("There was a problem restarting the JVM");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Starts a new JVM if the application was started on macOS without the
     * {@code -XstartOnFirstThread} argument. Returns whether a new JVM was started
     * and thus no code should be executed. Redirects the output of the new JVM to
     * the old one.
     * <p>
     * <u>Usage:</u>
     *
     * <pre>
     * public static void main(String... args) {
     * 	if (StartOnFirstThreadHelper.startNewJvmIfRequired()) {
     * 		return; // don't execute any code
     * 	}
     * 	// the actual main method code
     * }
     * </pre>
     *
     * @return whether a new JVM was started and thus no code should be executed in
     *         this one
     */
    public static boolean startNewJvmIfRequired() {
        return startNewJvmIfRequired(true);
    }

    /**
     * Starts a new JVM if required; otherwise executes the main method code given
     * as Runnable. When used with lambdas, this is allows for less verbose code
     * than {@link #startNewJvmIfRequired()}:
     *
     * <pre>
     * public static void main(String... args) {
     * 	StartOnFirstThreadHelper.executeIfJVMValid(() -> {
     * 		// the actual main method code
     * 	});
     * }
     * </pre>
     *
     * @param mainMethodCode
     */
    public static void executeIfJVMValid(Runnable mainMethodCode) {
        if (startNewJvmIfRequired()) {
            return;
        }
        mainMethodCode.run();
    }

}
