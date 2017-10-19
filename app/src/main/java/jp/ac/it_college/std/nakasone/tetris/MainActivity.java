package jp.ac.it_college.std.nakasone.tetris;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_main);

        // ボードの初期化
        Bitmap blocks = BitmapFactory.decodeResource(
                getResources(), R.drawable.blocks);
        board = new Board(this, blocks);

        // ボードを画面に追加
        ((FrameLayout)findViewById(R.id.board_area)).addView(board);

        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        findViewById(R.id.drop).setOnClickListener(this);
        findViewById(R.id.rotate).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                board.left();
                break;
            case R.id.right:
                board.right();
                break;
            case R.id.drop:
                board.drop();
                break;
            case R.id.rotate:
                board.rotate();
                break;
        }
    }
}
