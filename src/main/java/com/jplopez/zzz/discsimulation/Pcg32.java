package com.jplopez.zzz.discsimulation;

import java.nio.ByteBuffer;

/** PCG XSH-RR 64/32, stream 54. Indices are zero-based after the PCG seeding sequence. */
final class Pcg32 {

  static final String ALGORITHM = "pcg32";
  private static final long MULTIPLIER = 6364136223846793005L;
  private static final long INCREMENT = (54L << 1) | 1;

  private Pcg32() {}

  static long draw(long seed, long index) {
    long state = (INCREMENT + seed) * MULTIPLIER + INCREMENT;
    // Jump ahead in O(log index) using composition of affine functions modulo 2^64.
    long multiplier = MULTIPLIER;
    long increment = INCREMENT;
    for (long remaining = index; remaining != 0; remaining >>>= 1) {
      if ((remaining & 1) != 0) {
        state = state * multiplier + increment;
      }
      increment *= multiplier + 1;
      multiplier *= multiplier;
    }
    int xorshifted = (int) (((state >>> 18) ^ state) >>> 27);
    return Integer.toUnsignedLong(Integer.rotateRight(xorshifted, (int) (state >>> 59)));
  }

  static long forkSeed(long seed, long draws, int toEvent) {
    byte[] input = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES)
        .putLong(seed).putLong(draws).putInt(toEvent).array();
    long derived = Integer.toUnsignedLong(ByteBuffer.wrap(DiscJson.sha256().digest(input)).getInt());
    return derived == seed ? (seed + 1) & DiscDocuments.MAX_SEED : derived;
  }
}
