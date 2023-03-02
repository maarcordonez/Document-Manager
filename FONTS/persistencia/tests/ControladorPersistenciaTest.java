package persistencia.tests;

import domini.classes.IdxAutors;
import domini.utils.Pair;
import org.junit.Assert;
import org.junit.Test;
import persistencia.classes.ControladorPersistencia;
import persistencia.excepcions.ExceptionAPersitencia;

import java.io.IOException;
import java.util.ArrayList;

public class ControladorPersistenciaTest {
    /*
    @Test
    public void llegirStopWords() throws FileNotFoundException {
        ControladorPersistencia ctrPers = new ControladorPersistencia();
        String rebut = ctrPers.llegirStopWords();
        Assert.assertEquals("", rebut);
    }

     */
/*
    @Test
    public void saveIdxObres() throws ExceptionAPersitencia {
        ControladorPersistencia ctrPers = new ControladorPersistencia();
        TreeMap<String, TreeMap<String, LocalDateTime>> info = new TreeMap<>();
        Set<String> infoJoan = new TreeSet<>();
        Set<String> infoPere = new TreeSet<>();
        Set<String> infoJoana = new TreeSet<>();
        infoJoan.add("bon dia");
        infoJoan.add("bona nit");
        infoJoan.add("bona vesprada");
        infoJoan.add("bon mati");
        infoPere.add("bon mati");
        infoJoana.add("com estas");
        info.put("joan", infoJoan);
        info.put("pere", infoPere);
        info.put("joana", infoJoana);
        ctrPers.saveIdxObres(info);
    }

    @Test
    public void loadIdxObres() throws IOException, ClassNotFoundException {
        ControladorPersistencia ctrPers = new ControladorPersistencia();
        TreeMap<String, Set<String>> info = new TreeMap<>();
        Set<String> infoJoan = new TreeSet<>();
        Set<String> infoPere = new TreeSet<>();
        Set<String> infoJoana = new TreeSet<>();
        infoJoan.add("bon dia");
        infoJoan.add("bona nit");
        infoJoan.add("bona vesprada");
        infoJoan.add("bon mati");
        infoPere.add("bon mati");
        infoJoana.add("com estas");
        info.put("joan", infoJoan);
        info.put("pere", infoPere);
        info.put("joana", infoJoana);
        TreeMap<String, Set<String>> rebut = ctrPers.loadIdxObres();
        Assert.assertEquals(info, rebut);
    }


 */
    @Test
    public void saveIdxAutors() throws IOException, ExceptionAPersitencia {
        ControladorPersistencia ctrPers = new ControladorPersistencia();
        IdxAutors idx = new IdxAutors();
        idx.afegir("joan");
        idx.afegir("joana");
        idx.afegir("pere");
        idx.afegir("jordi");
        idx.afegir("luis");
        idx.afegir("monica");
        ArrayList<String> llista = idx.llistar();
        ctrPers.saveIdxAutors(llista);
    }

    @Test
    public void loadIdxAutors() throws IOException, ClassNotFoundException {
        ControladorPersistencia ctrPers = new ControladorPersistencia();
        ArrayList<String> expected = new ArrayList<>();
        expected.add("joan");
        expected.add("joana");
        expected.add("jordi");
        expected.add("luis");
        expected.add("monica");
        expected.add("pere");
        ArrayList<String> rebut = ctrPers.loadIdxAutors();
        Assert.assertEquals(expected, rebut);
    }

    @Test
    public void saveExpressionsBooleanes() throws IOException {
        ControladorPersistencia ctrPers = new ControladorPersistencia();
        ArrayList<Pair<String, String>> info = new ArrayList<Pair<String, String>>();
        Pair p1 = new Pair<>("hola", "adeu");
        Pair p2 = new Pair<>("uno", "dos");
        Pair p3 = new Pair<>("aqui", "alla");
        Pair p4 = new Pair<>("bo", "dolent");
        info.add(p1);
        info.add(p2);
        info.add(p3);
        info.add(p4);
        //ctrPers.saveExpressionsBooleanes(info);
    }

    @Test
    public void loadExpressionsBooleanes() throws IOException, ClassNotFoundException {
        ControladorPersistencia ctrPers = new ControladorPersistencia();
        ArrayList<Pair<String, String>> info = new ArrayList<Pair<String, String>>();
        Pair p1 = new Pair<>("hola", "adeu");
        Pair p2 = new Pair<>("uno", "dos");
        Pair p3 = new Pair<>("aqui", "alla");
        Pair p4 = new Pair<>("bo", "dolent");
        info.add(p1);
        info.add(p2);
        info.add(p3);
        info.add(p4);
        //ArrayList<Pair<String, String>> rebut = ctrPers.loadExpressionsBooleanes();
        //Assert.assertEquals(info, rebut);
    }
/*
    @Test
    public void saveEspaiVectorial() throws IOException {
        ControladorPersistencia ctrPers = new ControladorPersistencia();
        LinkedHashMap<String, Pair<Double, Double>> info = new LinkedHashMap<>();
        Pair p1 = new Pair<>("hola", "adeu");
        Pair p2 = new Pair<>("uno", "dos");
        Pair p3 = new Pair<>("aqui", "alla");
        Pair p4 = new Pair<>("bo", "dolent");
        info.put("prova1", p1);
        info.put("prova2", p2);
        info.put("prova3", p3);
        info.put("prova4", p4);
        ctrPers.saveEspaiVectorial(info);
    }

    @Test
    public void loadEspaiVectorial() throws IOException, ClassNotFoundException {
        ControladorPersistencia ctrPers = new ControladorPersistencia();
        LinkedHashMap<String, Pair<Double, Double>> info = new LinkedHashMap<>();
        Pair p1 = new Pair<>("hola", "adeu");
        Pair p2 = new Pair<>("uno", "dos");
        Pair p3 = new Pair<>("aqui", "alla");
        Pair p4 = new Pair<>("bo", "dolent");
        info.put("prova1", p1);
        info.put("prova2", p2);
        info.put("prova3", p3);
        info.put("prova4", p4);
        LinkedHashMap<String, Pair<Double, Double>> rebut = ctrPers.loadEspaiVectorial();
        Assert.assertEquals(info, rebut);
    }

 */
}
