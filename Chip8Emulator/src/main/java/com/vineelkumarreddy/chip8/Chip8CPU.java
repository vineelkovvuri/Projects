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

import java.awt.Toolkit;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

/**
 * @author Vineel Kumar Reddy Kovvuri
 * @version 1.0
 * @date 19-03-2016
 */
public class Chip8CPU {
    private int V[] = new int[16]; //8bit each
    private int I;  //16bit
    private int DT; //8bit
    private int ST; //8bit
    private int PC; //16bit program counter
    private int SP; //8bit stack pointer
    private int stack[] = new int[16]; //16bit each

    private final Chip8Display display;
    private final Chip8Keyboard keyboard;
    private final Chip8Memory ram;

    private final int DT_FREQUENCY = 60; //60 hz
    private final int ST_FREQUENCY = 60; //60 hz
    private final int HZ = 1000; // 60hz => 60 times in 1sec or in 1000 milliseconds
    Random random = new Random();

    public Chip8CPU(Chip8Memory ram, Chip8Display display, Chip8Keyboard keyboard) {
        this.ram = ram;
        this.display = display;
        this.keyboard = keyboard;

        // This is the default load address of the program for Chip8 CPU
        PC = 0x200;
    }

    private void delay(int nmillis) {
        try {
            Thread.sleep(nmillis);
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void printInstructionDecodeCycle(int n) {
        System.out.printf("%x%n", n);
    }

    public void run() {
        display.setVisible(true);
        //ram.dumpRAM();
        setupAndStartTimers();
        int previousInstruction = 0xffff;
        while (true) {
            int instruction = ram.getWord(PC);
            if (detectBusyWaitInfiniteLoop(instruction, previousInstruction))
                break;
            incrementPC(); //fetched the instruction so move on
            int opcode = (instruction >> 12) & 0xf;
            switch (opcode) {
                case 0:
                    decode0(instruction);
                    printInstructionDecodeCycle(0);
                    break;
                case 1:
                    decode1(instruction);
                    printInstructionDecodeCycle(1);
                    break;
                case 2:
                    decode2(instruction);
                    printInstructionDecodeCycle(2);
                    break;
                case 3:
                    decode3(instruction);
                    printInstructionDecodeCycle(3);
                    break;
                case 4:
                    decode4(instruction);
                    printInstructionDecodeCycle(4);
                    break;
                case 5:
                    decode5(instruction);
                    printInstructionDecodeCycle(5);
                    break;
                case 6:
                    decode6(instruction);
                    printInstructionDecodeCycle(6);
                    break;
                case 7:
                    decode7(instruction);
                    printInstructionDecodeCycle(7);
                    break;
                case 8:
                    decode8(instruction);
                    printInstructionDecodeCycle(8);
                    break;
                case 9:
                    decode9(instruction);
                    printInstructionDecodeCycle(9);
                    break;
                case 0xA:
                    decodeA(instruction);
                    printInstructionDecodeCycle(0xA);
                    break;
                case 0xB:
                    decodeB(instruction);
                    printInstructionDecodeCycle(0xB);
                    break;
                case 0xC:
                    decodeC(instruction);
                    printInstructionDecodeCycle(0xC);
                    break;
                case 0xD:
                    decodeD(instruction);
                    printInstructionDecodeCycle(0xD);
                    break;
                case 0xE:
                    decodeE(instruction);
                    printInstructionDecodeCycle(0xE);
                    break;
                case 0xF:
                    decodeF(instruction);
                    printInstructionDecodeCycle(0xF);
                    break;
            }
            previousInstruction = instruction;
        }
    }

    private void incrementPC() {
        PC += 2;
    }

    private void decode0(int instruction) {
        int opcodeArgs = instruction & 0xfff;
        switch (opcodeArgs & 0xff) {
            case 0xE0: // 00E0 - CLS
                display.clear();
                break;
            case 0xEE: // 00EE - RET
                PC = stack[--SP];
                break;
            default: //0nnn - SYS addr // not used.
                break;
        }
    }

    private void decode1(int instruction) {
        int opcodeArgs = instruction & 0xfff;
        PC = opcodeArgs; //1nnn - JP addr
    }

    private void decode2(int instruction) {
        int opcodeArgs = instruction & 0xfff;
        stack[SP] = PC;
        ++SP;
        PC = opcodeArgs;  //2nnn - CALL addr
    }

    private void decode3(int instruction) {
        int reg = (instruction >> 8) & 0xf;
        int value = instruction & 0xff;
        if (V[reg] == value) {
            incrementPC();  //3xkk - SE Vx, byte
        }
    }

    private void decode4(int instruction) {
        int reg = (instruction >> 8) & 0xf;
        int value = instruction & 0xff;
        if (V[reg] != value) {
            incrementPC();  //4xkk - SNE Vx, byte
        }
    }

    private void decode5(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        if (V[regX] == V[regY]) {
            incrementPC();  //5xy0 - SE Vx, Vy
        }
    }

    private void decode6(int instruction) {
        int reg = (instruction >> 8) & 0xf;
        int value = instruction & 0xff;
        V[reg] = value; //6xkk - LD Vx, byte
    }

    private void decode7(int instruction) {
        int reg = (instruction >> 8) & 0xf;
        int value = (instruction & 0xff) + V[reg];
        //7xkk - ADD Vx, byte
        if (value > 255)
            value -= 256;
        V[reg] = value;
    }

    private void decode8(int instruction) {
        int firstNibble = instruction & 0xf;
        switch (firstNibble) {
            case 0:
                decode8_0(instruction);
                break;
            case 1:
                decode8_1(instruction);
                break;
            case 2:
                decode8_2(instruction);
                break;
            case 3:
                decode8_3(instruction);
                break;
            case 4:
                decode8_4(instruction);
                break;
            case 5:
                decode8_5(instruction);
                break;
            case 6:
                decode8_6(instruction);
                break;
            case 7:
                decode8_7(instruction);
                break;
            case 0xE:
                decode8_E(instruction);
                break;
        }
    }

    private void decode8_0(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        V[regX] = V[regY]; // 8xy0 - LD Vx, Vy
    }

    private void decode8_1(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        V[regX] |= V[regY]; // 8xy1 - OR Vx, Vy
    }

    private void decode8_2(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        V[regX] &= V[regY]; // 8xy2 - AND Vx, Vy
    }

    private void decode8_3(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        V[regX] ^= V[regY]; // 8xy3 - XOR Vx, Vy
    }

    private void decode8_4(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        V[regX] += V[regY]; // 8xy4 - ADD Vx, Vy
        if (V[regX] > 255) {
            V[0xF] = 1;
            V[regX] -= 256;
        }
        else {
            V[0xF] = 0;
        }
    }

    private void decode8_5(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        if (V[regX] > V[regY])
            V[0xF] = 1;
        else
            V[0xF] = 0;
        V[regX] -= V[regY]; // 8xy5 - SUB Vx, Vy
        if (V[regX] < 0)
            V[regX] += 256;
    }

    private void decode8_6(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        V[0xF] = V[regX] & 0x1;
        V[regX] >>= 1; // 8xy6 - SHR Vx {, Vy} // https://en.wikipedia.org/wiki/CHIP-8#cite_note-shift-2
    }

    private void decode8_7(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        if (V[regX] < V[regY])
            V[0xF] = 1;
        else
            V[0xF] = 0;
        V[regX] = V[regY] - V[regX]; // 8xy7 - SUBN Vx, Vy
        if (V[regX] < 0)
            V[regX] += 256;
    }

    private void decode8_E(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        V[0xF] = (V[regX] >> 7) & 0x1; //Most significant bit
        V[regX] <<= 1; // 8xyE - SHL Vx {, Vy}
        if (V[regX] > 255)
            V[regX] -= 256;
    }

    private void decode9(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        if (V[regX] != V[regY]) {
            incrementPC(); //9xy0 - SNE Vx, Vy
        }
    }

    private void decodeA(int instruction) {
        int value = instruction & 0xfff;
        I = value; // Annn - LD I, addr
    }

    private void decodeB(int instruction) {
        int value = instruction & 0xfff;
        PC = V[0] + value; // Bnnn - JP V0, addr
    }

    private void decodeC(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int value = instruction & 0xff;
        int rnd = random.nextInt(255) & 0xff;
        V[regX] = rnd & value; // Cxkk - RND Vx, byte
    }

    /*
    Display is interesting and it deserves some explanation,
    The way the instruction works is, given a starting pixel (Vx, Vy).
    CPU first reads nibble number of bytes from the memory location
    pointer by I register. Now each bit in each byte corresponds
    to one pixel starting from (Vx, Vy). For example if jth bit
    of ith byte is 1 then the pixel at display[Vx + j][Vy + i] XOR
    with its previous value. This mechanism is also called
    Stripping in the reference.
     */
    private void decodeD(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int regY = (instruction >> 4) & 0xf;
        int height = (instruction >> 0) & 0xf;
        V[0xF] = 0;
        for (int i = 0; i < height; i++) {
            int spriteLine = ram.getByte(I + i);
            for (int j = 0; j < 8; j++) {
                if ((spriteLine & 0x80) > 0) {
                    if (display.setPixel(V[regX] + j, V[regY] + i)) {
                        V[0xF] = 1;
                    }
                }
                spriteLine <<= 1;
            }
        }
    }

    private void decodeE(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int subCode = instruction & 0xff;

        if (subCode == 0x9E && keyboard.isChip8keyPressed(V[regX]))
            incrementPC();
        else if (subCode == 0xA1 && !keyboard.isChip8keyPressed(V[regX]))
            incrementPC();
    }

    private void decodeF(int instruction) {
        int regX = (instruction >> 8) & 0xf;
        int subCode = instruction & 0xff;
        switch (subCode) {
            case 0x07: // Fx07 - LD Vx, DT
                V[regX] = DT;
                break;
            case 0x0A: // Fx0A - LD Vx, K
                //TODO: What is the better way to take input
                int key = Integer.parseInt(JOptionPane.showInputDialog("Enter key input:"), 16);
                V[regX] = key;
                break;
            case 0x15: // Fx15 - LD DT, Vx
                DT = V[regX];
                break;
            case 0x18: // Fx18 - LD ST, Vx
                ST = V[regX];
                break;
            case 0x1E: // Fx1E - ADD I, Vx
                I += V[regX];
                break;
            case 0x29: // Fx29 - LD F, Vx
                I = V[regX] * 5; // because we have 5 rows to present a single hex char as sprit in spritHexChars
                break;
            case 0x33: // Fx33 - LD B, Vx
                int n = V[regX];
                for (int i = 3; i > 0; i--) {
                    ram.setByte(I + i - 1, (n % 10));
                    n /= 10;
                }
                break;
            case 0x55: // Fx55 - LD [I], Vx
                for (int i = 0; i <= regX; i++)
                    ram.setByte(I + i, V[i] & 0xff);
                break;
            case 0x65: // Fx65 - LD Vx, [I]
                for (int i = 0; i <= regX; i++)
                    V[i] = ram.getByte(I + i);
                break;
        }
    }
    Timer delayTimer = new Timer("delayTimer");
    Timer soundTimer = new Timer("soundTimer");

    private void setupAndStartTimers() {
        delayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (DT > 0) {
                    DT--;
                    System.out.printf("DT %x %n", DT);
                }
            }
        }, 0, HZ / DT_FREQUENCY);

        soundTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (ST > 0) {
                    Toolkit.getDefaultToolkit().beep();
                    ST--;
                    System.out.printf("ST %x %n", ST);
                }
            }
        }, 0, HZ / ST_FREQUENCY);
    }

    // Detects jmp refering to same jmp address.
    // Similar to while(1); not while(1){...}
    private boolean detectBusyWaitInfiniteLoop(int instruction, int previousInstruction) {
        int currentOpcode = (instruction >> 12) & 0xf;
        int currentJumpAddress = (instruction >> 0) & 0xfff;

        int previousOpcode = (previousInstruction >> 12) & 0xf;
        int previousJumpAddress = (previousInstruction >> 0) & 0xfff;

        if (currentOpcode == previousOpcode && currentOpcode == 0x1) { // Jmp instruction
            return currentJumpAddress == previousJumpAddress;
        }
        return false;
    }
}
