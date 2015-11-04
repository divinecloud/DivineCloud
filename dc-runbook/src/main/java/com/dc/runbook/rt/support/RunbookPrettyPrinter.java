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
