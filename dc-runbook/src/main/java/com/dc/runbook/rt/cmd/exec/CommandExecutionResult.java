/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc.runbook.rt.cmd.exec;

public class CommandExecutionResult {
	private boolean	failed;
	private boolean	cancelled;
	private boolean	unreachable;
	private int	    failedCommandIndex;
	private int	    code;

	private CommandExecutionResult(Builder builder) {
		this.failed = builder.failed;
		this.cancelled = builder.cancelled;
		this.unreachable = builder.unreachable;
		this.failedCommandIndex = builder.failedCommandIndex;
		this.code = builder.code;
	}

	public boolean isFailed() {
		return failed;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isUnreachable() {
		return unreachable;
	}

	public int getFailedCommandIndex() {
		return failedCommandIndex;
	}

	public int getCode() {
		return code;
	}

	public static class Builder {
		private boolean	failed;
		private boolean	cancelled;
		private boolean	unreachable;
		private int		failedCommandIndex;
		private int		code;

		public Builder failed(boolean failed) {
			this.failed = failed;
            this.code = 989898;
			return this;
		}

		public Builder cancelled(boolean cancelled) {
			this.cancelled = cancelled;
			return this;
		}

		public Builder unreachable(boolean unreachable) {
			this.unreachable = unreachable;
			return this;
		}

		public Builder failedCommandIndex(int failedCommandIndex) {
			this.failedCommandIndex = failedCommandIndex;
			return this;
		}

		public Builder code(int code) {
			this.code = code;
			return this;
		}

		public CommandExecutionResult build() {
			return new CommandExecutionResult(this);
		}
	}
}
