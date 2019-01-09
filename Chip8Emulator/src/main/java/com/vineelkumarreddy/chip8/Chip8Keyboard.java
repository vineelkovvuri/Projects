/*
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.vineelkumarreddy.chip8;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Vineel Kumar Reddy Kovvuri
 * @version 1.0
 * @date 19-03-2016
 */
public class Chip8Keyboard implements KeyListener {
    // This keeps the state of the chip 8 keyboard's 15 keys
    private boolean keysPressed[] = new boolean[0x10];

    public boolean isChip8keyPressed(int key) {
        return keysPressed[key];
    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyReleased(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();
        if (Character.isDigit(key))
            keysPressed['0' - key] = true;
        else if (key >= 'a' && key <= 'f')
            keysPressed[9 + ('a' - key)] = true;
        else if (key >= 'A' && key <= 'F')
            keysPressed[9 + ('A' - key)] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        char key = e.getKeyChar();
        if (Character.isDigit(key))
            keysPressed['0' - key] = false;
        else if (key >= 'a' && key <= 'f')
            keysPressed[9 + ('a' - key)] = false;
        else if (key >= 'A' && key <= 'F')
            keysPressed[9 + ('A' - key)] = false;
    }
}
