package networking.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

import framework.common.InputManager;
import game.PlayerPrefs;
import gameobjects.CPlayer;
import networking.Packet;

/**
 * The CClient singleton (client-side client).
 * 
 * @author priyangkar ghosh
 */
public class CClient implements Runnable {
	/** The id and name. */
	public int id = -1; // default id of -1	
	public String name;

	/** The tcp and udp connections. */
	protected TCP tcp;	
	protected UDP udp;

	/** The outstanding ticks. */
	private int ticks = 0;

	/** If the client is active. */
	private volatile boolean active = false;

	/** The player and input manager. */
	public CPlayer player;	
	public InputManager input;

	/** The singleton instance. */
	private volatile static CClient instance;

	/**
	 * Singleton driver code.
	 *
	 * @return {CClient} the client
	 */
	public synchronized static CClient instance() {
		if (instance == null) instance = new CClient();
		return instance;
	}

	/**
	 * Instantiates a new cliend-side client.
	 */
	private CClient() { }

	/**
	 * Ticks the client.
	 */
	public void tick() {
		if (this.active) this.ticks++;
	}

	/**
	 * Connect to server.
	 *
	 * @param {String} host - the host
	 * @param {int} port - the port
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void connect(String host, int port) throws IOException {
		// gets the username from player preferences
		this.name = PlayerPrefs.get("name", "anonymous");
		
		// checks if this computer can connect to the port
		InetAddress address = InetAddress.getByName(host);
		if (!address.isReachable(3000)) throw new IOException();
		
		// starts the connection
		try {
			new Thread(this).start();
			new Thread(this.udp = new UDP()).start();
			new Thread(this.tcp = new TCP(host, port)).start();
		} 
		
		// if it cant connect, it throws an exception
		catch (IOException e) {
			System.out.println("[CLIENT] Could not connect to server.");
			this.active = false;
			throw new IOException();
		}
		
		ClientManager.init();
	}
	
	/**
	 * Tries connecting through game code.
	 *
	 * @param {String} code - the code
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */
	public void connect(String code) throws IOException, IllegalArgumentException, ArrayIndexOutOfBoundsException {
		// decodes the game code and splits it into three parts: internal ip, external ip, port number
		String[] decoded = new String(Base64.getDecoder().decode(code)).split("~");		
		
		// gets the port number
		int port = Integer.parseInt(decoded[2]);
		
		// declares variables
		String host;
		InetAddress internal;
		
		try {
			// checks if it can connect to the internal ip
			internal = InetAddress.getByName(decoded[0]); 
			if (internal.isReachable(1000)) host = decoded[0];
			
			// if it can't then it sets it to the external ip
			else host = decoded[1];
		} 
		
		catch (IOException e) {
			// if the internal ip is wrong, it sets it to the external ip
			host = decoded[1];
		}
		
		// tries to connect with this ip and port number
		connect(host, port);
	}
	
	/**
	 * Runs the client thread.
	 */
	@Override
	public void run() {
		this.active = true;
		
		// while this thread is active, it updates the client
		while (this.active) {
			Thread.yield();
			try { Thread.sleep(1); }
			catch (Exception e) {  }

			while (this.ticks > 0) {
				this.updateClientInput();
				this.ticks--;
			}
		}
	}

	/**
	 * Updates client input.
	 */
	public void updateClientInput() {
		if (this.input == null) return;
		this.input.updateMousePosition();
		ClientSend.playerInput();
	}

	/**
	 * Checks if this client is connected.
	 *
	 * @return {boolean} true, if is connected
	 */
	public boolean isConnected() {
		return this.active;
	}

	/**
	 * Disconnects the client from the server.
	 */
	public void disconnect() {
		if (!this.active) return;
		
		// sends the disconnect packet
		ClientSend.clientDisconnect();
		instance = null;
		
		// stops all threads
		this.active = false;
		this.tcp.running = false;
		this.udp.running = false;
		
		// sets the input manager to null
		this.input = null;
		
		// destroys the player object then sets it to null
		this.player.destroy();
		this.player = null;

		try {
			// closes the tcp in/out streams
			this.tcp.in.close();
			this.tcp.out.close();
			
			// closes the sockets
			this.tcp.socket.close();
			this.udp.socket.close();
		}

		catch (IOException | NullPointerException e) { }
		
		// sets the in/out streams to null
		this.tcp.in = null;
		this.tcp.out = null;
		
		// sets the sockets to null
		this.tcp.socket = null;
		this.udp.socket = null;
		
		// sets the tcp/udp to null
		this.tcp = null;
		this.udp = null;
		
		// resets the client manager
		ClientManager.reset();
		System.out.println("[CLIENT] Client disconnected");
	}

	/**
	 * The Class TCP.
	 */
	protected class TCP implements Runnable {

		/** The socket. */
		public Socket socket;

		/** The input stream. */
		public BufferedInputStream in;

		/** The output stream. */
		public BufferedOutputStream out;

		/** If this thread is running. */
		public volatile boolean running = false;

		/**
		 * Instantiates a new tcp.
		 *
		 * @param {String} host - the host
		 * @param {int} port - the port
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public TCP(String host, int port) throws IOException {
			// creates a new socket to connect with
			this.socket = new Socket(host, port);
			
			// gets the input stream
			this.in = new BufferedInputStream(
						this.socket.getInputStream()
			);
			
			// gets the output stream
			this.out = new BufferedOutputStream(
						this.socket.getOutputStream()
			);
		}

		/**
		 * Runs the thread.
		 */
		@Override
		public void run() {
			this.running = true;

			while (this.running) {
				try {
					// blocks till there's a buffer to read
					byte[] buffer = new byte[Packet.BUFFER_ALLOCATE];
					
					// reads the buffer and then handles it
					in.read(buffer);
					ClientHandle.handle(buffer);
				}

				catch (IOException e) {
					// if there's an exception, that means the tcp is disconnected
					System.out.println("[CLIENT] TCP disconnected. ");
				}
			}
		}
	}

	/**
	 * The Class UDP.
	 */
	protected class UDP implements Runnable {

		/** The udp socket. */
		public DatagramSocket socket;

		/** If the thread is running. */
		public volatile boolean running = false;

		/**
		 * Instantiates a new udp.
		 *
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public UDP() throws IOException {
			this.socket = new DatagramSocket();
		}

		/**
		 * Run.
		 */
		@Override
		public void run() {
			this.running = true;

			while (this.running) {
				try {
					// receives and udp packets from the server
					byte[] buffer = new byte[Packet.BUFFER_ALLOCATE];
					DatagramPacket datagramPacket = new DatagramPacket(
							buffer, Packet.BUFFER_ALLOCATE
					);
					
					// handles the packet if when received
					this.socket.receive(datagramPacket);
					ClientHandle.handle(buffer);
				}

				catch (IOException e) {
					// if theres an exception the udp has been disconnected
					System.out.println("[CLIENT] UDP disconnected. ");
				}
			}
		}
	}
}
