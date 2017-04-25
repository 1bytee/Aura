package ambosencoding.aura.tasks;

import ambosencoding.aura.Aura;
import ambosencoding.aura.utils.GameState;

public class IngameTask extends AbstractTask {

    int cooldown = 10 * 60;

    @Override
    public void run() {
        if (cooldown != 0 && Aura.INGAME.size() > 1) {
            if (cooldown == 10 * 60 || cooldown == 5 * 60) {
                broadcast("Spiel endet in §e" + cooldown / 60 + " §7Minuten.");
            } else if (cooldown == 120 || cooldown == 60) {
                broadcast("Spiel endet in in §e" + cooldown / 60 + " §7Minute" + (cooldown / 60 == 1 ? "." : "n."));
            } else if (cooldown == 30 || cooldown == 20 || cooldown == 10 || cooldown <= 5 && cooldown >= 1) {
                broadcast("Spiel endet in in §e" + cooldown / 60 + " §7Sekunden" + (cooldown / 60 == 1 ? "." : "n."));
            }
            cooldown--;
        } else {
            RestartTask.execute();
            cancel();
            Aura.STATE = GameState.ENDING;
            Aura.CURRENT_TASK = null;
        }
    }

}
