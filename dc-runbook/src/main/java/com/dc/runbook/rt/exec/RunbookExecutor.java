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
import com.dc.LinuxOSType;
import com.dc.node.NodeDetails;
import com.dc.node.NodeDetailsParser;
import com.dc.runbook.dt.domain.item.DownloadPrependType;
import com.dc.runbook.dt.domain.item.TransferType;
import com.dc.runbook.rt.CredentialsProvider;
import com.dc.runbook.rt.domain.DtProperty;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.rt.domain.item.*;
import com.dc.runbook.rt.exec.support.*;
import com.dc.runbook.rt.node.OnDemandNodesProvider;
import com.dc.ssh.client.CommandExecutionCallback;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.*;
import com.dc.ssh.client.exec.cmd.script.MultiOSCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptAttributes;
import com.dc.ssh.client.exec.cmd.script.ScriptCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.sftp.SftpClient;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.support.FileSupport;
import com.dc.ssh.utils.SedSupport;
import com.dc.support.KeyValuePair;
import com.dc.util.condition.ConditionalBarrier;
import com.dc.util.string.EnhancedStringBuilder;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RunbookExecutor {
	private RunbookContext	                           context;
	private RunBookExecutionHandler	                   runBookExecutionHandler;

	private ConditionalBarrier<String>	               conditionalBarrier;
	private static final int	                       DIGIT_BASE	= 100;
	private static final String	                       PAUSE_KEY	= "PAUSE_RUN_BOOK_EXECUTION_CONDITIONAL_BARRIER";

	private volatile boolean	                       cancelExecution;
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
        conditionalBarrier = new ConditionalBarrier<>();
        workerThreadsReference = new AtomicReference<>();
        sharedPropertiesList = new Vector<>();
        currentStep = new AtomicReference<>();
        runbookGeneratedPropertiesHandler = new GeneratedPropertiesHandler();
        onDemandNodesProvider = new OnDemandNodesProvider();
        callback = new RunbookCallbackAdapter(context.getCallback(), context.getExecutionId());
        callback.registerOnDemandNodesCleaner(onDemandNodesProvider);
        callback.registerDoneNotifier(context.getNotifier());
    }

	public RunbookExecutor(RunbookContext context, CredentialsProvider credentialsProvider) {
		this.context = context;
		conditionalBarrier = new ConditionalBarrier<>();
		workerThreadsReference = new AtomicReference<>();
		sharedPropertiesList = new Vector<>();
		currentStep = new AtomicReference<>();
		runbookGeneratedPropertiesHandler = new GeneratedPropertiesHandler();
        onDemandNodesProvider = new OnDemandNodesProvider();
        callback = new RunbookCallbackAdapter(context.getCallback(), context.getExecutionId());
        callback.registerOnDemandNodesCleaner(onDemandNodesProvider);
        callback.registerDoneNotifier(context.getNotifier());
        this.credentialsProvider = credentialsProvider;
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

    public void addTempNode(List<NodeDetails> list) {
        if(list != null) {
            for(NodeDetails nodeDetails : list) {
                List<String> dynamicTags = nodeDetails.getDynamicTags();
                if(dynamicTags != null && dynamicTags.size() > 0) {
                    for(String dynamicTag : dynamicTags) {
                        NodeCredentials nodeCredentials = prepareNodeCredentials(nodeDetails);
                        onDemandNodesProvider.addNode(dynamicTag, nodeDetails, nodeCredentials);
                    }
                }
            }
        }
    }

    private NodeCredentials prepareNodeCredentials(NodeDetails nodeDetails) {
        return credentialsProvider.provide(nodeDetails);
    }

    private List<KeyValuePair<String, String>> convertProperties(List<DtProperty> properties) {
		List<KeyValuePair<String, String>> result = null;
		List<KeyValuePair<String, String>> sharedProperties = null;

		if (properties != null) {
			result = new ArrayList<>();
			sharedProperties = new ArrayList<>();
			for (DtProperty property : properties) {
				String value = property.getValue();
				if (value == null) {
					if (property.getStepProperty().getDefaultValue() != null && property.getStepProperty().getDefaultValue().trim().length() > 0) {
						value = property.getStepProperty().getDefaultValue();
					}
				}
				KeyValuePair<String, String> pair = new KeyValuePair<>(property.getStepProperty().getName(), value);
				result.add(pair);
			}
		}

		// Add any shared properties to the properties list, so for the given step appropriate replacement happens.
		if (sharedPropertiesList.size() > 0) {
			if (result == null) {
				result = new ArrayList<>();
			}
			for (KeyValuePair<String, String> sharedPropertiesPair : sharedPropertiesList) {
				result.add(sharedPropertiesPair);
			}
		}

		if (sharedProperties != null) {
			for (KeyValuePair<String, String> newPair : sharedProperties) {
				sharedPropertiesList.add(newPair);
			}
		}
		return result;
	}

	private void addGeneratedRunBookProperties(GeneratedPropertiesHandler stepGeneratedPropertiesHandler) {
		List<KeyValuePair<String, String>> stepGeneratedProperties = stepGeneratedPropertiesHandler.retrieveGeneratedProperties();
		if (stepGeneratedProperties != null && stepGeneratedProperties.size() > 0) {
			for (KeyValuePair<String, String> prop : stepGeneratedProperties) {
				runbookGeneratedPropertiesHandler.addProperty(prop.getKey(), prop.getValue());
			}
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
				callback.done();
			} catch (Throwable t) {
				t.printStackTrace();
				callback.done(new Exception(t));
			}
		}

        private void addTransientNodesIfPresent() {
            List<NodeDetails> transientNodes = context.getRunbook().getTransientNodes();
            if(transientNodes != null && transientNodes.size() > 0) {
                addTempNode(transientNodes);
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

				addGeneratedRunBookProperties(generatedPropertiesHandler);
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
				RunBookWorker worker = new RunBookWorker(list.get(h).uniqueId, list.get(h).onDemand, list.get(h).dynamicTag, item, step, generatedPropertiesHandler);
				threads.add(worker);
				worker.start();
			}
			for (Thread thread : threads) {
				thread.join();
			}
			workerThreadsReference.set(null);
		}
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

    class NodeInfo {
        String uniqueId;
        String dynamicTag;
        boolean onDemand;

        public NodeInfo(String uniqueId, String dynamicTag, boolean onDemand) {
            this.uniqueId = uniqueId;
            this.dynamicTag = dynamicTag;
            this.onDemand = onDemand;
        }
    }

	class RunBookWorker extends Thread {

		private String		               displayId;
		private DtRunbookItem		       item;
		private DtRunbookStep		       step;
		private String		               execId;
		private boolean		               fileTransferMode;
		private GeneratedPropertiesHandler	generatedPropertiesHandler;
        private boolean onDemand;
        private String dynamicTag;

		public RunBookWorker(String displayId, boolean onDemand, String dynamicTag, DtRunbookItem item, DtRunbookStep step, GeneratedPropertiesHandler generatedPropertiesHandler) {
			this.displayId = displayId;
			this.item = item;
            this.onDemand = onDemand;
            this.dynamicTag = dynamicTag;
			this.step = step;
			this.generatedPropertiesHandler = generatedPropertiesHandler;
		}

		public void cancelExecution() {
			SshClient sshClient = getSshClient();
			if (fileTransferMode) {
				sshClient.getSftpClient().cancel(execId);
			} else {
				sshClient.cancel(execId);
			}
		}

		private String generateNodeSpecificPath(String path, String nodeDisplayId) {
			StringBuilder sb = new StringBuilder();
			sb.append(path.substring(0, path.lastIndexOf("/")));
			sb.append("/").append(nodeDisplayId).append("/");
			new File(sb.toString()).mkdirs();
			sb.append(path.substring(path.lastIndexOf("/") + 1));
			return sb.toString();
		}

        private SshClient getSshClient() {
            SshClient result;
            if(!onDemand) {
                result = context.getSshClients().get(displayId);
            }
            else {
                synchronized (displayId.intern()) {
                    result = onDemandNodesProvider.getClient(displayId);
                }
            }
            return result;
        }

		public void run() {
			try {
				SshClient sshClient = getSshClient();

				if (!cancelExecution) {
					if (item instanceof DtRunbookFile) {
						DtRunbookFile fileItem = (DtRunbookFile) item;
						String source = fileItem.getSource();
						String destination = fileItem.getDestination();
						List<KeyValuePair<String, String>> props = convertProperties(step.getProperties());
						List<KeyValuePair<String, String>> runBookProperties = runbookGeneratedPropertiesHandler.retrieveGeneratedProperties();
						source = PropertiesReplacer.replace(source, props, runBookProperties, step.isReplaceProperties());
						destination = PropertiesReplacer.replace(destination, props, runBookProperties, step.isReplaceProperties());
						fileItem.setSource(source);
						fileItem.setDestination(destination);

						transferFile(sshClient);
					} else {
						SshCommandAttributes cmdAttributes = getCommandAttributes();
						if (context.getRunbook().isUtilityMode()) {
							handleGeneratedPropertiesForStep(step, generatedPropertiesHandler, sshClient, cmdAttributes);
							addGeneratedRunBookProperties(generatedPropertiesHandler);

						}

						if (item instanceof DtRunbookStepGroupItem) {

							executeStepGroupItems(sshClient, cmdAttributes);
						} else {
							RunbookSshCallback sshCallback = new RunbookSshCallback(callback, displayId, item.getItemId(), conditionalBarrier);
							executeNonStepGroupItems(item, sshClient, sshCallback, cmdAttributes);
						}
						if (!context.getRunbook().isUtilityMode()) {
							handleGeneratedPropertiesForStep(step, generatedPropertiesHandler, sshClient, cmdAttributes);
						}

                        handleDynamicNodesImportForStep(step, sshClient, cmdAttributes);
					}

				} else {
					callback.itemExecOnNodeDone(item.getItemId(), displayId, 999888, "Node execution cancelled"); // Code for indicating the cmd execution cancelled.
				}
			}
            catch(DcException e) {
                e.printStackTrace();
                callback.itemExecOnNodeDone(item.getItemId(), displayId, 999911, e.getMessage()); // Code for indicating the cmd execution failed.
            }
            catch (Throwable t) {
				t.printStackTrace();
				callback.itemExecOnNodeDone(item.getItemId(), displayId, 999999, "Server-side error occurred - " + t.getMessage()); // Code for indicating the cmd execution cancelled.
			}
		}

		private void handleGeneratedPropertiesForStep(DtRunbookStep step, GeneratedPropertiesHandler generatedPropertiesHandler, SshClient sshClient, SshCommandAttributes cmdAttributes) {
			String generatedPropsFilePath = step.getGeneratedPropertiesFilePath();
			if (generatedPropsFilePath != null && generatedPropsFilePath.trim().length() > 1) {
				String commandString = "cat " + generatedPropsFilePath;
				ExecutionDetails executionDetails = sshClient.execute(commandString);
				byte[] output = executionDetails.getOutput();
				String data = new String(output);
				List<KeyValuePair<String, String>> pairList = parseProperties(data);
				if (pairList.size() > 0) {
					for (KeyValuePair<String, String> pair : pairList) {
						generatedPropertiesHandler.addProperty(pair.getKey(), pair.getValue());
					}
				}
			}
		}

        private void handleDynamicNodesImportForStep(DtRunbookStep step, SshClient sshClient, SshCommandAttributes cmdAttributes) {
            String filePath = step.getNodesImportFilePath();
            if(filePath != null && filePath.trim().length() > 0) {
                String commandString = "cat " + filePath;
                ExecutionDetails executionDetails = sshClient.execute(commandString);
                if(executionDetails.isFailed()) {
                    throw new DcException("Unable to access content for file : " + filePath + " : " + new String(executionDetails.getOutput()));
                }
                byte[] output = executionDetails.getOutput();
                String data = new String(output);
                List<NodeDetails> list = NodeDetailsParser.parse(data);
                addTempNode(list);

            }

        }

        private List<KeyValuePair<String, String>> parseProperties(String data) {
			List<KeyValuePair<String, String>> result = new ArrayList<>();

			String[] lines = data.split("\n");

			if (lines.length > 0) {
				for (String line : lines) {
					int index = line.indexOf("=");
					if (index > 0) {
						String key = line.substring(0, index);
						String value = line.substring(index + 1);
						KeyValuePair<String, String> pair = new KeyValuePair<>();
						pair.setKey(key.trim());
						pair.setValue(value.trim());
						result.add(pair);
					}
				}
			}
			return result;
		}

		private void executeStepGroupItems(SshClient sshClient, SshCommandAttributes cmdAttributes) {
			RunBookStepGroupSshCallback sshCallback = new RunBookStepGroupSshCallback(callback, displayId, item.getItemId(), conditionalBarrier);
			DtRunbookStepGroupItem stepGroupItem = (DtRunbookStepGroupItem) item;
			List<DtRunbookItem> itemsList = stepGroupItem.getItemsList();
			boolean firstItemComplete = false;
			int statusCode = 0;
			try {
				for (DtRunbookItem subItem : itemsList) {
					if (firstItemComplete) {
						if (statusCode != 0) {
							break;
						}
					}
					executeNonStepGroupItems(subItem, sshClient, sshCallback, cmdAttributes);
					firstItemComplete = true;
					statusCode = sshCallback.getStatusCode();
				}
			} finally {
				callback.itemExecOnNodeDone(item.getItemId(), displayId, sshCallback.getStatusCode(), null);
			}

			// call the runbook level callback done method over here
		}

        private void transferIncludedFiles(SshCommandAttributes cmdAttributes) {
            String runbookPath = context.getRunbook().getRunBookPath();
            String includedFiles = step.getFileIncludes();
            String destinationFolder = step.getFileIncludesDestinationFolder();
            List<KeyValuePair<String, byte[]>> sourceFilePaths = new ArrayList<>();
            if(includedFiles != null && includedFiles.trim().length() > 0 && destinationFolder != null && destinationFolder.trim().length() > 0
                    && runbookPath != null && runbookPath.trim().length() > 0) {
                String [] filesPath = includedFiles.split("\n");
                if(filesPath.length > 0) {
                    for(String filePath : filesPath) {
                        filePath = runbookPath + "/" + filePath;
                        byte[] bytes = FileSupport.readFile(new File(filePath));
                        String destFilePath = destinationFolder + "/" + filePath.substring(filePath.lastIndexOf("/") + 1);
                        KeyValuePair<String, byte[]> pair = new KeyValuePair<>();
                        pair.setKey(destFilePath);
                        pair.setValue(bytes);
                        sourceFilePaths.add(pair);
                    }
                    SshClient client = getSshClient();
                    client.transferFiles(sourceFilePaths);

                }
            }
        }

		private void executeNonStepGroupItems(DtRunbookItem runbookItem, SshClient sshClient, CommandExecutionCallback sshCallback, SshCommandAttributes cmdAttributes) {
            SshCommand sshCommand;
            List<KeyValuePair<String, String>> props = convertProperties(step.getProperties());
			List<KeyValuePair<String, String>> runBookProperties = runbookGeneratedPropertiesHandler.retrieveGeneratedProperties();
            transferIncludedFiles(cmdAttributes);
			if (runbookItem instanceof DtRunbookScript) {
				DtRunbookScript scriptItem = (DtRunbookScript) runbookItem;
                String arguments = null;
                List<String> scriptArgs = scriptItem.getArgs();
                if(scriptArgs != null && scriptArgs.size() > 0) {
                    for(String arg : scriptArgs) {
                        if(arg != null && arg.trim().length() > 0) {
                            if(arguments != null && arguments.length() > 0) {
                                arguments = arguments + " " + arg;
                            }
                            else {
                                arguments = arg;
                            }

                        }
                    }
                }

				String finalScript = PropertiesReplacer.replace(scriptItem.getScript(), props, runBookProperties, step.isReplaceProperties());
				String finalArgs = PropertiesReplacer.replace(arguments, props, runBookProperties, step.isReplaceProperties());

				ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, finalArgs, scriptItem.getLanguage(), scriptItem.getInvokingProgram(), scriptItem.getFileName());
				finalScript = removeCarriageReturnChars(finalScript);

				sshCommand = new ScriptCommand(scriptAttributes, finalScript);
			} else if (runbookItem instanceof DtRunbookCommand) {
				DtRunbookCommand commandItem = (DtRunbookCommand) runbookItem;
				String cmd = PropertiesReplacer.replace(commandItem.getCommand(), props, runBookProperties, step.isReplaceProperties());

				sshCommand = new SingleSshCommand(cmdAttributes, cmd);
			} else if (runbookItem instanceof DtRunbookMultiCommand) {
				DtRunbookMultiCommand multiCommandItem = (DtRunbookMultiCommand) runbookItem;
				String script = multiCommandItem.commandsAsScript();
				script = PropertiesReplacer.replace(script, props, runBookProperties, step.isReplaceProperties());
				ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, null, ScriptLanguage.Shell, "sh", null);

				sshCommand = new ScriptCommand(scriptAttributes, script);
			} else if (runbookItem instanceof DtRunbookMultiOsCommand) {
				DtRunbookMultiOsCommand multiOsCommandItem = (DtRunbookMultiOsCommand) runbookItem;

				ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, null, ScriptLanguage.Shell, "sh", null);

				List<KeyValuePair<String, LinuxOSType>> pair = PropertiesReplacer.replace(multiOsCommandItem.getCommandsList(), props, runBookProperties, step.isReplaceProperties());
				sshCommand = new MultiOSCommand(scriptAttributes, pair);
			} else if (runbookItem instanceof DtRunbookMultiScriptCommand) {
				DtRunbookMultiScriptCommand multiScriptCommandItem = (DtRunbookMultiScriptCommand) runbookItem;
				String script = multiScriptCommandItem.commandsAsScript();
				script = PropertiesReplacer.replace(script, props, runBookProperties, step.isReplaceProperties());
				ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, null, ScriptLanguage.Shell, "sh", null);
				sshCommand = new ScriptCommand(scriptAttributes, script);
			} else if (runbookItem instanceof DtRunbookTextReplaceItem) {
				DtRunbookTextReplaceItem textReplaceItem = (DtRunbookTextReplaceItem) runbookItem;
				String sedScript = SedSupport.createSedScript(textReplaceItem.getPropertiesList(), textReplaceItem.getFileName());
				sedScript = PropertiesReplacer.replace(sedScript, props, runBookProperties, step.isReplaceProperties());
				ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, null, ScriptLanguage.Shell, "sh", null);
				// sedScript = removeCarriageReturnChars(sedScript);
				sshCommand = new ScriptCommand(scriptAttributes, sedScript);
			} else if (runbookItem instanceof DtRunbookTextSaveItem) {
				DtRunbookTextSaveItem textSaveItem = (DtRunbookTextSaveItem) runbookItem;
                String text = PropertiesReplacer.replace(textSaveItem.getText(), props, runBookProperties, step.isReplaceProperties());
                sshCommand = new FileTransferCommand(cmdAttributes, textSaveItem.getFileName(), text.getBytes());

			} else if (runbookItem instanceof DtRunbookFileScript) {
				DtRunbookFileScript fileScriptItem = (DtRunbookFileScript) runbookItem;
				String path = fileScriptItem.getScriptPath();
				String arguments = (fileScriptItem.getArgs() != null && fileScriptItem.getArgs().size() > 0) ? fileScriptItem.getArgs().get(0) : null;
				byte[] bytes = FileSupport.readFile(new File(path));
				String scriptContent = new String(bytes);
				String finalScript = PropertiesReplacer.replace(scriptContent, props, runBookProperties, step.isReplaceProperties());
				String finalArgs = PropertiesReplacer.replace(arguments, props, runBookProperties, step.isReplaceProperties());

				ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, finalArgs, ScriptLanguage.Shell, "sh", null);
				finalScript = removeCarriageReturnChars(finalScript);
				sshCommand = new ScriptCommand(scriptAttributes, finalScript);
			} else if (runbookItem instanceof DtRunbookPropertiesTransfer) {
				DtRunbookPropertiesTransfer propertiesTransferItem = (DtRunbookPropertiesTransfer) runbookItem;
                sshCommand = new FileTransferCommand(cmdAttributes, propertiesTransferItem.getPath(), context.getRunbook().getPropertiesJson().getBytes());

			} else {
				System.out.println("Invalid RunBookItem type specified");
				throw new DcException("Invalid RunBookItem type specified");

			}
			execId = sshClient.execute(sshCommand, sshCallback);
			conditionalBarrier.block(displayId + "_" + item.getItemId());
		}

		private String removeCarriageReturnChars(String script) {
			EnhancedStringBuilder builder = new EnhancedStringBuilder(new StringBuilder(script));
			builder.replaceAll("\r\n", "\n");
			return builder.toString();
		}

		private void transferFile(SshClient sshClient) {
			fileTransferMode = true;
			RunbookSftpCallback sftpCallback = new RunbookSftpCallback(callback, displayId, item.getItemId(), conditionalBarrier);
			try {
				DtRunbookFile fileItem = (DtRunbookFile) item;
				SftpClient sftpClient = sshClient.getSftpClient();
				if (fileItem.getTransferType() != null && fileItem.getTransferType() == TransferType.DOWNLOAD) {
					String destinationPath;
					if (fileItem.getPrependType() == DownloadPrependType.NodeId) {
						destinationPath = generateNodeSpecificPath(fileItem.getDestination().replace("\\", "/"), sshClient.id());
					} else if (fileItem.getPrependType() == DownloadPrependType.NodeIp) {
						destinationPath = generateNodeSpecificPath(fileItem.getDestination().replace("\\", "/"), sshClient.ip());
					} else {
						destinationPath = fileItem.getDestination();
					}
					execId = sftpClient.getFile(fileItem.getSource().replace("\\", "/"), new File(destinationPath), sftpCallback);
				} else {
					execId = sftpClient.putFile(new File(fileItem.getSource().replace("\\", "/")), fileItem.getDestination().replace("\\", "/"), sftpCallback);
				}
				conditionalBarrier.block(displayId + "_" + item.getItemId());
			} catch (SftpClientException e) {
				e.printStackTrace();
				sftpCallback.done(e);
			} catch (Throwable t) {
				t.printStackTrace();
				sftpCallback.done(new SftpClientException("Server side error occurred"));
			}
		}

		private SshCommandAttributes getCommandAttributes() {
			RunAsAttributes runAsAttributes = null;
			if (step.getRunAs() != null || step.isAdmin()) {
				runAsAttributes = new RunAsAttributes(step.getRunAs(), step.getPassword(), step.isAdmin());
			}
			return new SshCommandAttributes(context.getExecutionId(), runAsAttributes, item.getAnswers(), item.isReboot());
		}

	}
}
