package chat;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<Mychennel> list = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        new Server().start();
    }




    public void start() throws IOException {
        ServerSocket server = new ServerSocket(9999);
        while(true){
            Socket client = server.accept();
            Mychennel chennel = new Mychennel(client);
            list.add(chennel);
            new Thread(chennel).start();
        }
    }




    private  class Mychennel implements  Runnable{
        private DataInputStream dis;
        private DataOutputStream dos;
        private boolean isRunning=true;
        private String name ;

        public Mychennel(Socket client) {
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                this.name = dis.readUTF();
                this.send("欢迎进入聊天室");
                this.send("本聊天室若想群聊请直接发言；若想私聊请@对方（格式：@用户名：）（@与：都为英文哦）");


                sendOthers(this.name+"进入聊天室",true);
            } catch (IOException e) {
                e.printStackTrace();
                CloseUtil.closeAll(dis,dos);
                isRunning=false;
            }
        }

        private String recevie(){
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
                CloseUtil.closeAll(dis,dos);
                isRunning=false;
                list.remove(this);
            }
            return msg;
        }




        private void send(String msg){
            if(null==msg||msg.equals("")){
                return;
            }
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                CloseUtil.closeAll(dis,dos);
                isRunning=false;
                list.remove(this);
            }
        }



        private void sendOthers(String msg,boolean sys){



            if(msg.startsWith("@")&& msg.indexOf(":")>-1){
                //获取name，正文
                String name = msg.substring(1,msg.indexOf(":"));
                String content = msg.substring(msg.indexOf(":")+1);

                for(Mychennel other:list){
                    if(other.name.equals(name)){
                        other.send(this.name+":"+content);
                    }
                }
            }
            else{
                for (Mychennel mychennel:list){
                    if(mychennel==this){
                        continue;
                    }
                    if(sys==true){
                        mychennel.send("系统消息："+msg);
                    }
                    else{
                    mychennel.send(this.name+":"+msg);
                    }
                }
            }

        }

        @Override
        public void run() {
            while(isRunning){
               sendOthers(recevie(),false);
            }
        }
    }
}
