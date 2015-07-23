import java.net.*;
import java.io.*;
import java.util.*;
/***
 Echoサーバへの接続
 telnet <ホスト名> [ポート番号]
 telnet localhost 18080
 
 学生証番号：344432
 氏名：　　　神田　拓哉
 
 344225
 大下 隼人
 
 345369
 本山　滉樹
 
 今回はnameメソッドのなかでクライアントの名前が他のクラインとの名前とhandler.getClientName().equals(message)をif文のなかの条件分岐でつかいthis.nameからループでまわるたびに判定を行いクライアント同士の名前の一致の確認の仕方を工夫しました。また、usersメソッドではpostを利用し、usersでは自分の名前も表示するためif(handler != this)を使わないなどの少しの変化でプログラムを書く事ができました。
 
 ***/

public class ChatClientHandler extends Thread{
    private Socket socket; //クライアントを表すソケット
    private BufferedReader in;
    private BufferedWriter out;
    List clients;
    String name;
    
    public ChatClientHandler(Socket socket, List clients){
        this.socket = socket;
        this.clients = clients;
        this.name = "undefined" + (clients.size() + 1);
    }
    
    public String getClientName(){
        return name;
    }
    
    public void run(){
        try{
            open();
            while(true){
                String message = receive();
                String[] commands = message.split(" ");
                if(commands[0].equalsIgnoreCase("post")){//post
                    post(commands[1]);
                }
                else if(commands[0].equalsIgnoreCase("bye")){//bye
                    bye();
                }
                else if(commands[0].equalsIgnoreCase("help")){//help
                    help();
                }
                else if(commands[0].equalsIgnoreCase("name")){//name
                    name(commands[1]);
                }
                else if(commands[0].equalsIgnoreCase("whoamai")){//whoamai
                    whoamai();
                }
                else if(commands[0].equalsIgnoreCase("users")){//users
                    users();
                }
                if(message.equals("")) break;
                send(message);
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally{
            close();
        }
    }
    
    public void users() throws IOException{//users
        List names = new ArrayList();
        for(int i = 0; i < clients.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)clients.get(i);
            names.add(handler.getClientName());
        }
        Collections.sort(names);
        String returnMessage = "";
        for(int i = 0; i < names.size(); i++){
            returnMessage = returnMessage + names.get(i)+ ",";
        }
        send("現在サーバに接続しているユーザは以下の通りです");
        this.send(returnMessage);
    }
    
    public void whoamai() throws IOException{//whoamai
        send("Your name is set");
        send(getClientName());
    }
    
    public void help() throws IOException{//help
        send("処理可能な命令一覧");
        send("help, name, whoamai, bye, post, users");
    }
    
    public void name(String message) throws IOException{//name
        List names = new ArrayList();
        for(int i = 0; i < clients.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)clients.get(i);
            if(handler.getClientName().equals(message)){
                send("名前が同一です。");
            }
        }
        this.name=message;
    }
    
    public void post(String message) throws IOException{//post
        //接続しているクライアント全員に送りたい。
        List names = new ArrayList();
        for(int i = 0; i < clients.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)clients.get(i);
            if(handler != this){
                names.add(handler.getClientName());
                handler.send("[" + this.getClientName() + "]" + message);
            }
        }
        Collections.sort(names);
        String returnMessage = "";
        for(int i = 0; i < names.size(); i++){
            returnMessage = returnMessage + names.get(i)+ ",";
        }
        this.send(returnMessage);
    }
    
    public void open() throws IOException{
        //クライアントとのデータのやり取りを行なうストリームを開くメソッド
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        
    }
    
    public String receive() throws IOException{//クライアントからデータを受け取るメソッド
        String line = in.readLine();
        System.out.println(line);
        return line;
    }
    
    public void send(String message) throws IOException{//クライアントにデータを送信するメソッド
        out.write(message);
        out.write("\r\n");
        out.flush();
    }
    
    public void close(){//in, out, socketのストリームを閉じる
        if(in != null){
            try{
                in.close();
            } catch(IOException e){ }
        }
        if(out != null){
            try{
                out.close();
            } catch(IOException e){ }
        }
        if(socket != null){
            try{
                socket.close();
            } catch(IOException e){ }
        }
    }
    
    public void bye() throws IOException{//クライアントの接続を閉じるメソッド
        send("サーバへの接続を終了します。");
        
        if(clients != null){
            for(int i=0; i < clients.size(); i++){
                ChatClientHandler handler = (ChatClientHandler)clients.get(i);
                if(handler.equals(this)) clients.remove(i);
            }
            close();
        }
    }
}









