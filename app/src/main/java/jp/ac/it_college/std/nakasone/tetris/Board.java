package jp.ac.it_college.std.nakasone.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.atomic.AtomicBoolean;

import jp.ac.it_college.std.nakasone.tetris.util.MT;

/**
 * テトリスのボードをあらわすView
 */

public class Board extends SurfaceView implements SurfaceHolder.Callback {
    private final Bitmap BLOCKS;
    private final int BLOCK_SIZE;
    private final Rect[] BLOCK_SRC = new Rect[8];
    private final Rect block_dst = new Rect();
    private int[][] block_list = new int[10][23];
    private DrawThread thread;
    private Tetromino currentTetromino;
    private MT rand = new MT(System.currentTimeMillis());
    private int inputFlag = 0x00;
    private int dropFrames = 0;

    public Board(Context context, Bitmap blocks) {
        super(context);

        getHolder().addCallback(this);

        BLOCKS = blocks;
        BLOCK_SIZE = BLOCKS.getWidth();

        for (int i = 0; i < BLOCK_SRC.length; i++) {
            BLOCK_SRC[i] = new Rect(
                    0,                      // left
                    BLOCK_SIZE * i,         // top
                    BLOCK_SIZE,             // right
                    BLOCK_SIZE * (i + 1));  // bottom
        }

        // debug
        currentTetromino = new Tetromino(5, 3,
                rand.next(1, 7),
                rand.next(3));
        putTetromino(currentTetromino);
    }

    public boolean putTetromino(Tetromino tetromino) {
        return putTetromino(tetromino, false);
    }

    public boolean putTetromino(Tetromino t, boolean action) {
        if (t.getX() < 0 || t.getX() >= 10 || t.getY() >= 23) {
            return false;
        }
        if (block_list[t.getX()][t.getY()] != 0) {
            return false;
        }
        if (action) {
            block_list[t.getX()][t.getY()] = t.getType();
        }

        int r = t.getRotate() % Tetromino.blocks[t.getType()].getMaxRotate();
        for (int i = 0; i < 3; i++) {
            int dx = Tetromino.blocks[t.getType()].get(i)[0];
            int dy = Tetromino.blocks[t.getType()].get(i)[1];

            for (int j = 0; j < r; j++) {
                int nx = dx;
                dx = dy;
                dy = -nx;
            }
            if (t.getX() + dx < 0 || t.getX() + dx >= 10
                    || t.getY() + dy < 0 || t.getY() + dy >= 23) {
                return false;
            }
            if (block_list[t.getX() + dx][t.getY() + dy] != 0) {
                return false;
            }
            if (action) {
                block_list[t.getX() + dx][t.getY() + dy] = t.getType();
            }
        }
        if (!action) {
            putTetromino(t, true);
        }
        return true;
    }

    public void deleteTetromino(Tetromino t) {
        block_list[t.getX()][t.getY()] = 0;

        int r = t.getRotate() % Tetromino.blocks[t.getType()].getMaxRotate();
        for (int i = 0; i < 3; i++) {
            int dx = Tetromino.blocks[t.getType()].get(i)[0];
            int dy = Tetromino.blocks[t.getType()].get(i)[1];

            for (int j = 0; j < r; j++) {
                int nx = dx;
                dx = dy;
                dy = -nx;
            }
            block_list[t.getX() + dx][t.getY() + dy] = 0;
        }
    }

    public void left() {
        inputFlag |= 0x01;
    }

    public void right() {
        inputFlag |= 0x02;
    }

    public void drop() {
        inputFlag |= 0x04;
    }

    public void rotate() {
        inputFlag |= 0x08;
    }

    private void move() {
        // ひだり押された
        if ((inputFlag & 0x01) != 0) {
            currentTetromino.moveLeft();
        }
        // みぎ押された
        if ((inputFlag & 0x02) != 0) {
            currentTetromino.moveRight();
        }
        // ドロップ押された
        if ((inputFlag & 0x04) != 0) {
            currentTetromino.moveBottom();
        }
        // かいてん押された
        if ((inputFlag & 0x08) != 0) {
            currentTetromino.moveRotate();
        }
    }

    private void undo() {
        // ひだり押された
        if ((inputFlag & 0x01) != 0) {
            currentTetromino.moveRight();
        }
        // みぎ押された
        if ((inputFlag & 0x02) != 0) {
            currentTetromino.moveLeft();
        }
        // ドロップ押された
        if ((inputFlag & 0x04) != 0) {
            currentTetromino.moveTop();
        }
        // かいてん押された
        if ((inputFlag & 0x08) != 0) {
            currentTetromino.moveCounterRotate();
        }
    }

    private void startDrawThread() {
        stopDrawThread();
        thread = new DrawThread();
        thread.start();
    }

    private void stopDrawThread() {
        if (thread != null) {
            thread.finish();
            thread = null;
        }
    }

    private void nextTetromino() {

    }

    private void drawBoard(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 23; y++) {
                if (block_list[x][y] == 0) {
                    continue;
                }
                block_dst.set(
                        BLOCK_SIZE * x,         // left
                        BLOCK_SIZE * (y - 3),   // top
                        BLOCK_SIZE * (x + 1),   // right
                        BLOCK_SIZE * (y - 2));  // bottom
                canvas.drawBitmap(BLOCKS,
                        BLOCK_SRC[block_list[x][y]],
                        block_dst, null);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDrawThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
    }

    private class DrawThread extends Thread {
        private static final long DRAW_INTERVAL = 1000 / 60;
        private final AtomicBoolean isFinished = new AtomicBoolean();
        private long frames = 0;

        public void finish() {
            isFinished.set(true);
        }

        @Override
        public void run() {
            SurfaceHolder holder = getHolder();

            while (!isFinished.get()) {
                if (holder.isCreating()) {
                    continue;
                }

                if (++dropFrames % 60 == 0) {
                    inputFlag |= 0x04;
                }

                deleteTetromino(currentTetromino);
                move();
                if (!putTetromino(currentTetromino)) {
                    undo();
                    putTetromino(currentTetromino);
                    if ((inputFlag & 0x04) != 0) {
                        nextTetromino();
                    }
                }
                // フラグリセット
                inputFlag &= ~0x0F;

                // ハードウェアアクセラレーションが有効なCanvasを取得する
                Canvas canvas = holder.getSurface().lockHardwareCanvas();
                if (canvas != null) {
                    drawBoard(canvas);

                    holder.getSurface().unlockCanvasAndPost(canvas);
                }

                synchronized (this) {
                    try {
                        wait(DRAW_INTERVAL);
                    } catch (InterruptedException e) {
                        Log.i("DrawThread", e.getMessage(), e);
                    }
                }
            }
        }
    }
}
