package model;

import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;
import model.maglietta.MagliettaOrdine;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class CarrelloModelBenchmark {

    @Param({"10", "100", "1000"})
    public int cartSize;

    @Param({"S", "M", "L", "XL"})
    public String taglia;

    private CarrelloModel carrello;
    private int existingId;
    private int missingId;

    private StubMagliettaDAO stubDao;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        stubDao = new StubMagliettaDAO();

        carrello = new CarrelloModel(stubDao);

        ArrayList<MagliettaOrdine> list = new ArrayList<>(cartSize);
        for (int i = 1; i <= cartSize; i++) {
            MagliettaBean b = stubDao.beanFor(i);
            list.add(new MagliettaOrdine(b, taglia));
        }
        carrello.setCarrello(list);

        existingId = Math.max(1, cartSize / 2); 
        missingId = cartSize + 10_000;          
    }

    /**
     * Caso "hit": stesso ID/taglia già presente -> loop + incrementaQuantita()
     */
    @Benchmark
    public void aggiungi_hit_existing(final Blackhole bh) {
        carrello.aggiungi(existingId, taglia);
        bh.consume(carrello);
    }

    /**
     * Caso "miss": elemento assente -> loop + doRetrieveByKey (stub) + add(new MagliettaOrdine)
     * N.B.: rimuoviamo dopo per non far crescere la lista all'infinito
     */
    @Benchmark
    public void aggiungi_miss_insert(final Blackhole bh) {
        carrello.aggiungi(missingId, taglia);
        bh.consume(carrello);
        carrello.rimuovi(missingId, taglia);
    }

    /**
     * setQuantita su elemento presente: loop + setQuantita
     */
    @Benchmark
    public void setQuantita_update(final Blackhole bh) {
        int q = ThreadLocalRandom.current().nextInt(1, 5);
        carrello.setQuantita(existingId, q, taglia);
        bh.consume(q);
    }

    /**
     * removeIf: rimuove un elemento e poi lo reinserisce per mantenere dimensione stabile
     */
    @Benchmark
    public void rimuovi_removeIf_then_readd(final Blackhole bh) {
        carrello.rimuovi(existingId, taglia);
        bh.consume(carrello);

        carrello.aggiungi(existingId, taglia);
    }

    /**
     * Stub del DAO che:
     * - non chiama MagliettaDAO() 
     * - overridea doRetrieveByKey(Integer)
     */
    private static final class StubMagliettaDAO extends MagliettaDAO {

        StubMagliettaDAO() {
            super((DataSource) null);
        }

        @Override
        public synchronized MagliettaBean doRetrieveByKey(final Integer code) throws SQLException {
            return beanFor(code);
        }

        MagliettaBean beanFor(final int id) {
            MagliettaBean b = new MagliettaBean();
            b.setID(id);
            return b;
        }
    }
}
