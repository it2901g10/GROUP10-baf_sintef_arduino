package no.ntnu.osnap.com;

import java.io.IOException;
import java.util.ArrayDeque;


public class ProtocolThread extends Thread {
	private ArrayDeque<ProtocolInstruction> commandQueue;
	private Protocol parent;
	private ProtocolInstruction currentInstruction;
	
	public boolean running;
	
	public ProtocolThread(Protocol parent){
		commandQueue = new ArrayDeque<ProtocolInstruction>();
		running = true;
		this.parent = parent;
	}
	
	@Override
	public synchronized void run(){
		while (running){
			if (commandQueue.isEmpty()){
				try {
					wait();
				} catch (InterruptedException ex) {
					break;
				}
			}
			
			currentInstruction = commandQueue.pop();
			try {
				parent.sendBytes(currentInstruction.getInstructionBytes());
			} catch (IOException ex) {
				// TODO: use logger
				System.out.println("Send derp");
			}
		}
	}
	
	public synchronized void issueCommand(ProtocolInstruction instruction){
		commandQueue.add(instruction);
		notify();
	}
}
