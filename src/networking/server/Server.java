package networking.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Base64;

import game.PlayerPrefs;
import networking.Packet;

/**
 * The Class Server singleton.
 * 
 * @author priyangkar ghosh
 */
public class Server implements Runnable {

	/** The Constant MAX_PLAYERS. */
	public static final int MAX_PLAYERS = 10;
	
	/** The code and port number. */
	private String code; 
	private int port;

	/** The tcp and udp connections. */
	protected TCP tcp;	
	protected UDP udp;

	/** The outstanding ticks. */
	private int ticks = 0;

	/** The active. */
	private boolean active = false;

	/** The clients and the server manager. */
	private SClient[] clients = new SClient[MAX_PLAYERS];	
	public ServerManager manager = new ServerManager();

	/** The instance. */
	private volatile static Server instance;

	/**
	 * singleton driver method.
	 *
	 * @return {Server} the server instance
	 */
	public synchronized static Server instance() {
		if (instance == null) instance = new Server();
		return instance;
	}

	/**
	 * Instantiates a new server.
	 */
	private Server() { }

	/**
	 * Starts the server.
	 */
	public void start() {
		System.out.println("[SERVER] Started new server. ");
		
		// gets a port if the user specified one
		this.tcp = new TCP(PlayerPrefs.get("port", 0));
		
		// gets the port from the tcp socket
		this.port = this.tcp.socket.getLocalPort();
		
		// creates a udp socket on this port
		this.udp = new UDP(this.port);
		
		// gets the code, if it can't get a valid code the server is stopped
		try { this.code = setCode(); } 
		
		catch (IOException e) {
			System.out.println("[SERVER] Server could not be started succesfully. ");
			this.stop();
			return;
		}
		
		// starts the server, server tcp, and server udp threads
		new Thread(this).start();
		new Thread(tcp).start();
		new Thread(udp).start();
		
		// initializes the server manager class for this server instance
		ServerManager.init();
	}

	/**
	 * Ticks the server.
	 */
	public void tick() {
		if (this.active) this.ticks++;
	}

	/**
	 * Runs this thread.
	 */
	@Override
	public void run() {
		// starts running this thread
		this.active = true;
		System.out.printf("[SERVER] Server listening on port %d.\n", this.port);
		System.out.println("[SERVER] Code: " + this.code);
		
		while (this.active) {
			Thread.yield();
			try { Thread.sleep(1); }
			catch (Exception e) {  }

			while (this.ticks > 0) {
				// ticks the server
				ServerManager.tick();
				this.ticks--;
			}
		}
	}

	/**
	 * Stops the server.
	 */
	public void stop() {
		if (!this.active) return;
		
		// sends the server stopped packet to all the clients
		ServerSend.serverStopped();
		Server.instance = null;
		
		// stops running all the threads
		this.active = false;
		this.tcp.running = false;
		this.udp.running = false;

		try {
			// closes this servers open sockets
			this.tcp.socket.close();
			this.udp.socket.close();
		}
		catch (IOException e) { }
		
		// removes all the clients
		for (int i = 0; i < Server.MAX_PLAYERS; i++)
			removeClient(i);
		
		// makes the sockets null
		this.tcp.socket = null;
		this.udp.socket = null;
		
		// makes the tcp and udp classes null
		this.tcp = null;
		this.udp = null;
		
		// resets the server manager
		ServerManager.reset();
		System.out.println("[SERVER] Server stopped.");
	}

	/**
	 * Uses sequential search to find first available client id.
	 *
	 * @param {Socket} socket - the socket
	 * @return {boolean} true, if the client was added successfully
	 */
	public boolean addClient(Socket socket) {
		for (int i = 0; i < Server.MAX_PLAYERS; i++)
			if (this.clients[i] == null) {
				// adds this client if its null
				this.clients[i] = new SClient(i, socket);
				new Thread(clients[i]).start();
				return true;
			}
		return false;
	}

	/**
	 * Removes the client.
	 *
	 * @param {int} id - the id
	 */
	public void removeClient(int id) {
		if (this.clients[id] == null) return;
		this.clients[id].close();
		this.clients[id] = null;
	}

	/**
	 * Gets the client.
	 *
	 * @param {int} id - the id
	 * @return {SClient} the client
	 */
	public SClient getClient(int id) {
		if (id >= MAX_PLAYERS || id < 0) return null;
		return this.clients[id];
	}

	/**
	 * Gets the port.
	 *
	 * @return {int} the port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * The Server TCP class.
	 */
	protected class TCP implements Runnable {

		/** The server socket. */
		public ServerSocket socket;

		/** The running. */
		private boolean running = false;

		/**
		 * Instantiates a new tcp server socket.
		 */
		public TCP() {
			// creates a new server socket
			try { this.socket = new ServerSocket(0); }
			catch (IOException e) { }
		}

		/**
		 * Instantiates a new tcp on a specific port.
		 *
		 * @param {int} port - the port
		 */
		public TCP(int port) {
			// creates a new server socket
			try { this.socket = new ServerSocket(port); }
			catch (IOException e) { }
		}

		/**
		 * Runs the TCP thread.
		 */
		@Override
		public void run() {
			this.running = true;

			while (this.running) {
				try {
					// blocks until a connection is received
					Socket socket = this.socket.accept();
					System.out.println("[SERVER] Incoming connection from {" + socket.getInetAddress() + "}...");
					
					// adds the client
					if (Server.instance().addClient(socket)) continue;
					
					// if the client wasn't connected the socket is closed
					System.out.println("[SERVER] Client could not connect.");
					socket.close();
				}

				catch (IOException e) { }
			}
		}
	}

	/**
	 * The Class server UDP class.
	 */
	protected class UDP implements Runnable {

		/** The server udp socket. */
		public DatagramSocket socket;

		/** The running. */
		private boolean running = false;

		/**
		 * Instantiates a new udp.
		 */
		public UDP() {
			try { this.socket = new DatagramSocket(); }
			catch (IOException e) { }
		}

		/**
		 * Instantiates a new udp on a specific port.
		 *
		 * @param {int} port - the port
		 */
		public UDP(int port) {
			try { this.socket = new DatagramSocket(port); }
			catch (IOException e) { }
		}

		/**
		 * Runs the thread.
		 */
		@Override
		public void run() {
			this.running = true;

			while (this.running) {
				try {
					// allocates the buffer
					byte[] buffer = new byte[Packet.BUFFER_ALLOCATE];
					DatagramPacket datagramPacket = new DatagramPacket(
							buffer, Packet.BUFFER_ALLOCATE
					);
					
					// receives the udp packet
					this.socket.receive(datagramPacket);
					
					// handles the packet
					ServerHandle.handle(-1, buffer);
				}

				catch (IOException e) { }
			}
		}
	}

	/**
	 * If the server is running.
	 *
	 * @return {boolean} true, if is running
	 */
	public boolean isRunning() {
		return this.active;
	}
	
	/**
	 * Gets the internal ip address.
	 *
	 * @return {String} the internal ip address
	 * @throws UnknownHostException the unknown host exception
	 */
	// gets the internal / local ip address
	private String getInternalIpAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
	
	/**
	 * Gets the external/public ip address.
	 *
	 * @return {String} the external ip address
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String getExternalIpAddress() throws IOException {		
		URL url = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		return in.readLine();	
	}
	
	/**
	 * Gets the game code.
	 *
	 * @return {String} sets/updates the game code string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String setCode() throws IOException {
		String internal = getInternalIpAddress();
		String external = getExternalIpAddress();
		return Base64.getEncoder().encodeToString(
				(internal + "~" + external + "~" + port).getBytes()
		);
	}

	/**
	 * Returns the code.
	 *
	 * @return {String} code
	 */
	public String getCode() {
		return this.code;
	}
}
