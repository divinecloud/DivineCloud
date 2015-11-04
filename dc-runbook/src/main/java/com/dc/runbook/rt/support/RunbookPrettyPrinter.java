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

package com.dc.runbook.rt.support;

import java.util.Arrays;

import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.rt.domain.TransformedRunBook;
import com.dc.runbook.rt.domain.item.DtRunbookCommand;
import com.dc.runbook.rt.domain.item.DtRunbookItem;
import com.dc.runbook.rt.domain.item.DtRunbookScript;

public class RunbookPrettyPrinter {
	public static String get(TransformedRunBook runbook) {
		StringBuilder sb = new StringBuilder();
		sb.append("Name : " + runbook.getName()).append("\n");
		int count = 0;
		for (DtRunbookStep step : runbook.getSteps()) {
			count++;
			sb.append("Step : " + count).append("\n");

			DtRunbookItem item = step.getItem();

			if (item instanceof DtRunbookCommand) {
				DtRunbookCommand command = (DtRunbookCommand) item;
				sb.append("	ItemId		: " + command.getId()).append("\n");
				sb.append("	Commmand	: " + command.getCommand()).append("\n");
			} else if (item instanceof DtRunbookScript) {
				DtRunbookScript script = (DtRunbookScript) item;
				sb.append("	ItemId		: " + script.getId()).append("\n");
				sb.append("	Script		: " + script.getScript()).append("\n");
				sb.append("	Args		: " + Arrays.toString(script.getArgs().toArray())).append("\n");
			}
		}
		return sb.toString();
	}
}
