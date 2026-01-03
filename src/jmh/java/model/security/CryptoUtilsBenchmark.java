package model.security;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class CryptoUtilsBenchmark {

    @Param({"16", "64", "256", "1024", "4096"})
    public int size;

    private SecretKey key;
    private String plaintext;
    private String encodedCiphertext; // input per decrypt

    @Setup(Level.Trial)
    public void setup() throws Exception {
        // Key AES (128 bit). Se il tuo progetto usa altro, dimmelo e lo allineiamo.
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        key = kg.generateKey();

        // plaintext deterministico di lunghezza 'size'
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) ('a' + (i % 26));
        }
        plaintext = new String(bytes, StandardCharsets.US_ASCII);

        // Prepara un ciphertext valido per il benchmark di decrypt
        encodedCiphertext = CryptoUtils.encrypt(key, plaintext);
    }

    @Benchmark
    public void encrypt_only(Blackhole bh) throws Exception {
        String out = CryptoUtils.encrypt(key, plaintext);
        bh.consume(out);
    }

    @Benchmark
    public void decrypt_only(Blackhole bh) throws Exception {
        String out = CryptoUtils.decrypt(key, encodedCiphertext);
        bh.consume(out);
    }

    @Benchmark
    public void roundtrip_encrypt_then_decrypt(Blackhole bh) throws Exception {
        String enc = CryptoUtils.encrypt(key, plaintext);
        String dec = CryptoUtils.decrypt(key, enc);
        bh.consume(dec);
    }
}
