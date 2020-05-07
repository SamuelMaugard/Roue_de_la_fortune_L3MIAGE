package core;

import serveur.GameManager;

import java.util.Timer;
import java.util.TimerTask;

public class TimeOut {
    Timer timer;

    public TimeOut(int seconds, GameManager gm, String func) {
        timer = new Timer();
        timer.schedule(new RemindTask(gm, func), seconds*1000);
    }

    class RemindTask extends TimerTask {
        GameManager gameManager;
        String function;
        public RemindTask(GameManager gm, String func) {
            gameManager = gm;
            function = func;
        }
        public void run() {
            switch (function) {
                case "MancheRapide":
                    gameManager.updateMancheRapide();
            }
            timer.cancel();
        }
    }
}