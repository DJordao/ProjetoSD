package com.company.RMIFiles;

import com.company.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI extends Remote {
	String sayHello() throws RemoteException;

	 void remote_print(String s) throws RemoteException;

	void remote_print(Message m) throws RemoteException;

	Message ping_pong(Message m) throws RemoteException;
}