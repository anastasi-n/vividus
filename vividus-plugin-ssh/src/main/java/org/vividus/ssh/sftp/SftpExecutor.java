/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.ssh.sftp;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Named;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import org.vividus.softassert.ISoftAssert;
import org.vividus.ssh.Commands;
import org.vividus.ssh.JSchExecutor;
import org.vividus.ssh.SingleCommand;
import org.vividus.ssh.SshConnectionParameters;

@Named
public class SftpExecutor extends JSchExecutor<ChannelSftp, SftpOutput>
{
    private final ISoftAssert softAssert;

    public SftpExecutor(ISoftAssert softAssert)
    {
        this.softAssert = softAssert;
    }

    @Override
    public String getChannelType()
    {
        return "sftp";
    }

    @Override
    protected SftpOutput executeCommand(SshConnectionParameters sshConnectionParameters, Commands commands,
            ChannelSftp channel) throws JSchException, IOException
    {
        channel.setAgentForwarding(sshConnectionParameters.isAgentForwarding());
        channel.setPty(sshConnectionParameters.isPseudoTerminalEnabled());
        channel.connect();
        SftpOutput executionOutput = new SftpOutput();
        StringBuilder output = null;
        try
        {
            for (SingleCommand<SftpCommand> command : commands.getSingleCommands(SftpCommand::fromString))
            {
                String commandOutput = command.getCommand().execute(channel, command.getParameters());
                if (commandOutput != null)
                {
                    if (output == null)
                    {
                        output = new StringBuilder();
                    }
                    else
                    {
                        output.append(System.lineSeparator());
                    }
                    output.append(commandOutput);
                }
            }
        }
        catch (SftpException e)
        {
            softAssert.recordFailedAssertion("SFTP command error", e);
        }
        executionOutput.setResult(Optional.ofNullable(output).map(StringBuilder::toString));
        return executionOutput;
    }
}
