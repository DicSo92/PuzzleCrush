package com.game.puzzlecrush;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;

public class PausePopup extends Dialog {
    private Button btn_continue, btn_backMenu;

    public PausePopup(Activity activity) {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.pause_popup);

        this.btn_continue = findViewById(R.id.pause_continue);
        this.btn_backMenu = findViewById(R.id.pause_backMenu);
    }

    public Button getBtn_continue () {
        return btn_continue;
    }
    public Button getBtn_backMenu () {
        return btn_backMenu;
    }

    public void Build(){
        show();
    }
}
