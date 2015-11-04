package com.dc.ssh.client.support;

import com.dc.ssh.client.SshException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileTransferSupport {

    public static void transfer(Session session, byte[] sourceBytes, String destination, String permissions) throws SshException {
        String command="scp -C " + "-t " + destination;
        try {
            Channel channel = createChannel(session, destination, command);
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            channel.connect();
            if(checkAck(in)!=0){
                throw new SshException("checkAck returned non-zero status");
            }
            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = sourceBytes.length;
            command = permissions + " " + filesize + " ";
            if(destination.lastIndexOf('/')>0){
                command+=destination.substring(destination.lastIndexOf('/')+1);
            }
            else{
                command+=destination;
            }
            command+="\n";
            out.write(command.getBytes()); out.flush();
            if(checkAck(in)!=0){
                throw new IOException("checkAck returned non-zero status");
            }

            // send a content of lfile
            ByteArrayInputStream bis = new ByteArrayInputStream(sourceBytes);
            byte[] buf=new byte[1024];
            while(true){
                int len=bis.read(buf, 0, buf.length);
                if(len<=0) break;
                out.write(buf, 0, len); //out.flush();
            }
            bis.close();
            // send '\0'
            buf[0]=0; out.write(buf, 0, 1); out.flush();
            if(checkAck(in)!=0){
                throw new IOException("checkAck returned non-zero status");
            }
            out.close();
            channel.disconnect();

        } catch (JSchException | IOException e) {
            throw new SshException(e);
        }
    }

    private static Channel createChannel(Session session, String destination, String command) throws JSchException {
        Channel channel=session.openChannel("exec");
        ((ChannelExec)channel).setCommand(command);
        return channel;
    }

    private static int checkAck(InputStream in) throws IOException{
        int b=in.read();
        // b may be 0 for success,
        // 1 for error,
        // 2 for fatal error,
        // -1
        if(b==0) return b;
        if(b==-1) return b;

        if(b==1 || b==2){
            StringBuffer sb=new StringBuffer();
            int c;
            do {
                c=in.read();
                sb.append((char)c);
            }
            while(c!='\n');
            if(b==1){ // error
                System.out.print(sb.toString());
            }
            if(b==2){ // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

}
