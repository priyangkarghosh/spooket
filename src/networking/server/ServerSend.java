package networking.server;

import java.io.IOException;
import java.net.DatagramPacket;

import framework.common.GameObject;
import framework.common.Transform;
import networking.Packet;
import scenes.GameScene;

/**
 * The Class ServerSend, sends packets from server to the clients.
 * 
 * @author priyangkar ghosh
 */
public class ServerSend {

	/**
	 * Send TCP packet to specific client.
	 *
	 * @param {int} to - the to
	 * @param {Packet} packet - the packet
	 */
	private static void sendTCP(int to, Packet packet) {
		try {
			SClient sc = Server.instance().getClient(to);
			// writes the packet then flushes the stream
			sc.out.write(packet.getBuffer());
			sc.out.flush();
		}

		catch (IOException e) {
			System.out.println("[SERVER] TCP to client " + to + "sending failed.");
		}

		catch (NullPointerException e) {

		}
	}

	/**
	 * Send all TCP.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void sendTCP(Packet packet) {
		// sends tcp packet to all clients
		for (int id = 0; id < Server.MAX_PLAYERS; id++)
			sendTCP(id, packet);
	}

	/**
	 * Send UDP packet.
	 *
	 * @param {int} to - the to
	 * @param {Packet} packet - the packet
	 */
	private static void sendUDP(int to, Packet packet) {
		try {
			// gets the client
			SClient sc = Server.instance().getClient(to);
			
			// creates a udp packet
			DatagramPacket udpPacket = new DatagramPacket(
					packet.getBuffer(), Packet.BUFFER_ALLOCATE,
					sc.address, sc.port
			);
			
			// sends the udp packet to that client
			Server.instance().udp.socket.send(udpPacket);
		}

		catch (IOException e) {
			System.out.println("[SERVER] UDP sending to client " + to + " failed.");
		}

		catch (NullPointerException e) {

		}
	}

	/**
	 * Send all UDP.
	 *
	 * @param {Packet} packet - the packet
	 */
	private static void sendUDP(Packet packet) {
		// sends a udp packet to all clients
		for (int id = 0; id < Server.MAX_PLAYERS; id++)
			sendUDP(id, packet);
	}

	/**
	 * Admit.
	 *
	 * @param {int} id - the id
	 * @param {String} msg - the msg
	 */
	public static void admit(int id, String msg) {
		Packet packet = new Packet(Packet.Server.ADMIT);
		
		// writes the packet content
		packet.write(id);
		packet.write(msg);

		sendTCP(id, packet);
	}

	/**
	 * Validate UDP.
	 *
	 * @param {int} id - the id
	 */
	public static void validateUDP(int id) {
		Packet packet = new Packet(Packet.Server.VALIDATE);

		// writes the packet content
		packet.write(id);

		sendUDP(id, packet);
	}

	/**
	 * Player connected.
	 *
	 * @param {int} id - the id
	 * @param {int} kills - the kills
	 * @param {double} health - the health
	 * @param {String} name - the name
	 * @param {Transform} transform - the transform
	 */
	public static void playerConnected(int id, int kills, double health, String name, Transform transform) {
		Packet packet = new Packet(Packet.Server.PLAYER_CONNECTED);
		
		// writes the packet content
		packet.write(id);
		packet.write(name);
		packet.write(kills);
		packet.write(health);
		packet.write(transform);

		sendTCP(packet);

		// send the other players to the new client
		for (int i = 0; i < Server.MAX_PLAYERS; i++) {
			if (i == id) continue;

			SClient client = Server.instance().getClient(i);
			if (client == null) continue;
			
			packet = new Packet(Packet.Server.PLAYER_CONNECTED);

			// writes the packet content
			packet.write(client.getId());
			packet.write(client.getName());
			packet.write(client.player.getKills());
			packet.write(client.player.getHealth());
			packet.write(client.player.transform);

			sendTCP(id, packet);
		}
	}

	/**
	 * Player disconnected.
	 *
	 * @param {int} id - the id
	 */
	public static void playerDisconnected(int id) {
		Packet packet = new Packet(Packet.Server.PLAYER_DISCONNECTED);
		
		// writes the packet content
		packet.write(id);

		sendTCP(packet);
	}

	/**
	 * Player information.
	 *
	 * @param {int} id - the id
	 * @param {double} health - the health
	 * @param {boolean} isShooting - the is shooting
	 * @param {Transform} transform - the transform
	 */
	public static void playerInformation(int id, double health, boolean isShooting, Transform transform) {
		Packet packet = new Packet(Packet.Server.PLAYER_INFORMATION);

		// writes the packet content
		packet.write(id);
		packet.write(health);
		packet.write(isShooting);
		packet.write(transform);

		sendUDP(packet);
	}

	/**
	 * When a player kills another player.
	 *
	 * @param {int} id - the id
	 */
	public static void killedPlayer(int id) {
		Packet packet = new Packet(Packet.Server.KILLED_PLAYER);
		
		// writes the packet content
		packet.write(id);

		sendTCP(packet);
	}

	/**
	 * Shot added.
	 *
	 * @param {String} id - the id
	 * @param {Transform} transform - the transform
	 */
	public static void shotAdded(String id, Transform transform) {
		Packet packet = new Packet(Packet.Server.SHOT_ADDED);
		
		// writes the packet content
		packet.write(id);
		packet.write(transform);

		sendTCP(packet);
	}

	/**
	 * Remove a shot.
	 *
	 * @param {String} id - the id
	 */
	public static void shotRemoved(String id) {
		Packet packet = new Packet(Packet.Server.SHOT_REMOVED);
		
		// writes the packet content
		packet.write(id);

		sendTCP(packet);
	}

	/**
	 * Shot information.
	 *
	 * @param {String} id - the id
	 * @param {Transform} transform - the transform
	 */
	public static void shotInformation(String id, Transform transform) {
		Packet packet = new Packet(Packet.Server.SHOT_INFORMATION);
		
		// writes the packet content
		packet.write(id);
		packet.write(transform);

		sendUDP(packet);
	}

	/**
	 * If the server was stopped, tell the client.
	 */
	public static void serverStopped() {
		Packet packet = new Packet(Packet.Server.SERVER_STOPPED);
		sendTCP(packet);
	}
	
	/**
	 * Asteroid information.
	 *
	 * @param {GameObject[]} asteroids - the asteroids
	 */
	public static void asteroidInformation(GameObject[] asteroids) {
		Packet packet = new Packet(Packet.Server.ASTEROID_INFORMATION);

		for (int i = 0; i < GameScene.NUM_OF_ASTEROIDS; i++)
			if (asteroids[i] != null) packet.write(asteroids[i].transform);

		sendUDP(packet);
	}
}