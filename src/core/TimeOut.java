package core;

import java.util.Timer;
import java.util.TimerTask;

import server.GameManager;

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
                    break;
                case "Finale":
                	gameManager.finDeFinale();
                	break;
            }
            timer.cancel();
        }
    }
    
    public Timer getTimer() {
    	return timer;
    }
}