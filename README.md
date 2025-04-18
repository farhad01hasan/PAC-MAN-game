# PAC-MAN game
A simple recreation of the iconic PAC-MAN game.
This classic Pac-Man arcade game has been recreated using Java and Swing. The game includes a playable menu, food collection, ghost AI, teleportation, and a save/load system using the Preferences API.

Features

Menu with options to start a new game or continue from a saved game
Pac-Man movement using arrow keys or WASD
Ghosts move randomly and chase the player
Food collection and scoring system
Lives and game over screen
Teleporting across map edges
Save and load functionality (position, direction, score, food, lives)
High score tracking


Technologies Used

Java (JDK 8+)
Java Swing and AWT for GUI
Java Preferences API for save/load system


Project Structure

├── App.java                Main launcher class
├── PACMAN.java             Main game logic




Requirements to run :

Java JDK 8 or higher
An IDE like IntelliJ IDEA or Visual Studio Code


How to Run :
Clone or download this repository.
Make sure the image files (e.g., wall.png, pacmanRight.png, ghost images) are placed in the same directory or correct resource path.
Open the project in your IDE.
Run the App.java file.

Save and Load System :
The game automatically saves the following data when the window is closed:

Pac-Man’s position and direction
Current score and remaining lives
Food positions
High score

This feature enables us to play our unfinished game later on by clicking CONTINUE on main menu.


Interface : 
![image](https://github.com/user-attachments/assets/4b927089-0fef-47d9-b15b-659870b28a10)
![image](https://github.com/user-attachments/assets/b576fd68-92ac-4129-b0d8-0f98339db1da)
![image](https://github.com/user-attachments/assets/cdd4cc09-4184-422b-89a5-51cc2080ab9c)




Author
Name: Farhad Hasan Anik
mail address : farhad.anikh@gmail.com 

License
This project is developed for educational purposes. You are free to use or modify it with proper credit.

Acknowledgements
This project is inspired by the original Pac-Man game. All images and design assets used are for educational purposes only.

