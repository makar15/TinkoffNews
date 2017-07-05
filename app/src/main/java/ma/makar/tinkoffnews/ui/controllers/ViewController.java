package ma.makar.tinkoffnews.ui.controllers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public interface ViewController {

    void attach(View view, @Nullable Bundle args);
}
