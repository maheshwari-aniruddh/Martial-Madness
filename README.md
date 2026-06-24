# Martial Madness 
This game is about surviving(not hurting) the enemy until his health or the timer runs out. It's about self denseve. Each mvoe that you do can either block the enemy's attack or push him away. You can select between 3 characters. The default, the ninja and the sumo. Each player has its own advantaged and weaknesses.
---
Why I made it:
- I really like the og street fighter and i always wanted to code it for myself. However to make it different, i realized that its isnt always about hurting the enemy but rather more about protecting yourself so i decided to make it more self defense focused.

**Note** Here is the demo url since hackclub doesn't allow me to post a cdn link of the demo. 
https://cdn.hackclub.com/019ea2c6-baa3-766b-82ee-7daf02664bff/Screen%20Recording%202026-06-03%20at%209.31.22%E2%80%AFPM.mov

## How to Play 
- Use the tutorial for more detailed informaiotn about how to play the game.
space to restart
D to block
F to punch
V to kick
R to uppercut
E to roundhouse kick
---

## How to Run the Game

### Option 1: Windows .exe (recommended — no terminal needed)
1. Download the executable directly: **[MartialMadness.exe](https://github.com/maheshwari-aniruddh/Martial-Madness/releases/download/v1.1.0/MartialMadness.exe)**
2. Once downloaded, double-click `MartialMadness.exe` to launch the game.
3. **Requirement:** you need Java 8 or newer installed on your computer. The `.exe` is a native Windows launcher that runs the game through your installed Java runtime — it is not a fully standalone binary, so Java must be present on the machine.
   - To check if you already have Java, open Command Prompt and run `java -version`.
   - If you don't have Java, download it free from [java.com/download](https://www.java.com/download/) and install it. No restart is required — the `.exe` will detect Java automatically once it's installed.
   - **Java 21 is strongly recommended** — animations and combat timing can look off on older Java runtimes (e.g. Java 8).
4. **Windows SmartScreen / antivirus note:** since this `.exe` isn't code-signed, Windows may show a "Windows protected your PC" warning on first launch. Click **More info → Run anyway** to proceed. This is expected for unsigned indie executables and is not a sign of malware.

### Option 2: Cross-platform .jar (Windows, Mac, Linux)
1. Go to the [Releases Page](https://github.com/maheshwari-aniruddh/Martial-Madness/releases) and download **`MartialMadness.jar`** from the latest release.
2. Make sure you have Java 8+ installed (`java -version` in a terminal to check).
3. Run it:
   - Double-click the `.jar` file, **or**
   - Open a terminal in the folder containing it and run:
     ```bash
     java -jar MartialMadness.jar
     ```

### Option 3: Compile from Source Code
If you want to run the raw source code files:
1. Compile the code:
   ```bash
   javac *.java
   ```
2. Run the game:
   ```bash
   java MartialMadness
   ```

