package com.brioal.cman.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * socket长连接类
 */
public class SocketThread implements Runnable {
	private static Socket s = null;
	DataOutputStream dos = null;
	OutputStreamWriter outSW = null;
	BufferedWriter bw = null;
	DataInputStream dis = null;
	InputStreamReader inSW = null;
	BufferedReader br = null;
	public static boolean bConnected = false;
	private static boolean rcvRunning = false;//控制接收线程终止，因为收发线程共用，所以要提出来，作为成员变量
	String ServerIP;
	int port;
	ReceiveThread receiveThread = null;	//接收线程，只启一个
	public SocketThread(String ServerIP, int port){
		this.ServerIP = ServerIP;
		this.port = port;
		if(!bConnected){
			connect();
		}
	}

	@Override
	public void run() {

	}
	private void connect() {
		try {
			s = new Socket(ServerIP, port);
			dos = new DataOutputStream(s.getOutputStream());
			outSW = new OutputStreamWriter(dos, "GBK");
			bw = new BufferedWriter(outSW);

			dis = new DataInputStream(s.getInputStream());
//		    inSW = new InputStreamReader(dis, "GBK");
//		    br = new BufferedReader(inSW);

			System.out.println("~~~~~~~~连接成功~~~~~~~~!");
			bConnected = true;
			rcvRunning = true;//
//		    receiveThread = new ReceiveThread(s);
//		    receiveThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			dos.close();
			dis.close();
			s.close();
			bConnected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public  int SendThread (String sendstr) {

		try {
			if(!bConnected){
				connect();
			}
			//Socket sp = new Socket();
			bw.write(sendstr + "\r\n");     //加上分行符，以便服务器按行读取
			bw.flush();
//		    dos.write(sendstr.getBytes("UTF-8"));
//		    dos.flush();
			return 0;
		} catch (IOException e1) {
			e1.printStackTrace();
			return -1;
		}
	}
	public String RecvThread () {
		String str = "";
		while(bConnected)
		{
//	      try {

			if (rcvRunning) {
				//接收数据
//	        	while((str = br.readLine()) != null) {
//	        	    str = str.trim();
//	        	    System.out.println("服务器回复：" + str);
//	        	    break;
//	        	}
				byte[] buffer;
				int revLen = 0;//接收长度
				buffer = new byte[192];//512
				try {
					revLen = dis.read(buffer);//阻塞式接收，收不到，是不会返回的，收到了立刻返回
					System.out.println("接收到的数据"+buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try{
					Thread.currentThread().sleep(100);
				}catch (InterruptedException e){}
				//str = dis.readUTF();
			}
//	      } catch (SocketException e) {
//	        System.out.println("退出了，bye!");
//	      } catch (EOFException e) {
//	        System.out.println("退出了，bye!");
//	      } catch (IOException e) {
//	        e.printStackTrace();
//	      }
		}
		return str;
	}
	//接收线程
	private class ReceiveThread extends Thread{
		private DataInputStream dis = null;

		//接收buffer,接收的接口,需要修改程序,作为类似函数的输出参数
		//接收到的数据，在buffer里
		private byte[] buffer;
		////////////////////////////////
		//接收到的数据转成字符串
		//private String str = null;
		////////////////////////////////

		ReceiveThread(Socket socket){
			try {
				dis = new DataInputStream(socket.getInputStream());
				inSW = new InputStreamReader(dis, "GBK");
				br = new BufferedReader(inSW);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@SuppressWarnings("static-access")
		@Override
		public void run(){
			while(rcvRunning){
				int revLen = 0;//接收长度
				buffer = new byte[192];//512

				//BufferedReader br = new BufferedReader(inSW);
//				try {
//					String str = br.readLine();
//					while(str != null) {
//					    str = str.trim();
//					    System.out.println("服务器回复：" + str);
//					    break;
//					}
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				try {
					revLen = dis.read(buffer);//阻塞式接收，收不到，是不会返回的，收到了立刻返回
					System.out.println("接收到的数据"+buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}

//					if(revLen>0){
//						sumOfSdTimesNoReceive = 0;	
//						handleReceivedPacket(buffer,revLen);	
//						
//					}
				try{
					Thread.currentThread().sleep(100);
				}catch (InterruptedException e){}
			}
		}
	}

}
