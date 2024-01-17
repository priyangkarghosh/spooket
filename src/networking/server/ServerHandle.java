package networking.server;

import gameobjects.SPlayer;
import networking.Packet;

/**
 * The Class ServerHandle, handles packets sent from client.
 * 
 * @author priyangkar ghosh
 */
public class ServerHandle {

	/**
	 * Handle using packet id.
	 *
	 * @param {int} from - the from
	 * @param {byte[]} buffer - the buffer
	 */
	public static void handle(int from, byte[] buffer) {
		Packet packet = new Packet(buffer);

		try {
			switch (packet.readInt()) {
				case 0:
					admissionReceived(from, packet);
					break;
				case 1:
					clientDisconnected(from, packet);
					break;
				case 2:
					playerInput(packet);
					break;
				default:
					break;
			}
		}

		catch (NullPointerException e) { }
	}

	/**
	 * Admission received.
	 *
	 * @param {int} from - the from
	 * @param {Packet} packet - the packet
	 */
	private static void admissionReceived(int from, Packet packet) {
		// reads packet contents
		int id = packet.readInt();
		String name = packet.readString();
		int udpPort = packet.readInt();

		System.out.println("[SERVER] Client with id of " + from + " connected successfully.");
		if (id != from) {
			System.out.printf("[SERVER] Client %s with id %d has assumed a wrong id of %d\n", name, from, id);
			return;
		}
		
		// updates the server client with the updates information
		SClient sc = Server.instance().getClient(from);
		sc.setName(name); sc.port = udpPort;
		sc.player = new SPlayer(id, name);
		
		// sends that the player has connected
		ServerSend.playerConnected(id, sc.player.getKills(), sc.player.getHealth(), name, sc.player.transform);
		ServerSend.validateUDP(from);
	}

	/**
	 * Client disconnected.
	 *
	 * @param {int} from - the from
	 * @param {Packet} packet - the packet
	 */
	private static void clientDisconnected(int from, Packet packet) {
		// removes the player
		Server.instance().removeClient(from);
		System.out.println("[SERVER] Client with id of " + from + " has disconnected successfully.");
		
		// sends the removal to all other clients
		ServerSend.playerDisconnected(from);
	}

	/**
	 * Player input.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void playerInput(Packet packet) {
		int id = packet.readInt();
		
		// processes the input packet for this player
		SClient sc = Server.instance().getClient(id);
		sc.player.controller.inputManager.process(packet);
	}
}
