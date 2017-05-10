/*
 * Copyright (C) 2017 lqcandqq13 (https://github.com/lqcandqq13).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monitor.plugin.utils

import org.apache.tools.ant.taskdefs.condition.Os

class ClassFilterUtils {

    public static boolean skipThisClassForJar(String entryName) {
        if (!entryName.endsWith(".class"))
            return true
        if (entryName.contains("/R\$") || entryName.endsWith("/R.class") || entryName.endsWith("/BuildConfig.class"))
            return true

        return false
    }

    public static boolean skipThisClassForFile(String filePath) {
        if (!filePath.endsWith(".class"))
            return true
        if (filePath.contains("${File.separator}R\$") || filePath.endsWith("${File.separator}R.class") ||
                filePath.endsWith("${File.separator}BuildConfig.class"))
            return true

        return false
    }

    public static Set<String> formatPath(Collection<String> paths) {
        Set<String> theNew = new HashSet<>()
        for (String path : paths) {
            theNew.add(path.replaceAll("\\.", "/"))
        }
        return theNew
    }

    public static boolean isExcluded(String path, Collection<String> excludePackage, Collection<String> excludeClass) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            path = path.replaceAll("\\\\", "/");
        }

        for (String exclude : excludeClass) {
            if (path == exclude) {
                return true;
            }
        }
        for (String exclude : excludePackage) {
            if (path.contains(exclude)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isIncluded(String path, Collection<String> includePackage) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            path = path.replaceAll("\\\\", "/");
        }

        if (includePackage.size() == 0) {
            return true
        }

        for (String include : includePackage) {
            if (path.contains(include)) {
                return true;
            }
        }

        return false;
    }
}
