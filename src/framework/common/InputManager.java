package framework.common;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import framework.math.Vector;
import game.Game;
import networking.Packet;

/**
 * Manages inputs for both client and server.
 * 
 * @author priyangkar ghosh
 */
public class InputManager {

	/**
	 * Holds controls and their corresponding keys.
	 */
	public static enum Controls {
		/** The shoot key. */
		SHOOT(MouseEvent.BUTTON1),

		/** The view leaderboard key. */
		VIEW_SCOREBOARD(KeyEvent.VK_E);

		/** key code. */
		private int code;

		/**
		 * Instantiates a new controls constant.
		 *
		 * @param {int} code - the code
		 */
		private Controls(int code) {
			this.code = code;
		}

		/**
		 * Equals.
		 *
		 * @param {int} other - the other
		 * @return true, if successful
		 */
		public boolean equals(int other) {
			return this.code == other;
		}
	}

	/** The mouse position. */
	private Vector mousePosition = Vector.zero();

	/** The input hash map. */
	@SuppressWarnings("serial")
	private HashMap<Controls, Boolean> input =
		new HashMap<>() { {
			for (Controls key : Controls.values())
				put(key, false);
		}
	};

	/**
	 * Updates mouse position, relative to the window.
	 *
	 */
	public void updateMousePosition() {
		Point screenPos = Game.getContentPane().getLocationOnScreen();
		Point relMousePos = MouseInfo.getPointerInfo().getLocation();
		this.mousePosition = new Vector(relMousePos.x - screenPos.x, relMousePos.y - screenPos.y);
	}

	/**
	 * Processes a mouse event.
	 *
	 * @param {MouseEvent} me - the mouse event
	 * @param {boolean} pressed - the pressed
	 */
	public void process(MouseEvent me, boolean pressed) {
		if (me.getButton() == Controls.SHOOT.code)
			this.input.put(Controls.SHOOT, pressed);
	}

	/**
	 * Processes a keyboard event.
	 *
	 * @param {KeyEvent} ke - the keyboard event
	 * @param {boolean} pressed - the pressed
	 */
	public void process(KeyEvent ke, boolean pressed) {
		int code = ke.getKeyCode();

		if (Controls.VIEW_SCOREBOARD.equals(code))
			this.input.put(Controls.VIEW_SCOREBOARD, pressed);
	}

	/**
	 * Processes a packet.
	 *
	 * @param {Packet} packet - the packet
	 */
	public void process(Packet packet) {
		for (Controls key : Controls.values())
			this.input.put(key, packet.readBoolean());
		this.mousePosition = packet.readVector();
	}

	/**
	 * Gets the input hash map.
	 *
	 * @return {HashMap} input
	 */
	public HashMap<Controls, Boolean> getInput() {
		return this.input;
	}

	/**
	 * Gets the input for a specified control.
	 *
	 * @param {Controls} controls - the controls
	 * @return {HashMap} input
	 */
	public boolean getInput(Controls controls) {
		return this.input.get(controls);
	}

	/**
	 * Gets the mouse position.
	 *
	 * @return {Vector} the mouse position
	 */
	public Vector getMousePosition() {
		return this.mousePosition;
	}

	/**
	 * Gets the mouse direction.
	 *
	 * @param {Vector} origin - the origin
	 * @return {Vector} the mouse direction
	 */
	public Vector getMouseDirection(Vector origin) {
		return this.mousePosition.subtract(origin).normalized();
	}
}