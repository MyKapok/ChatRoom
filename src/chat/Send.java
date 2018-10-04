package chat;

import java.io.*;
import java.net.Socket;

/**
 * 发送数据
 *
 */
public class Send implements Runnable {
    //从控制台接收数据
    private BufferedReader console;
    //从流接收数据
    private DataOutputStream dos;
    //线程标识符
    private boolean isRunning = true;
    //名称
    private String name;
    public Send() {
        console = new BufferedReader(new InputStreamReader(System.in));
    }
    public Send(Socket client,String name) {
        this();
        try {
            dos = new DataOutputStream(client.getOutputStream());
            this.name = name;
            send(this.name);
        } catch (IOException e) {
            isRunning = false;
            CloseUtil.closeAll(dos,console);
        }
    }
    //从控制台接受数据
    private String getMsgFromConsole(){
        try {
            return console.readLine();
        } catch (IOException e) {

        }
        return "";
    }
    //发送数据
    public void send(String msg){

        if(msg!=null&& !msg.equals("")){
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
               // e.printStackTrace();
                isRunning=false;
                CloseUtil.closeAll(dos,console);
            }
        }
    }
    @Override
    public void run() {
        while (isRunning){
            send(getMsgFromConsole());
        }
    }
}
