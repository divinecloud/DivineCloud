package com.dc.runbook.rt.exec;

import com.dc.DcException;
import com.dc.runbook.dt.domain.Property;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.dt.domain.RunBookStep;
import com.dc.runbook.dt.domain.item.*;
import com.dc.runbook.dt.locator.RunBookLocator;
import com.dc.runbook.rt.domain.DtProperty;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.rt.domain.TransformedRunBook;
import com.dc.runbook.rt.domain.item.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DtRunBookConverter {

	public static TransformedRunBook convert(RunBook runBook) {
		List<DtRunbookStep> steps = new ArrayList<>();
		Map<Integer, RunBook> runBookMap = new HashMap<>();
		TransformedRunBook transformedRunBook = new TransformedRunBook();
		transformedRunBook.setName(runBook.getName());
		transformedRunBook.setUtilityMode(runBook.isUtilityMode());
		transformedRunBook.setGeneratedPropertiesFilePath(runBook.getGeneratedPropertiesFilePath());
		transformedRunBook.setReleaseName(runBook.getReleaseName());
		transformedRunBook.setReleaseVersion(runBook.getReleaseVersion());
		transformedRunBook.setRunBooksMap(runBookMap);
		transformedRunBook.setSteps(steps);
		applyProperties(transformedRunBook, runBook);
		int runBookId = 0;
		AtomicInteger sequenceId = new AtomicInteger();
		convertRunBook(runBook, transformedRunBook, runBookId, sequenceId, null);
		return transformedRunBook;
	}

	private static void applyProperties(TransformedRunBook transformedRunBook, RunBook runBook) {
		if (runBook.getProperties() != null) {
			String runBookName = (transformedRunBook.getName().equals(runBook.getName())) ? "" : runBook.getName();
			for (Property property : runBook.getProperties()) {
				DtProperty dtProperty = new DtProperty(property, null, runBookName);
				transformedRunBook.getProperties().add(dtProperty);
			}
		}
	}

	public static int convertRunBook(RunBook runBook, TransformedRunBook transformedRunBook, int runBookId, AtomicInteger sequenceId, List<Integer> parentRunBooksId) {
		runBookId++;
		transformedRunBook.getRunBooksMap().put(runBookId, runBook);
		if (parentRunBooksId == null) {
			parentRunBooksId = new ArrayList<>();
		}
		parentRunBooksId.add(runBookId);
		int itemId = 0;
		int myRunBookId = runBookId;
		for (RunBookStep step : runBook.getSteps()) {
			if (step.getItem() != null && step.getItemType() != null) {
				if (step.getItemType() == ItemType.RunBook) {
					RunBook locatedRunBook = locateRunBook(step);
					applyProperties(transformedRunBook, locatedRunBook);
					if (locatedRunBook.isGroupSteps()) {
						convertToStepGroupItem(locatedRunBook, step, transformedRunBook, ++runBookId, ++itemId, sequenceId, parentRunBooksId);
					} else {
						if (!locatedRunBook.isUtilityMode()) {
							runBookId = convertRunBook(locatedRunBook, transformedRunBook, runBookId, sequenceId, parentRunBooksId);
						} else {
							String stepId = ((RunBookReferenceItem) step.getItem()).getStepId();
							RunBookStep locatedRunBookStep = locatedRunBook.findById(stepId);

							DtRunbookItem dtRunbookItem = convert(locatedRunBookStep.getItem(), locatedRunBookStep.getItemType(), ++runBookId, 1);
							DtRunbookStep dtRunbookStep = new DtRunbookStep();
							dtRunbookStep.setId(locatedRunBookStep.getId());
							dtRunbookStep.setName(locatedRunBookStep.getName());
							dtRunbookStep.setNodeSet(locatedRunBookStep.getNodeSet());
							dtRunbookStep.setGeneratedPropertiesFilePath(locatedRunBookStep.getGeneratedPropertiesFilePath());
							dtRunbookStep.setNodesImportFilePath(locatedRunBookStep.getNodesImportFilePath());
							dtRunbookStep.setFileIncludes(locatedRunBookStep.getFileIncludes());
							dtRunbookStep.setFileIncludesDestinationFolder(locatedRunBookStep.getFileIncludesDestinationFolder());
							dtRunbookStep.setItem(dtRunbookItem);
							dtRunbookStep.setAnswersRequired(locatedRunBookStep.isAnswersRequired());
							dtRunbookStep.setReplaceProperties(locatedRunBookStep.isReplaceProperties());
							dtRunbookStep.setDynamicNodeTags(locatedRunBookStep.getDynamicNodeTags());
							dtRunbookStep.setParentRunbooksId(parentRunBooksId);
							if (locatedRunBookStep.getProperties() != null && locatedRunBookStep.getProperties().size() > 0) {
								dtRunbookStep.setProperties(new ArrayList<>());
								for (Property stepProperty : locatedRunBookStep.getProperties()) {
									dtRunbookStep.getProperties().add(new DtProperty(stepProperty, null, locatedRunBook.getName()));
								}
							}
							int seqId = sequenceId.incrementAndGet();
							dtRunbookStep.setSequenceId(seqId);
							transformedRunBook.getSteps().add(dtRunbookStep);
						}
					}

				} else {
					itemId++;
					DtRunbookItem dtRunbookItem = convert(step.getItem(), step.getItemType(), myRunBookId, itemId);
					DtRunbookStep dtRunbookStep = new DtRunbookStep();
					dtRunbookStep.setId(step.getId());
					dtRunbookStep.setName(step.getName());
					dtRunbookStep.setNodeSet(step.getNodeSet());
					dtRunbookStep.setAnswersRequired(step.isAnswersRequired());
					dtRunbookStep.setReplaceProperties(step.isReplaceProperties());
					dtRunbookStep.setGeneratedPropertiesFilePath(step.getGeneratedPropertiesFilePath());
                    dtRunbookStep.setNodesImportFilePath(step.getNodesImportFilePath());
					dtRunbookStep.setFileIncludes(step.getFileIncludes());
					dtRunbookStep.setFileIncludesDestinationFolder(step.getFileIncludesDestinationFolder());
					dtRunbookStep.setDynamicNodeTags(step.getDynamicNodeTags());
					dtRunbookStep.setItem(dtRunbookItem);
					dtRunbookStep.setParentRunbooksId(parentRunBooksId);
					if (step.getProperties() != null && step.getProperties().size() > 0) {
						dtRunbookStep.setProperties(new ArrayList<>());
						for (Property stepProperty : step.getProperties()) {
							dtRunbookStep.getProperties().add(new DtProperty(stepProperty, null, ""));
						}
					}
					int seqId = sequenceId.incrementAndGet();
					dtRunbookStep.setSequenceId(seqId);
					transformedRunBook.getSteps().add(dtRunbookStep);
				}
			} else {
				// step with null item or item type present, skip it.
			}
		}

		return myRunBookId;
	}

	private static void convertToStepGroupItem(RunBook locatedRunBook, RunBookStep currentStep, TransformedRunBook transformedRunBook, int runBookId, int itemId, AtomicInteger sequenceId, List<Integer> parentRunBooksId) {
		DtRunbookStep dtRunbookStep = new DtRunbookStep();
		int localItemId = 0;
		List<DtRunbookItem> itemsList = new ArrayList<>();
		dtRunbookStep.setProperties(new ArrayList<>());
		for (RunBookStep step : locatedRunBook.getSteps()) {
			if (step.getItem() != null && step.getItemType() != null) {

				localItemId++;
				DtRunbookItem dtRunbookItem = convert(step.getItem(), step.getItemType(), runBookId, localItemId);
				if (step.getProperties() != null && step.getProperties().size() > 0) {
					for (Property stepProperty : step.getProperties()) {
						dtRunbookStep.getProperties().add(new DtProperty(stepProperty, null, ""));
					}
				}
				itemsList.add(dtRunbookItem);
			}
			// else step with null item or item type present, skip it.
		}

		DtRunbookStepGroupItem stepGroupItem = new DtRunbookStepGroupItem(runBookId, itemId, itemsList);

		dtRunbookStep.setId(currentStep.getId());
		dtRunbookStep.setName(locatedRunBook.getStepGroupsName());
		dtRunbookStep.setNodeSet(currentStep.getNodeSet());
		dtRunbookStep.setGeneratedPropertiesFilePath(currentStep.getGeneratedPropertiesFilePath());
		dtRunbookStep.setNodesImportFilePath(currentStep.getNodesImportFilePath());
		dtRunbookStep.setFileIncludes(currentStep.getFileIncludes());
		dtRunbookStep.setFileIncludesDestinationFolder(currentStep.getFileIncludesDestinationFolder());
		dtRunbookStep.setItem(stepGroupItem);
		dtRunbookStep.setReplaceProperties(currentStep.isReplaceProperties());
		dtRunbookStep.setDynamicNodeTags(currentStep.getDynamicNodeTags());
		dtRunbookStep.setParentRunbooksId(parentRunBooksId);
		int seqId = sequenceId.incrementAndGet();
		dtRunbookStep.setSequenceId(seqId);
		transformedRunBook.getSteps().add(dtRunbookStep);
	}

	private static RunBook locateRunBook(RunBookStep step) {
		RunBookReferenceItem runBookReference = (RunBookReferenceItem) step.getItem();
		return RunBookLocator.locate(runBookReference.getUri(), runBookReference.getLocation());
	}

	private static DtRunbookItem convert(RunBookItem item, ItemType itemType, int runBookId, int itemId) {
		DtRunbookItem dtRunbookItem;

		switch (itemType) {
			case Command:
				dtRunbookItem = new DtRunbookCommand(runBookId, itemId, ((CommandItem) item).getCommand(), item.getAnswers(), item.isReboot());
				break;
			case MultiCommand:
				dtRunbookItem = new DtRunbookMultiCommand(runBookId, itemId, ((MultiCommandItem) item).getCommands(), item.getAnswers(), item.isReboot());
				break;
			case File:
				DownloadPrependType downloadPrependType = ((FileItem) item).getPrependType();
				if (downloadPrependType == null) {
					downloadPrependType = DownloadPrependType.None;
				}
				dtRunbookItem = new DtRunbookFile(runBookId, itemId, ((FileItem) item).getSource(), ((FileItem) item).getDestination(), ((FileItem) item).getTransferType(), downloadPrependType);
				break;
			case Script:
				ScriptItem scriptItem = (ScriptItem) item;
				dtRunbookItem = new DtRunbookScript(runBookId, itemId, scriptItem.getScript(), splitArguments(scriptItem.getArguments()), item.getAnswers(), scriptItem.getLanguage(), scriptItem.getInvokingProgram(), scriptItem.getFileName(), item.isReboot());
				break;
			case FileScript:
				FileScriptItem fileScriptItem = (FileScriptItem) item;
				dtRunbookItem = new DtRunbookFileScript(runBookId, itemId, fileScriptItem.getScriptPath(), splitArguments(fileScriptItem.getArguments()), fileScriptItem.getFileName(), fileScriptItem.getLanguage(), fileScriptItem.getInvokingProgram(), fileScriptItem.isRelative(), item.getAnswers(), item.isReboot());
				break;
			case PropertiesTransfer:
				PropertiesTransferItem propertiesTransferItem = (PropertiesTransferItem) item;
				dtRunbookItem = new DtRunbookPropertiesTransfer(runBookId, itemId, propertiesTransferItem.getPath());
				break;
			case MultiOsCommand:
				MultiOsCommandItem multiOsCommandItem = (MultiOsCommandItem) item;
				dtRunbookItem = new DtRunbookMultiOsCommand(runBookId, itemId, multiOsCommandItem.getCommandsList(), item.getAnswers(), item.isReboot());
				break;
			case MultiScriptCommand:
				MultiScriptCommandItem multiScriptCommandItem = (MultiScriptCommandItem) item;
				dtRunbookItem = new DtRunbookMultiScriptCommand(runBookId, itemId, multiScriptCommandItem.getCommands(), item.getAnswers(), item.isReboot());
				break;
			case TextSave:
				TextSaveItem textSaveItem = (TextSaveItem) item;
				dtRunbookItem = new DtRunbookTextSaveItem(runBookId, itemId, item.getAnswers(), item.isReboot(), textSaveItem.getFileName(), textSaveItem.getText(), textSaveItem.getMode(), textSaveItem.isBackup());
				break;
			case TextReplace:
				TextReplaceItem textReplaceItem = (TextReplaceItem) item;
				dtRunbookItem = new DtRunbookTextReplaceItem(runBookId, itemId, item.getAnswers(), item.isReboot(), textReplaceItem.getFileName(), textReplaceItem.getPropertiesList(), textReplaceItem.isBackup());
				break;
			default:
				throw new DcException("Runbook Item Type " + itemType + " not supported for now");

		}

		return dtRunbookItem;
	}

	private static List<String> splitArguments(String arguments) {
		List<String> result = null;

		if (arguments != null && !arguments.trim().equals("")) {
			String[] args = arguments.split(" ");
			if (args != null && args.length > 0) {
				result = new ArrayList<>();
				for (String arg : args) {
					result.add(arg);
				}
			}
		}
		return result;
	}
}
