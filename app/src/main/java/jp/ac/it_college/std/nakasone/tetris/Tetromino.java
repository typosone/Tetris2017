package jp.ac.it_college.std.nakasone.tetris;

/**
 * テトロミノの座標とかのやつ
 */

public class Tetromino {
    public static final Block[] blocks = {
            new Block(1, new int[][]{{0, 0}, {0, 0}, {0, 0}}), // null
            new Block(2, new int[][]{{-2, 0}, {-1, 0}, {1, 0}}), // I
            new Block(1, new int[][]{{-1, -1}, {-1, 0}, {0, -1}}), // O
            new Block(2, new int[][]{{-1, 1}, {0, 1}, {1, 0}}), // S
            new Block(2, new int[][]{{-1, 0}, {0, 1}, {1, 1}}), // Z
            new Block(4, new int[][]{{-1, -1}, {-1, 0}, {1, 0}}), // J
            new Block(4, new int[][]{{-1, 0}, {1, 0}, {1, -1}}), // L
            new Block(4, new int[][]{{-1, 0}, {0, -1}, {1, 0}}), // T
    };
    private int x;
    private int y;
    private int type;
    private int rotate;

    public Tetromino(int x, int y, int type, int rotate) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.rotate = rotate;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public int getRotate() {
        return rotate;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public void moveBottom() {
        y++;
    }

    public void moveTop() {
        y--;
    }

    public void moveRotate() {
        rotate++;
    }

    public void moveCounterRotate() {
        rotate--;
    }

    @Override
    public String toString() {
        return "Tetromino@ type:" + type + " rotate:" + rotate;
    }

    public static class Block {
        private final int maxRotate;
        private final int position[][] = new int[3][2];

        public int[] get(int pos) {
            return position[pos];
        }

        public int getMaxRotate() {
            return maxRotate;
        }

        public Block(int rotate, int[][] coordinates) {
            this.maxRotate = rotate;
            for (int i = 0; i < 3; i++) {
                position[i][0] = coordinates[i][0];
                position[i][1] = coordinates[i][1];
            }
        }
    }
}
