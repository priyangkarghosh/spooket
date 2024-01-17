package networking.client;

import javax.swing.JOptionPane;

import framework.audio.AudioManager;
import framework.common.Transform;
import networking.Packet;
import scenes.GameScene;
import scenes.PlayScene;
import scenes.Scene;

/**
 * The Class ClientHandle, used to handle packets from server to client.
 * 
 * @author priyangkar ghosh
 */
public class ClientHandle {

	/**
	 * Handles packet based on the packet id.
	 *
	 * @param {byte[]} buffer - the buffer
	 */
	public static void handle(byte[] buffer) {
		Packet packet = new Packet(buffer);
		
		// gets the packet id, then calls the corresponding function
		switch (packet.readInt()) {
			case 0:
				admit(packet);
				break;
			case 1:
				validateUDP(packet);
				break;
			case 2:
				playerConnected(packet);
				break;
			case 3:
				playerDisconnected(packet);
				break;
			case 4:
				playerInformation(packet);
				break;
			case 5:
				shotAdded(packet);
				break;
			case 6:
				shotRemoved(packet);
				break;
			case 7:
				shotInformation(packet);
				break;
			case 8:
				killedPlayer(packet);
				break;
			case 9:
				serverStopped(packet);
			case 10:
				asteroidInformation(packet);
			default:
				break;
		}
	}

	/**
	 * Admit packet.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void admit(Packet packet) {
		// returns if the client has already been admitted
		if (CClient.instance().id != -1) return;
		
		// reads the packet content
		int id = packet.readInt();
		String msg = packet.readString();
		
		// sets the clients id and the server message
		CClient.instance().id = id;
		System.out.println("[CLIENT] " + "My id is " + id + ", message: " + msg);
		
		// sends the admission received package to the server
		ClientSend.admissionReceived();
		
		// connects the udp socket
		CClient.instance().udp.socket.connect(
				CClient.instance().tcp.socket.getInetAddress(),
				CClient.instance().tcp.socket.getPort()
		);
	}

	/**
	 * Validate UDP.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void validateUDP(Packet packet) {
		// if this packet is received, the udp has been connected 
		int id = packet.readInt();
		System.out.println("[CLIENT] UDP connected successfully. ");
		
		// checks to make sure that the client has the right id
		if (id != CClient.instance().id)
			System.out.println("[CLIENT] Assumed incorrect id.");
	}

	/**
	 * Player connected packet.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void playerConnected(Packet packet) {
		// reads the packet contents
		int id = packet.readInt();
		String name = packet.readString();
		
		int kills = packet.readInt();
		double health = packet.readDouble();
		Transform transform = packet.readTransform();
		
		// the client manager adds the player to the scene
		ClientManager.addPlayer(id, name, kills, health, transform);
		System.out.printf("[CLIENT] Player added, id: %d, name: %s\n", id, name);
	}

	/**
	 * Player disconnected.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void playerDisconnected(Packet packet) {
		// reads the packet contents
		int id = packet.readInt();
		
		// removes the player from the scene
		ClientManager.removePlayer(id);
		System.out.printf("[CLIENT] Player with id %d was removed\n", id);
	}

	/**
	 * Player information.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void playerInformation(Packet packet) {
		// read the packet contents
		int id = packet.readInt();
		double health = packet.readDouble();
		boolean isShooting = packet.readBoolean();
		Transform transform = packet.readTransform();
		
		// updates the player contents
		ClientManager.updatePlayer(id, health, isShooting, transform);
	}

	/**
	 * Shot added.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void shotAdded(Packet packet) {
		// reads the packet contents
		String id = packet.readString();
		Transform transform = packet.readTransform();
		
		// adds the new shot
		ClientManager.addShot(id, transform);
		AudioManager.play("shoot", false);
	}

	/**
	 * Shot removed.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void shotRemoved(Packet packet) {
		// reads the packet contents
		String id = packet.readString();
		
		// removes the corresponding shot
		ClientManager.removeShot(id);
	}

	/**
	 * Shot information.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void shotInformation(Packet packet) {
		// read the packet contents
		String id = packet.readString();
		Transform transform = packet.readTransform();
		
		// update the corresponding shot
		ClientManager.updateShot(id, transform);
	}

	/**
	 * Player killed packet.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void killedPlayer(Packet packet) {
		// read the packet contents
		int id = packet.readInt();
		
		// adds a kill to the player with that id
		ClientManager.killedPlayer(id);
	}

	/**
	 * Server stopped.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void serverStopped(Packet packet) {
		// disconnect the client if the server was stopped
		CClient.instance().disconnect();
		Scene.load(new PlayScene());
		JOptionPane.showMessageDialog(null, "Server was stopped.");
	}
	
	/**
	 * Updated asteroid information.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void asteroidInformation(Packet packet) {
		for (int i = 0; i < GameScene.NUM_OF_ASTEROIDS; i++)
			ClientManager.updateAsteroid(i, packet.readTransform());
	}
}
