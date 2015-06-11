package gq.baijie.catalog.entity;

import java.util.Arrays;

import javax.annotation.Nonnull;

public class Hash {

    @Nonnull
    private final Algorithm algorithm;

    @Nonnull
    private final byte[] value;

    public Hash(@Nonnull byte[] value, @Nonnull Algorithm algorithm) {
        if (algorithm.bitsLength != value.length * 8) {
            throw new IllegalArgumentException("value's length isn't consistent with algorithm");
        }
        this.algorithm = algorithm;
        this.value = value.clone();
    }

    public Hash(@Nonnull byte[] value) {
        Algorithm algorithm = Algorithm.probeHashAlgorithm(value);
        if (algorithm == null) {
            throw new IllegalArgumentException("Unknown algorithm");
        }
        this.algorithm = algorithm;
        this.value = value.clone();
    }


    @Nonnull
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    @Nonnull
    public byte[] getValue() {
        return value.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj.getClass() != getClass()) {
            return false;
        } else {
            Hash otherHash = (Hash) obj;
            return algorithm == otherHash.algorithm && Arrays.equals(value, otherHash.value);
        }
    }

    public enum Algorithm {
        MD5(128),
        SHA1(160),
        SHA256(256);

        private final int bitsLength;

        private Algorithm(int bitsLength) {
            this.bitsLength = bitsLength;
        }

        public String toString() {
            switch (this) {
                case MD5:
                    return "MD5";
                case SHA1:
                    return "SHA-1";
                case SHA256:
                    return "SHA-256";
            }
            throw new Error("this method isn't up to date");
        }

        public static Algorithm probeHashAlgorithm(int bitsLength) {
            if (bitsLength < 0) {
                throw new IllegalArgumentException("bitsLength shouldn't less than 0");
            }
            for (Algorithm algorithm : values()) {
                if (algorithm.bitsLength == bitsLength) {
                    return algorithm;
                }
            }
            return null;
        }

        public static Algorithm probeHashAlgorithm(@Nonnull byte[] hashValue) {
            return probeHashAlgorithm(hashValue.length * 8);
        }

    }

}
