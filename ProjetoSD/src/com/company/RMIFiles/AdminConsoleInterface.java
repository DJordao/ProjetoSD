package com.company.RMIFiles;
import java.rmi.*;


public interface AdminConsoleInterface extends Remote{

    void print_on_client(String s) throws RemoteException;

}
