package gq.baijie.catalog.entity;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getAlgorithm())
                .append(getValue())
                .toHashCode();
    }

    public enum Algorithm {
        MD5(128, "MD5"),
        SHA1(160, "SHA-1"),
        SHA256(256, "SHA-256");

        private final int bitsLength;

        @Nonnull
        private final String string;

        private Algorithm(int bitsLength, @Nonnull String string) {
            this.bitsLength = bitsLength;
            this.string = string;
        }

        @Nonnull
        public String toString() {
            return string;
        }

        @Nullable
        public static Algorithm fromString(@Nullable String algorithm) {
            if (algorithm == null) {
                return null;
            }
            algorithm = algorithm.toUpperCase(Locale.US);
            for (Algorithm algorithmEnum : values()) {
                if (algorithmEnum.toString().equals(algorithm)) {
                    return algorithmEnum;
                }
            }
            throw new IllegalArgumentException("Unknown algorithm:" + algorithm);
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
