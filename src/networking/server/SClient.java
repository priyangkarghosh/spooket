package networking.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import gameobjects.SPlayer;
import networking.Packet;

/**
 * The Class SClient (server-side client).
 * 
 * @author priyangkar ghosh
 */
public class SClient implements Runnable {

	/** The socket this client is connected to. */
	private Socket socket;

	/** The input and output streams. */
	protected BufferedInputStream in;
	protected BufferedOutputStream out;

	/** The address and port it is connected to. */
	protected InetAddress address;
	protected int port;

	/**  The id and name of the client. */
	private int id;	
	private String name;

	/** The player. */
	public SPlayer player;

	/** If this client thread is running. */
	private boolean running = false;

	/**
	 * Instantiates a new server-side client.
	 *
	 * @param {int} id - the id
	 * @param {Socket} socket - the socket
	 */
	public SClient(int id, Socket socket) {
		this.id = id;
		this.socket = socket;
		this.address = this.socket.getInetAddress();

		try {
			// initializes input and output streams
            this.out = new BufferedOutputStream(socket.getOutputStream());
            this.in = new BufferedInputStream(socket.getInputStream());
		}

		catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
	}

	/**
	 * Runs the client.
	 */
	@Override
	public void run() {
		this.running = true;
		
		// admits the client to the server
		ServerSend.admit(this.id, "Welcome to the server.");
		
		// constantly receives tcp data from the client
		while (this.running) tcpReceive();
	}

	/**
	 * Closes/disconnects this client.
	 */
	public void close() {
		// stops running the thread
		this.running = false;

		try {
			// closes the input/output streams
			this.in.close();
			this.out.close();
			
			// closes the socket
			this.socket.close();
		}

		catch (IOException e) { }
		
		// sets all of them to null
		this.in = null;
		this.out = null;
		this.socket = null;
		
		// removes the body from the world
		ServerManager.world.removeBody(this.player.body);
		
		// destroys the player game object
		this.player.destroy();
		this.player = null;
	}
	
	/**
	 * Recieves tcp data.
	 */
	private void tcpReceive() {
		try {
			// initializes the buffer
			byte[] buffer = new byte[Packet.BUFFER_ALLOCATE];
			
			// blocks until it gets some data
			in.read(buffer);
			
			// handles the data
			ServerHandle.handle(this.id, buffer);
		}

		catch (IOException e) { }
	}
	
	/**
	 * Gets the id of this client.
	 *
	 * @return {int} the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Gets the name of this client.
	 *
	 * @return {String} the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this client.
	 *
	 * @param {String} name - the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
