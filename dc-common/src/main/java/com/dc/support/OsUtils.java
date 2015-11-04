/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.dc.support;

public class OsUtils {
	private static String	OS	= null;

	private static String getOsName() {
		if (OS == null) {
			OS = System.getProperty("os.name").toLowerCase();
		}
		return OS;
	}

	public static boolean isWindows() {
		return (getOsName().indexOf("win") >= 0);
	}
 
	public static boolean isMac() {
		return (getOsName().indexOf("mac") >= 0);
	}
 
	public static boolean isUnix() {
		return (getOsName().indexOf("nix") >= 0 || getOsName().indexOf("nux") >= 0 || getOsName().indexOf("aix") > 0 );
	}
 
	public static boolean isSolaris() {
		return (getOsName().indexOf("sunos") >= 0);
	}
}


