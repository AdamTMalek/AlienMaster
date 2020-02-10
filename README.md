# AlienMaster
The AlienMaster program is developed for the purpose of controlling the Alien-Robot
by communicating with the MBED microcontroller that is inside the robot via a virtual
serial port.

*The project is a part of the System Project, 3rd year course*

## Installation
### Requirements
- Gradle version 6.1
- JDK 11 (only for development, JARs can run on Java 8)

### Setup
0. If you have an older JDK installed, ensure Gradle uses JDK 11 (required by JavaFX plugin)
or the project will not compile.
   
    To tell Gradle which JDK to use, refer to the 1st step in this 
    [SO answer](https://stackoverflow.com/a/21212790)
    which makes changes globally, or, for runtime only config, follow the instructions 
    [here](https://stackoverflow.com/a/21212790).
1. **Install Gradle wrapper**

    ```
   $ gradle wrapper --gradle-version 6.1 --distribution-type all
   ```
2. **Build the project**
    1. Using Gradle wrapper
        ```
        $ ./gradlew build
        ```
    2. Using an IntelliJ IDEA, the IDE should detect gradle so all you need to do is to build
    the project as you would normally do.
3. **Create the database**
    ```
    $ ./gradlew flyWayMigrate
    ```
4. **Run the project**
    1. Using Gradle wrapper
       ```
       $ ./gradlew run
       ```
    2. Using the IDE (instructions for IntelliJ IDEA)
        1. Go into Run and then click Open Configurations
        2. Click the + in the top left corner of the window, add new Gradle configuration
        3. Give the configuration a name, this can be, for example, `AlienMaster [run]`.
        4. Choose the Gradle project by using the directory icon on the right of the input.
        5. Type `run` in the tasks, click Apply and then OK.
        6. Now, you can run the project using the green arrow or using the assigned shortcut.
        