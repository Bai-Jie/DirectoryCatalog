package gq.baijie.catalog.client.cli.converter;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.BaseConverter;

import java.util.Locale;

import gq.baijie.catalog.entity.Hash;

public class AlgorithmConverter extends BaseConverter<Hash.Algorithm> {

    public AlgorithmConverter(String optionName) {
        super(optionName);
    }

    @Override
    public Hash.Algorithm convert(final String value) {
        switch (value.toUpperCase(Locale.US)) {
            case "MD5":
                return Hash.Algorithm.MD5;
            case "SHA1":
            case "SHA-1":
                return Hash.Algorithm.SHA1;
            case "SHA256":
            case "SHA-256":
                return Hash.Algorithm.SHA256;
            default:
                throw new ParameterException(getErrorString(value, "a Algorithm"),
                        new IllegalArgumentException("Unknown Hash Algorithm:" + value));
        }
    }

}
