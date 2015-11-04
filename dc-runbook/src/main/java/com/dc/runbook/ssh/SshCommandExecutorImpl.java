package com.dc.runbook.ssh;

import com.dc.DcException;
import com.dc.DcLoggerFactory;
import com.dc.runbook.rt.cmd.IndividualCmdCancelRequest;
import com.dc.runbook.rt.cmd.IndividualCmdExecRequest;
import com.dc.runbook.rt.cmd.IndividualCmdRequest;
import com.dc.runbook.rt.cmd.exec.CommandExecCallbackImpl;
import com.dc.runbook.rt.cmd.exec.CommandExecutionResult;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.rt.domain.item.DtRunbookCommand;
import com.dc.runbook.rt.domain.item.DtRunbookMultiCommand;
import com.dc.runbook.rt.domain.item.DtRunbookScript;
import com.dc.ssh.client.CommandExecutionCallback;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.RunAsAttributes;
import com.dc.ssh.client.exec.cmd.SingleSshCommand;
import com.dc.ssh.client.exec.cmd.SshCommand;
import com.dc.ssh.client.exec.cmd.SshCommandAttributes;
import com.dc.ssh.client.exec.cmd.script.MultiSshCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptAttributes;
import com.dc.ssh.client.exec.cmd.script.ScriptCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;

public class SshCommandExecutorImpl implements SshCommandExecutor {
	public void execute(IndividualCmdRequest request) {
		IndividualCmdExecRequest execRequest = (IndividualCmdExecRequest) request;
		executeCommandRequest(execRequest);
	}

	public void cancel(IndividualCmdRequest request) {
		IndividualCmdCancelRequest cancelRequest = (IndividualCmdCancelRequest) request;
		try {
			cancelRequest.getSshClient().cancel(cancelRequest.getExecutionId());
		} catch (SshException e) {
			DcLoggerFactory.getInstance().getLogger().error(e.getMessage(), e);
			// TODO: handle exceptions. How to inform user, that cancel is
			// failed?
		}
	}

	private void executeCommandRequest(IndividualCmdExecRequest execRequest) {
		CommandExecutionResult result;
		CommandExecutionCallback execCallback = new CommandExecCallbackImpl(execRequest.getCallback());

		try {
			executeSystemCommand(execRequest, execCallback, execRequest.getSshClient(), execRequest.getExecutionId());
		} catch (SshException e) {
			DcLoggerFactory.getInstance().getLogger().error(execRequest.toString(), e);
			result = new CommandExecutionResult.Builder().code(execCallback.getStatusCode()).failed(true).build();
			execRequest.getCallback().done(result);
		} catch (Throwable e) {
			DcLoggerFactory.getInstance().getLogger().error(e.getMessage(), e);
			result = new CommandExecutionResult.Builder().failed(true).code(88888888).build();
			execRequest.getCallback().done(result);
		}
	}

	private void executeSystemCommand(IndividualCmdExecRequest execRequest, CommandExecutionCallback callback, SshClient sshClient, String executionId) throws SshException {
		// List<CommandInputDto> inputs = execRequest.getCommandInputDtoList();
		SshCommand command = generateSshCommand(execRequest.getCommand(), executionId);
		sshClient.execute(command, callback);
	}

	private SshCommand generateSshCommand(DtRunbookStep commandInput, String executionId) {
		SshCommand command;
		switch (commandInput.getItem().getType()) {
			case Script:
				command = generateScriptCommand(commandInput, executionId);
				break;
			case Command:
				command = generateSingleCommand(commandInput, executionId);
				break;
			case MultiCommand:
				command = generateMultiCommand(commandInput, executionId);
				break;
			default:
				throw new DcException("Invalid Command Input Type specified. Type : " + commandInput.getItem().getType());
		}
		return command;
	}

	private SshCommand generateScriptCommand(DtRunbookStep commandInput, String executionId) {
		DtRunbookScript scriptItem = (DtRunbookScript)commandInput.getItem();
		SshCommandAttributes cmdAttributes = getCommandAttributes(executionId, commandInput);
		String arguments = (scriptItem.getArgs() != null && scriptItem.getArgs().size() > 0) ? scriptItem.getArgs().get(0) : null;
		ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, arguments, scriptItem.getLanguage(), scriptItem.getInvokingProgram(), scriptItem.getFileName());
		return new ScriptCommand(scriptAttributes, ((DtRunbookScript) commandInput.getItem()).getScript());
	}

	private SshCommand generateSingleCommand(DtRunbookStep commandInput, String executionId) {
		SshCommandAttributes cmdAttributes = getCommandAttributes(executionId, commandInput);
		return new SingleSshCommand(cmdAttributes, ((DtRunbookCommand) commandInput.getItem()).getCommand()); // @TODO: Later add logic to handle other inputs like RunAs, reboot
	}

	private SshCommand generateMultiCommand(DtRunbookStep commandInput, String executionId) {
		SshCommandAttributes cmdAttributes = getCommandAttributes(executionId, commandInput);
		ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, null, ScriptLanguage.Shell, "sh", null);
		return new MultiSshCommand(scriptAttributes, ((DtRunbookMultiCommand) commandInput.getItem()).getCommands());
	}

	private SshCommandAttributes getCommandAttributes(String executionId, DtRunbookStep commandInput) {
		RunAsAttributes runAsAttributes = null;
		if (commandInput.getRunAs() != null || commandInput.isAdmin()) {
			runAsAttributes = new RunAsAttributes(commandInput.getRunAs(), commandInput.getPassword(), commandInput.isAdmin());
		}
		return new SshCommandAttributes(executionId, runAsAttributes, commandInput.getItem().getAnswers(), false);
	}

}
