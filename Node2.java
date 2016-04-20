package bully.algorithm;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Ekirapa
 */
public class Node2 implements BullyInterface {
	ArrayList<String> nodes = new ArrayList<>();
	boolean foundgreater = false;
	static boolean electionInProgress = false;
	private static String thisNode = "2";
	private static String coordinator;
	static BullyInterface stub2;

	public Node2() {

	}

	public static void main(String[] args) {
		Node2 obj = new Node2();

		try {

			stub2 = (BullyInterface) UnicastRemoteObject.exportObject(obj, 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind(thisNode, stub2);

			System.err.println("Node" + thisNode + " is  ready");
			stub2.startElection(thisNode);
		} catch (RemoteException e) {
			System.out.println("Couldnt bind node to registry\n");
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			System.out.println("Node already bound to registry \n");
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new ShutDown());
		repeat();

	}

	public static void repeat() {
		Random rand = new Random();
		int randomNum = rand.nextInt((6 - 1) + 1) + 1;
		Timer timer = new Timer();
		timer.schedule(new TimerCheck(), randomNum * 1000);
	}

	@Override
	public String sayHello() throws RemoteException {
		return "This is a server speaking";
	}

	@Override
	public String startElection(String nodeId) throws RemoteException {
		electionInProgress = true;
		foundgreater = false;
		if (nodeId.equals(thisNode)) {
			System.out.println("You started the elections");

			Registry reg = LocateRegistry.getRegistry();
			for (String nodeName : reg.list()) {
				if (!nodeName.equals(thisNode) && Integer.parseInt(nodeName) > 2) {
					System.out.println(reg.list().length);
					BullyInterface stub;
					try {
						stub = (BullyInterface) reg.lookup(nodeName);
						System.out.println("Sending election challenge to " + nodeName);
						stub.startElection(nodeId);
						foundgreater = true;

					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (!foundgreater) {
				iWon(thisNode);
			}

			return null;
		} else {
			System.out.println("Received election request from " + nodeId);
			sendOk(thisNode, nodeId);
			return null;
		}
	}

	@Override
	public String sendOk(String where, String to) throws RemoteException {
		if (!thisNode.equals(to)) {
			try {
				Registry reg = LocateRegistry.getRegistry();
				BullyInterface stub = (BullyInterface) reg.lookup(to);
				System.out.println("Sending OK to " + to);
				stub.sendOk(where, to);

				// start election after sending OK
				startElection(thisNode);
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		} else {
			// receive OK
			System.out.println(where + " Replied with Ok..");
		}
		return null;
	}

	@Override
	public String iWon(String node) throws RemoteException {
		coordinator = node;
		electionInProgress = false;
		if (node.equals(thisNode)) {
			// send win
			System.out.println("You have won the election.");
			System.out.println("Bragging about winning to other nodes.....");
			Registry reg = LocateRegistry.getRegistry();
			for (String nodeName : reg.list()) {
				if (!nodeName.equals(thisNode)) {
					BullyInterface stub;
					try {
						stub = (BullyInterface) reg.lookup(nodeName);
						stub.iWon(node);

					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			System.out.println("Node " + node + " is the new Coodinator\n");
		} else {
			// receive win
			System.out.println("Node " + node + " has won the election.");
			System.out.println("Node " + node + " is the new Coodinator\n");
		}
		return null;
	}

	@Override
	public String register(String id) throws RemoteException {
		nodes.add(id);
		return null;
	}

	static class TimerCheck extends TimerTask {

		@Override
		public void run() {
			if (!thisNode.equals(coordinator) && !electionInProgress) {
				try {
					Registry reg = LocateRegistry.getRegistry();
					BullyInterface stub;
					stub = (BullyInterface) reg.lookup(coordinator);
					stub.isalive();

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					coordinatorCrashed();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					coordinatorCrashed();
				}
			}
			repeat();

		}
	}

	private static void coordinatorCrashed() {
		System.out.println("Coordinator has crushed. Iniating new election");
		try {
			stub2.startElection(thisNode);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean isalive() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}

	static class ShutDown extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				System.out.println("Terminating node");
				LocateRegistry.getRegistry().unbind(thisNode);
			} catch (AccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
