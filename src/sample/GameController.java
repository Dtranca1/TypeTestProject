package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;



import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;



public class GameController implements Initializable {


    private int wordCounter = 0;
    private int first = 1;

    private File saveData;

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);


    @FXML
    public Text seconds;
    @FXML
    private Text wordsPerMin;
    @FXML
    private Text accuracy;
    @FXML
    private Text programWord;
    @FXML
    private Text secondProgramWord;

    @FXML
    private TextField userWord;

    @FXML
    private ImageView correct;
    @FXML
    private ImageView wrong;

    @FXML
    private Button playAgain;


    ArrayList<String> words = new ArrayList<>();

    // add words to array list
    public void addToList() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new
                    FileReader("wordsList"));
            String line = reader.readLine();
            while (line != null) {
                words.add(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toMainMenu(ActionEvent ae) throws IOException {
        Main m = new Main();
        m.changeScene("sample.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // calls for addToList and shuffles words and sets first word and second word while shuffling
        playAgain.setVisible(false);
        playAgain.setDisable(true);
        seconds.setText("60");
        addToList();
        Collections.shuffle(words);
        programWord.setText(words.get(wordCounter));
        secondProgramWord.setText(words.get(wordCounter + 1));
        wordCounter++;

        // creates file and saves data to data folder
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        saveData = new File("C:\\ProgrammingFinal\\TypeTestFinal\\src\\data\\" + formatter.format(date).strip() + ".txt");

        try {
            if (saveData.createNewFile()) {
                System.out.println("File created: " + saveData.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int timer = 60;
    Runnable r = new Runnable() {
        // runnable object r
        @Override
        public void run() {
            if (timer <= -1) {
                if (timer == -1) {
                    userWord.setDisable(true);
                    userWord.setText("Game over");
                    // writes data to data file
                    try {
                        FileWriter myWriter = new FileWriter(saveData);
                        myWriter.write(countAll + ";");
                        myWriter.write(counter + ";");
                        myWriter.write(String.valueOf(countAll-counter));
                        myWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                    // play again button timer
                if (timer == -4) {
                    playAgain.setVisible(true);
                    playAgain.setDisable(false);
                    executor.shutdown();
                }

            } else {
                seconds.setText(String.valueOf(timer));
            }
            timer -= 1;
        }
    };

    Runnable fadeCorrect = new Runnable() {
        // shows if word is correct
        @Override
        public void run() {
            correct.setOpacity(0);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            correct.setOpacity(50);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            correct.setOpacity(100);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            correct.setOpacity(0);

        }
    };

    Runnable fadeWrong = new Runnable() {
        // shows if word is wrong
        @Override
        public void run() {
            wrong.setOpacity(0);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wrong.setOpacity(50);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wrong.setOpacity(100);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wrong.setOpacity(0);
        }
    };

    private int countAll = 0;
    private int counter = 0;

    public void startGame(KeyEvent ke) {
        // only gets called once
        if (first == 1) {
            first = 0;
            executor.scheduleAtFixedRate(r, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
        }

        // Check for Enter key using the getText()
        if (ke.getText().equals("\r") || ke.getText().equals("\n")) {
            // Both \r and \n can represent Enter depending on platform

            String s = userWord.getText();
            String real = programWord.getText();
            countAll++;

            // If correct
            if (s.equals(real)) {
                counter++;
                wordsPerMin.setText(String.valueOf(counter));

                Thread t = new Thread(fadeCorrect);
                t.start();
            } else {
                Thread t = new Thread(fadeWrong);
                t.start();
            }
            userWord.setText("");
            accuracy.setText(String.valueOf(Math.round((counter * 1.0 / countAll) * 100))); // calculates accuracy
            programWord.setText(words.get(wordCounter));

            if (wordCounter + 1 < words.size()) { // To avoid index out of bounds
                secondProgramWord.setText(words.get(wordCounter + 1));
            } else {
                secondProgramWord.setText("");
            }

            wordCounter++;
        }
    }
}