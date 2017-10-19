package jp.ac.it_college.std.nakasone.tetris.util;

/**
 * メルセンヌ・ツイスタ
 * @see <a href="http://droid-game-samurai.blogspot.jp/2014/09/mtjava-class.html">ドロイドゲーム侍 &#65288;Androidゲームをバッサリ&#65289;</a>
 */
public class MT {
    public final static long Max = 0xffffffff;
    final static int MT_N = 624; // length of state vector
    final static int MT_M = 397; // a period parameter
    final static long MT_MATRIX_A = 0x9908B0DFL; // constant vector a

    long[] sv = new long[MT_N]; // state vector
    int N, M; // random value is computed from here

    public MT(long _s) {
        N = 0;
        M = MT_M;
        for (int i = 0; i < MT_N; i++)
            sv[i] = _s = ((1812433253L * (_s ^ (_s >> 30)) + (long) (i))) & 0xffffffffL;
    }

    public long next() {
        int P = N;
        if (++N == MT_N) N = 0;
        // move hi bit of u to hi bit of v
        sv[P] = sv[M] ^ (((sv[P] & 0x80000000L) | (sv[N] & ~(0x80000000L))) >> 1) ^ (((sv[N] & 1L) != 0) ? MT_MATRIX_A : 0);
        if (++M == MT_N) M = 0;
        // Tempering
        long y = sv[P];
        y ^= (y >> 11);
        y ^= (y << 7) & 0x9D2C5680L;
        y ^= (y << 15) & 0xEFC60000L;
        y ^= (y >> 18);
        return ((long) (y));
    }

    public long nextL(long _max) {
        return (next() % (_max + 1));
    }

    public long nextL(long _min, long _max) {
        long range = _max - _min + 1;
        return (next() % range + _min);
    }

    public int next(int _max) {
        return ((int) (next() % (_max + 1)));
    }

    public int next(int _min, int _max) {
        int range = _max - _min + 1;
        return ((int) (next() % range + _min));
    }

    public float next(float _max) {
        return ((float) (next()) * _max / 0x100000000L);
    }

    public float next(float _min, float _max) {
        float range = _max - _min;
        return ((float) (next()) * range / 0x100000000L + _min);
    }
}
