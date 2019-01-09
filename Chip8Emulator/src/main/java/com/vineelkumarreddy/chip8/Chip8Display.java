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

import java.awt.Color;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Vineel Kumar Reddy Kovvuri
 * @version 1.0
 * @date 19-03-2016
 */
public class Chip8Display extends JFrame {
    private final int frameWidth = 1000, frameHeight = 800;

    //Chip8 display size
    private final int displayWidth = 64, displayHeight = 32;
    private JPanel display[][] = new JPanel[displayHeight][displayWidth];

    public Chip8Display(Chip8Keyboard keyboard) throws HeadlessException {
        setTitle("Chip8 Emulator 1.0 - Vineel");
        setSize(frameWidth, frameHeight);
        addKeyListener(keyboard);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(false);
        getContentPane().setLayout(null);
        for (int i = 0; i < displayHeight; i++) {
            for (int j = 0; j < displayWidth; j++) {
                display[i][j] = new JPanel();
                display[i][j].setBounds(100 + j * 12, 100 + i * 12, 12, 12);
                display[i][j].setBackground(Color.BLACK);
                this.getContentPane().add(display[i][j]);
            }
        }
        // clear();
    }

    public boolean setPixel(int x, int y) {
        if (x > displayWidth)
            x -= displayWidth;
        else if (x < 0)
            x += displayWidth;

        if (y > displayHeight)
            y -= displayHeight;
        else if (y < 0)
            y += displayHeight;

        Color bgcolor = display[y][x].getBackground();
        if (bgcolor == Color.GREEN) {
            display[y][x].setBackground(Color.BLACK);
            return true; //erased
        }
        else {
            display[y][x].setBackground(Color.GREEN);
            return false;
        }
    }

    public void clear() {
        for (int i = 0; i < 32; i++)
            for (int j = 0; j < 64; j++)
                display[i][j].setBackground(Color.BLACK);
    }
}
