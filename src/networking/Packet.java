package networking;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import framework.common.Transform;
import framework.math.Vector;

/**
 * The Class Packet, used as a format for sending/receiving TCP and UDP data.
 * 
 * @author priyangkar ghosh
 */
public class Packet {
	/** The Constant BUFFER_ALLOCATE. */
	public static final int BUFFER_ALLOCATE = 1024;

	/**
	 * Packets sent from server to client.
	 */
	public static enum Server {
		/** The admit. */
		ADMIT,

		/** The validate. */
		VALIDATE,

		/** The player connected. */
		PLAYER_CONNECTED,

		/** The player disconnected. */
		PLAYER_DISCONNECTED,

		/** The player information. */
		PLAYER_INFORMATION,

		/** Bullet shot. */
		SHOT_ADDED,

		/** Bullet removed. */
		SHOT_REMOVED,

		/** Bullet information. */
		SHOT_INFORMATION,

		/** Player killed packet. */
		KILLED_PLAYER,

		/**Server stopped packet. */
		SERVER_STOPPED,
		
		/** Asteroid information packet. */
		ASTEROID_INFORMATION,
	}

	/**
	 * Packets sent from client to server.
	 */
	public static enum Client {

		/** The admission received. */
		ADMISSION_RECEIVED,

		/** The client disconnected. */
		CLIENT_DISCONNECTED,

		/** The player input. */
		PLAYER_INPUT,
	}

	/** The buffer of the packet. */
	protected ByteBuffer buffer;

	/**
	 * Instantiates a new packet.
	 */
	public Packet() {
		this.buffer = ByteBuffer.allocate(BUFFER_ALLOCATE);
	}

	/**
	 * Instantiates a new packet.
	 *
	 * @param {Client} type - the type
	 */
	public Packet(Client type) {
		this.buffer = ByteBuffer.allocate(BUFFER_ALLOCATE);
		write(type.ordinal());
	}

	/**
	 * Instantiates a new packet.
	 *
	 * @param {Server} type - the type
	 */
	public Packet(Server type) {
		this.buffer = ByteBuffer.allocate(BUFFER_ALLOCATE);
		write(type.ordinal());
	}

	/**
	 * Instantiates a new packet with specified data.
	 *
	 * @param {byte[]} data - the data
	 */
	public Packet(byte[] data) {
		this.buffer = ByteBuffer.wrap(data);
	}

	/**
	 * Write a boolean value.
	 *
	 * @param {boolean} value - the value
	 */
	public void write(boolean value) {
		buffer.put((byte)(value ? 1 : 0));
	}

	/**
	 * Write an int value.
	 *
	 * @param {int} value - the value
	 */
	public void write(int value) {
		buffer.putInt(value);
	}

	/**
	 * Write a double value.
	 *
	 * @param {double} value - the value
	 */
	public void write(double value) {
		buffer.putDouble(value);
	}

	/**
	 * Write a vector value.
	 *
	 * @param {Vector} value - the value
	 */
	public void write(Vector value) {
		buffer.putDouble(value.x);
		buffer.putDouble(value.y);
	}

	/**
	 * Writes a byte array.
	 *
	 * @param {byte[]} value - the value
	 */
	public void write(byte[] value) {
		buffer.put(value);
	}

	/**
	 * Writes a string.
	 *
	 * @param {String} value - the value
	 */
	public void write(String value) {
		byte[] b_value = value.getBytes(StandardCharsets.UTF_8);
		write(b_value.length);
		write(b_value);
	}

	/**
	 * Writes a transform.
	 *
	 * @param {Transform} transform - the transform
	 */
	public void write(Transform transform) {
		write(transform.position);
		write(transform.rotation);
		write(transform.scale);
	}

	/**
	 * Read boolean.
	 *
	 * @return the boolean value
	 */
	public boolean readBoolean() {
		return buffer.get() == 1;
	}

	/**
	 * Read int.
	 *
	 * @return {int} the int value
	 */
	public int readInt() {
		return buffer.getInt();
	}

	/**
	 * Read double.
	 *
	 * @return {double} the double value
	 */
	public double readDouble() {
		return buffer.getDouble();
	}

	/**
	 * Read vector.
	 *
	 * @return {Vector} the vector value
	 */
	public Vector readVector() {
		return new Vector(readDouble(), readDouble());
	}

	/**
	 * Read string.
	 *
	 * @return {String} the string
	 */
	public String readString() {
		byte[] b_string = new byte[readInt()];
		buffer.get(b_string);
		return new String(b_string, StandardCharsets.UTF_8);
	}

	/**
	 * Read transform.
	 *
	 * @return {Transform} the transform
	 */
	public Transform readTransform() {
		return new Transform(readVector(), readDouble(), readVector());
	}

	/**
	 * Gets the buffer.
	 *
	 * @return {byte[]} the buffer array
	 */
	public byte[] getBuffer() {
		return buffer.array();
	}
}
