# Accompaniment Generator
This project generates Accompaniment (music chords) for a given MIDI file using a customized Genetic Algorithm while building
upon the functionalities provided by the Java music Library JFugue.
## Project's Structure
The project can be divided into 4 main parts:
1. Chords: This folder is mainly the bridge between JFugue library and the Genetic algorithm components as it represents 
the theoretical musical components such as chords, keys, scales and wraps them in customized object that can be operated on
the algorithm:
    * ChordObject: A custom class storing all the relevant attributes of a chord to numerically express its 'fitness' suitability.
    * ChordReproduction: a class responsible for creating new chords: the individuals of the new generation
    * Evolution: This class executes the entire evolution process: starting from the initial random generation (set of chords) 
to creating the final fit (harmonic) generation: representing our accompaniment for the piece of music
2. MidiMusicUtilities: This folder contains classes capable for extracting theoretical music properties from the midi file. 
The output is used to create adequate ChordObject instances representing the individuals in the genetic algorithm 
3. AccompanimentGenerator: This folder contains classes two main classes:
   * FilePlayer: reading the file as a string and playing it in MP4 format
   * Generator: puts the different components together to create a new file with decent accompaniment out of the original file.
4. Main file: the final program to be run either through command line or by input through the console.