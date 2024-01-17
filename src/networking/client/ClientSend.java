package networking.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;

import framework.common.InputManager.Controls;
import networking.Packet;

/**
 * The Class ClientSend, used to send packets from client to server.
 * 
 * @author priyangkar ghosh
 */
public class ClientSend {
	
	/**
	 * Send a TCP packet to the server.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void sendTCP(Packet packet) {
		try {
			// writes the packet to the output stream and then flushes
			CClient.instance().tcp.out.write(packet.getBuffer());
			
			// if it's not flushed, it won't send
			CClient.instance().tcp.out.flush();
		}

		catch (IOException e) {
			System.out.println("[CLIENT] TCP sending failed.");
		}
	}

	/**
	 * Send UDP packet to the server.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void sendUDP(Packet packet) {
		try {
			// creates a new datagram packet from the packet
			DatagramPacket udpPacket = new DatagramPacket(
					packet.getBuffer(), Packet.BUFFER_ALLOCATE,
					CClient.instance().udp.socket.getInetAddress(),
					CClient.instance().udp.socket.getPort()
			);
			
			// sends the packet using udp
			CClient.instance().udp.socket.send(udpPacket);
		}

		catch (IOException e) {
			System.out.println("[CLIENT] UDP sending failed.");
		}
	}
	
	//** THE PACKETS THE CLIENT CAN SEND TO THE SERVER */
	
	/**
	 * Admission received.
	 */
	public static void admissionReceived() {
		// initializes the packet with its id
		Packet packet = new Packet(Packet.Client.ADMISSION_RECEIVED);
		
		// writes the contents of the packet
		packet.write(CClient.instance().id);
		packet.write(CClient.instance().name);
		packet.write(CClient.instance().udp.socket.getLocalPort());
		
		// sends it to the server
		sendTCP(packet);
	}

	/**
	 * Client disconnect.
	 */
	public static void clientDisconnect() {
		// initializes the packet with its id
		Packet packet = new Packet(Packet.Client.CLIENT_DISCONNECTED);
		
		// writes the contents of the packet
		ClientManager.removePlayer(CClient.instance().id);

		// sends it to the server using tcp
		sendTCP(packet);
	}

	/**
	 * Player input.
	 */
	public static void playerInput() {
		// initializes the packet with its id
		Packet packet = new Packet(Packet.Client.PLAYER_INPUT);
		
		// writes the contents of the packet
		try {
			packet.write(CClient.instance().id);
			HashMap<Controls, Boolean> inputMap = CClient.instance().input.getInput();
			for (Controls key : Controls.values()) packet.write(inputMap.get(key));
			packet.write(CClient.instance().input.getMousePosition());
			
			// sends it to the server using udp
			sendUDP(packet);
		} 
		
		catch (NullPointerException e) {
			// if input is null it doesn't send the packet
		}
	}
}
