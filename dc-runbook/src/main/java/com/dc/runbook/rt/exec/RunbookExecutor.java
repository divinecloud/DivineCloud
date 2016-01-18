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

package com.dc.runbook.rt.exec;

import com.dc.DcException;
import com.dc.node.NodeDetails;
import com.dc.runbook.rt.CredentialsProvider;
import com.dc.runbook.rt.domain.DtProperty;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.rt.domain.item.DtRunbookItem;
import com.dc.runbook.rt.exec.support.GeneratedPropertiesHandler;
import com.dc.runbook.rt.exec.support.NodeInfo;
import com.dc.runbook.rt.exec.support.RunBookWorker;
import com.dc.runbook.rt.node.OnDemandNodesProvider;
import com.dc.support.KeyValuePair;
import com.dc.util.condition.ConditionalBarrier;
import com.dc.util.string.EnhancedStringBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RunbookExecutor {
	private RunbookContext	                           context;
	private RunBookExecutionHandler	                   runBookExecutionHandler;

	private ConditionalBarrier<String>	               conditionalBarrier;
	private static final int	                       DIGIT_BASE	= 100;
	private static final String	                       PAUSE_KEY	= "PAUSE_RUN_BOOK_EXECUTION_CONDITIONAL_BARRIER";

	private volatile Boolean	                       cancelExecution;
	private volatile boolean	                       pauseExecution;
	private AtomicReference<List<RunBookWorker>>	workerThreadsReference;
	private List<KeyValuePair<String, String>>	       sharedPropertiesList;

	private AtomicReference<DtRunbookStep>	           currentStep;
	private GeneratedPropertiesHandler	               runbookGeneratedPropertiesHandler;

    private OnDemandNodesProvider onDemandNodesProvider;
    private CredentialsProvider credentialsProvider;
    private RunbookCallbackAdapter callback;

    public RunbookExecutor(RunbookContext context) {
        this.context = context;
        cancelExecution = false;
        conditionalBarrier = new ConditionalBarrier<>();
        workerThreadsReference = new AtomicReference<>();
        sharedPropertiesList = new Vector<>();
        currentStep = new AtomicReference<>();
        runbookGeneratedPropertiesHandler = new GeneratedPropertiesHandler();
        onDemandNodesProvider = new OnDemandNodesProvider(credentialsProvider);
        callback = new RunbookCallbackAdapter(context.getCallback(), context.getExecutionId());
        callback.registerOnDemandNodesCleaner(onDemandNodesProvider);
        callback.registerDoneNotifier(context.getNotifier());
    }

	public RunbookExecutor(RunbookContext context, CredentialsProvider credentialsProvider) {
		this.context = context;
        this.credentialsProvider = credentialsProvider;
        cancelExecution = false;
        conditionalBarrier = new ConditionalBarrier<>();
		workerThreadsReference = new AtomicReference<>();
		sharedPropertiesList = new Vector<>();
		currentStep = new AtomicReference<>();
		runbookGeneratedPropertiesHandler = new GeneratedPropertiesHandler();
        onDemandNodesProvider = new OnDemandNodesProvider(credentialsProvider);
        callback = new RunbookCallbackAdapter(context.getCallback(), context.getExecutionId());
        callback.registerOnDemandNodesCleaner(onDemandNodesProvider);
        callback.registerDoneNotifier(context.getNotifier());

	}

	public synchronized void execute() {
		if (runBookExecutionHandler != null) {
			throw new RuntimeException("Execution already in progress");
		}
		runBookExecutionHandler = new RunBookExecutionHandler();
		runBookExecutionHandler.start();
	}

	public void cancel() {
		cancelExecution = true;
		pauseExecution = false;
		callback.markCancelled();
		List<RunBookWorker> workerThreads = workerThreadsReference.get();
		if (workerThreads != null) {
			for (RunBookWorker thread : workerThreads) {
				thread.cancelExecution();
			}
		}
	}

	public void pause() {
		pauseExecution = true;
	}

	public void resume() {
		pauseExecution = false;
		conditionalBarrier.release(PAUSE_KEY);
	}

	public void feedAnswer(String answer) {
		DtRunbookStep step = currentStep.get();
		List<String> displayIds = context.getItemNodesMap().get(step.getItem().getItemId());
		for (String displayId : displayIds) {
			context.getSshClients().get(displayId).feedAnswer(context.getExecutionId(), answer);
		}
	}

	class RunBookExecutionHandler extends Thread {

		public void run() {
			try {
                callback.started();
				List<DtRunbookStep> steps = context.getRunbook().getSteps();
				DtRunbookStep previousStep = null;
				int stepCount = 1;
				addToSharedProperties();
                addTransientNodesIfPresent();
				for (DtRunbookStep step : steps) {
					if (context.getRunbook().getGeneratedPropertiesFilePath() != null) {
						if (step.getGeneratedPropertiesFilePath() == null || "".equals(step.getGeneratedPropertiesFilePath().trim())) {
							step.setGeneratedPropertiesFilePath(context.getRunbook().getGeneratedPropertiesFilePath());
						}
					}
					currentStep.getAndSet(step);
					if (context.getRunbook().isUtilityMode()) {
						if (stepCount == context.getStartingStep()) {
							executeRunbookStep(step);
						}
					} else {
						if (stepCount >= context.getStartingStep()) {
							if (previousStep != null && !previousStep.isContinueOnError()) {
								if (callback.didLatestStepFail()) {
									break;
								}
							}

							if (step.isPauseHere()) {
								pause();
							}
							if (pauseExecution) {
								callback.pausedItem(step.getItem());
								conditionalBarrier.block(PAUSE_KEY);
								callback.resumedItem(step.getItem());
							}
							executeRunbookStep(step);
							previousStep = step;
						}
					}
					stepCount++;
				}
                String message;
                if(callback.didLatestStepFail()) {
                    message = replaceTokens(context.getRunbook().getFailedMessage());
                }
                else {
                    message = replaceTokens(context.getRunbook().getSuccessMessage());
                }
				callback.done(message);
			} catch (Throwable t) {
                if(t instanceof DcException) {
                    callback.done(t.getMessage(), (DcException)t);
                }
                else {
                    callback.done(t.getMessage(), new Exception(t.getMessage(), t));
                }
			}
		}

        private String replaceTokens(String text) {
            String result = text;
            if(text != null && text.trim().length() > 0) {
                EnhancedStringBuilder builder = new EnhancedStringBuilder(new StringBuilder(text));
                List<KeyValuePair<String, String>> props = runbookGeneratedPropertiesHandler.retrieveGeneratedProperties();
                if (props != null) {
                    for (KeyValuePair<String, String> pair : props) {
                        builder.replaceAll(pair.getKey(), pair.getValue());
                    }
                }
                result = builder.toString();
            }
            return result;
        }

        private void addTransientNodesIfPresent() {
            List<NodeDetails> transientNodes = context.getRunbook().getTransientNodes();
            if(transientNodes != null && transientNodes.size() > 0) {
                onDemandNodesProvider.addTempNode(transientNodes);
            }
        }

        private void executeRunbookStep(DtRunbookStep step) throws InterruptedException {
			if (!cancelExecution && !step.isSkip()) {
				GeneratedPropertiesHandler generatedPropertiesHandler = new GeneratedPropertiesHandler();
				callback.executingItem(step.getItem());
				List<String> list = context.getItemNodesMap().get(step.getItem().getItemId());
                List<NodeInfo> convertedList = convert(list, false);
                List<NodeInfo> tempNodes = retrieveTempNodes(step.getDynamicNodeTags());
                if(tempNodes.size() > 0) {
                    convertedList.addAll(tempNodes);
                }
                int totalSize = convertedList.size();
				int digitBaseCount = totalSize / DIGIT_BASE;
				int index = 0;
				for (int i = 0; i < digitBaseCount; i++) {
					if (!cancelExecution) {
						executeInBatch(step, step.getItem(), convertedList, DIGIT_BASE, index, index + DIGIT_BASE, generatedPropertiesHandler);
						index += DIGIT_BASE;
					}
				}
				if (!cancelExecution) {
					if (convertedList.size() > (digitBaseCount * DIGIT_BASE)) {
						int remainingCount = convertedList.size() - (digitBaseCount * DIGIT_BASE);
						executeInBatch(step, step.getItem(), convertedList, remainingCount, index, index + remainingCount, generatedPropertiesHandler);
					}
				}

                runbookGeneratedPropertiesHandler.addGeneratedRunBookProperties(generatedPropertiesHandler);
				callback.completedItem(step.getItem());
			} else {
				callback.skippingItem(step.getItem());
			}
		}

        private List<NodeInfo> convert(List<String> list, Boolean flag) {
            List<NodeInfo> result = new ArrayList<>();
            if(list != null) {
                for (String item : list) {
                    NodeInfo info = new NodeInfo(item, null, flag);
                    result.add(info);
                }
            }
            return result;
        }

        private List<NodeInfo> retrieveTempNodes(List<String> dynamicTags) {
            List<NodeInfo> result = new ArrayList<>();
            Map<String, String> dynamicNodesMap = new HashMap<>();
            if(dynamicTags != null) {
                for(String dynamicTag : dynamicTags) {
                    Map<String, NodeDetails> nodeDetailsMap = onDemandNodesProvider.getNodes(dynamicTag);
                    if(nodeDetailsMap != null) {
                        for(String uniqueId : nodeDetailsMap.keySet()) {
                            dynamicNodesMap.put(uniqueId, dynamicTag);
                        }
                    }
                }
            }
            for(Map.Entry<String, String> entry : dynamicNodesMap.entrySet()) {
                NodeInfo info = new NodeInfo(entry.getKey(), entry.getValue(), true);
                result.add(info);
            }

            return result;
        }

		private void executeInBatch(DtRunbookStep step, DtRunbookItem item, List<NodeInfo> list, int batchCount, int startIndex, int endIndex, GeneratedPropertiesHandler generatedPropertiesHandler) throws InterruptedException {
			List<RunBookWorker> threads = new ArrayList<>(batchCount);
			workerThreadsReference.set(threads);
			for (int h = startIndex; h < endIndex; h++) {
				RunBookWorker worker = new RunBookWorker(list.get(h).getUniqueId(), list.get(h).isOnDemand(), list.get(h).getDynamicTag(), item, step,
                        generatedPropertiesHandler, cancelExecution, context, callback, onDemandNodesProvider, conditionalBarrier, runbookGeneratedPropertiesHandler, sharedPropertiesList);
				threads.add(worker);
				worker.start();
			}
			for (Thread thread : threads) {
				thread.join();
			}
			workerThreadsReference.set(null);
		}

        private void addToSharedProperties() {
            List<DtProperty> list = context.getRunbook().getProperties();
            if (list != null && list.size() > 0) {
                for (DtProperty property : list) {
                    KeyValuePair<String, String> pair = new KeyValuePair<>(property.getStepProperty().getName(), property.getValue());
                    sharedPropertiesList.add(pair);
                }
            }
        }

	}
}
