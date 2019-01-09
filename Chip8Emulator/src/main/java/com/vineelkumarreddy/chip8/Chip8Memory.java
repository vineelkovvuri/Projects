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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Vineel Kumar Reddy Kovvuri
 * @version 1.0
 * @date 19-03-2016
 */
public class Chip8Memory {
    private int ram[] = new int[0x1000];
    private final int DEFAULT_LOAD_ADDRESS = 0x200;

    // This may not be used by all programs
    int spritHexChars[] = {
        0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
        0x20, 0x60, 0x20, 0x20, 0x70, // 1
        0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
        0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
        0x90, 0x90, 0xF0, 0x10, 0x10, // 4
        0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
        0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
        0xF0, 0x10, 0x20, 0x40, 0x40, // 7
        0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
        0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
        0xF0, 0x90, 0xF0, 0x90, 0x90, // A
        0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
        0xF0, 0x80, 0x80, 0x80, 0xF0, // C
        0xE0, 0x90, 0x90, 0x90, 0xE0, // D
        0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
        0xF0, 0x80, 0xF0, 0x80, 0x80 // F
    };

    public Chip8Memory() {
        for (int i = 0; i < ram.length; i++)
            ram[i] = 0;

        // Load sprits to the lower portion of the chip8 RAM
        for (int i = 0; i < spritHexChars.length; i++)
            ram[i] = spritHexChars[i];
    }

    public int getWord(int index) {
        return (ram[index] << 8) | (ram[index + 1] & 0xff);
    }

    public int getByte(int index) {
        return ram[index] & 0xff;
    }

    public void setByte(int index, int value) {
        ram[index] = value & 0xff;
    }

    public void loadProgram(Path path) {
        try (InputStream reader = Files.newInputStream(path)) {
            int _byte = 0;
            for (int i = DEFAULT_LOAD_ADDRESS; (_byte = reader.read()) > -1; i++)
                ram[i] = _byte & 0xff;
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void dumpRAM() {
        for (int i = 0x200; i <= 0xfff; i += 16) {
            System.out.printf("%08x: ", i);
            for (int j = 0; j < 16; j += 2) {
                System.out.printf("%02x%02x ", ram[i + j], ram[i + j + 1]);
            }
            System.out.println("");
        }
    }
}
