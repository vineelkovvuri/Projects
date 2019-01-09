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

import java.nio.file.Paths;

/**
 * @author Vineel Kumar Reddy Kovvuri
 * @version 1.0
 * @date 19-03-2016
 */
public class Main {

    // I want to keep this program simple.
    // Change the program file path accordingly
    // before you run
    public static void main(String... args) {

        // Init peripherals
        Chip8Keyboard keyboard = new Chip8Keyboard();
        Chip8Display display = new Chip8Display(keyboard);
        Chip8Memory ram = new Chip8Memory();

        // Init CPU
        Chip8CPU cpu = new Chip8CPU(ram, display, keyboard);

        // Load program to RAM
        ram.loadProgram(Paths.get("/home/vineel/Downloads/chip8/CHIP8/GAMES/IBM"));

        // Run CPU
        cpu.run();
    }
}
